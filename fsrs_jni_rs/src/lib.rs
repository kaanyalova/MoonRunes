use core::panic;
use std::collections::HashMap;
use std::iter;
use std::mem::transmute;
use std::sync::Mutex;

use anyhow::Result;
use anyhow::anyhow;
use chrono::Date;
use chrono::Duration;
use chrono::OutOfRange;
use chrono::TimeZone;
use chrono::{DateTime, Utc};
use fsrs::ComputeParametersInput;
use fsrs::FSRS;
use fsrs::FSRSReview;
use fsrs::MemoryState as FSRSMemoryState;

use fsrs::FSRSItem;
use itertools::Itertools;
use jni::EnvUnowned;
use jni::errors::Error;
use jni::errors::ThrowRuntimeExAndDefault;
use jni::jni_mangle;
use jni::objects::JByteArray;
use jni::objects::JClass;
use jni::objects::JString;
use jni::sys::jstring;
use rand::{Rng, rngs::ThreadRng};
use rkyv::Archive;
use rkyv::vec;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, rkyv::Serialize, rkyv::Deserialize, Archive, Clone)]
// this doesn't need the card id,
pub struct Review {
    pub card_id: i64,
    pub rating: u32,
    pub timestamp: i64,
}

pub struct FSRSContext {
    fsrs: FSRS,
    desired_retention: f32,
    cards: Vec<Card>,
    rng: ThreadRng,
    reviews: HashMap<i64, Vec<Review>>, // log of all reviews, used for the fancy optimization stuff
    optimized_data: Vec<f32>,
}

#[derive(Debug, rkyv::Serialize, rkyv::Deserialize, Archive)]
struct SerializableFSRSContext {
    desired_retention: f32,
    cards: Vec<Card>,
    reviews: HashMap<i64, Vec<Review>>,
    optimized_data: Vec<f32>,
}

impl FSRSContext {
    fn to_serializable(&self) -> SerializableFSRSContext {
        SerializableFSRSContext {
            desired_retention: self.desired_retention,
            cards: self.cards.clone(),
            reviews: self.reviews.clone(),
            optimized_data: self.optimized_data.clone(),
        }
    }

    fn from_serializable(state: SerializableFSRSContext) -> Self {
        Self {
            fsrs: FSRS::new(Some(&state.optimized_data)).unwrap(),
            desired_retention: state.desired_retention,
            cards: state.cards,
            rng: rand::rng(),
            reviews: HashMap::new(),
            optimized_data: state.optimized_data,
        }
    }
}

#[derive(Debug, Clone, rkyv::Serialize, rkyv::Deserialize, Archive, Serialize, Deserialize)]
pub struct MemoryState {
    pub stability: f32,
    pub difficulty: f32,
}

impl From<MemoryState> for FSRSMemoryState {
    fn from(val: MemoryState) -> Self {
        FSRSMemoryState {
            stability: val.stability,
            difficulty: val.difficulty,
        }
    }
}

impl From<FSRSMemoryState> for MemoryState {
    fn from(val: FSRSMemoryState) -> Self {
        MemoryState {
            stability: val.stability,
            difficulty: val.difficulty,
        }
    }
}

#[derive(Debug, Clone, Archive, rkyv::Serialize, rkyv::Deserialize, Serialize, Deserialize)]
struct Card {
    id: i64,
    due: i64,
    memory_state: Option<MemoryState>,
    scheduled_days: u32,
    last_review: Option<i64>,
}

impl Card {
    fn new() -> Self {
        Self {
            due: Utc::now().timestamp_millis(),
            memory_state: None,
            scheduled_days: 0,
            last_review: None,
            id: 0,
        }
    }

    fn id(&self) -> i64 {
        self.id
    }
}

#[derive(Debug, Serialize, Deserialize)]
enum ReviewType {
    Again,
    Hard,
    Good,
    Easy,
}

#[derive(Debug, Serialize, Deserialize)]
struct CardReviewIntervals {
    again_interval: f32,
    hard_interval: f32,
    good_interval: f32,
    easy_interval: f32,
}

impl TryFrom<i32> for ReviewType {
    type Error = anyhow::Error;

    fn try_from(value: i32) -> std::result::Result<Self, Self::Error> {
        match value {
            0 => Ok(ReviewType::Again),
            1 => Ok(ReviewType::Hard),
            2 => Ok(ReviewType::Good),
            3 => Ok(ReviewType::Easy),
            _ => Err(anyhow!("Invalid review type")),
        }
    }
}

impl FSRSContext {
    fn new(retention: f32) -> Result<Self> {
        Ok(Self {
            fsrs: FSRS::new(None).unwrap(),
            desired_retention: retention,
            rng: rand::rng(),
            cards: vec![],
            reviews: HashMap::new(),
            optimized_data: vec![],
        })
    }

    fn create_card(&mut self, id: i64) {
        let mut card = Card::new();
        card.id = id;
        self.cards.push(card);
    }

    fn dump_state(&self) -> Vec<u8> {
        let serializable = self.to_serializable();
        rkyv::to_bytes::<rkyv::rancor::Error>(&serializable)
            .unwrap()
            .to_vec()
    }

    fn from_state(bytes: Vec<u8>) -> Self {
        let serializable: SerializableFSRSContext =
            rkyv::from_bytes::<SerializableFSRSContext, rkyv::rancor::Error>(&bytes).unwrap();
        Self::from_serializable(serializable)
    }

    fn get_due_cards(&self) -> Vec<Card> {
        let now = Utc::now().timestamp_millis();
        self.cards
            .iter()
            .map(|c| c.to_owned())
            .filter(|c| c.due < now)
            .sorted_by_key(|c| c.due)
            .collect()
    }

    fn optimize(&mut self) -> Result<()> {
        let training_set = Self::prepare_training_set(&self.reviews);
        let trained = self.fsrs.compute_parameters(ComputeParametersInput {
            train_set: training_set,
            ..Default::default() // it is possible to show progress, this has a arc for that, would have preferred a callback
        })?;

        self.fsrs = FSRS::new(Some(&trained))?;
        self.optimized_data = trained;
        Ok(())
    }

    fn review_card(&mut self, card_id: i64, review_type: ReviewType) -> Result<()> {
        let card = self
            .cards
            .iter_mut()
            .find(|c| c.id == card_id)
            .ok_or(anyhow!("card not found"))?;

        let memory_state = card.memory_state.clone().map(Into::into);

        let elapsed_days = match card.last_review {
            Some(timestamp) => {
                let last_review_date = DateTime::from_timestamp_millis(timestamp)
                    .ok_or(anyhow!("cannot parse the time of the review"))?;
                (Utc::now() - last_review_date).num_days() as u32
            }
            None => 0,
        };

        let next_states =
            self.fsrs
                .next_states(memory_state, self.desired_retention, elapsed_days)?;

        let new_state = match review_type {
            ReviewType::Again => next_states.again,
            ReviewType::Hard => next_states.hard,
            ReviewType::Good => next_states.good,
            ReviewType::Easy => next_states.easy,
        };

        let interval = new_state.interval.round().max(1.0) as u32;

        card.memory_state = Some(new_state.memory.into());
        card.scheduled_days = interval;

        let last_review = Utc::now();
        let last_review_timestamp = last_review.timestamp_millis();

        card.last_review = Some(last_review_timestamp);
        card.due = (last_review + Duration::days(interval as i64)).timestamp_millis();

        Ok(())
    }

    /// groups the review history by cards, strips the card ids adds the time between the reviews of the same card
    /// it assumes that each card's reviews are sorted by timestamps (which would be if its inserted by the user)
    fn prepare_training_set(reviews: &HashMap<i64, Vec<Review>>) -> Vec<FSRSItem> {
        let grouped: Vec<Vec<FSRSReview>> = reviews
            .iter()
            .filter(|(_, e)| !e.is_empty()) // won't panic
            .map(|(_, e)| {
                let first = e.first().unwrap();
                iter::once(first)
                    .chain(e)
                    .tuple_windows()
                    .map(|(prev, curr)| {
                        let then = DateTime::from_timestamp(prev.timestamp, 0).unwrap();
                        let current = DateTime::from_timestamp(curr.timestamp, 0).unwrap();
                        let days = (current.date_naive() - then.date_naive()).num_days() as u32;
                        FSRSReview {
                            rating: curr.rating,
                            delta_t: days,
                        }
                    })
                    .collect()
            })
            .collect();

        grouped
            .into_iter()
            .map(|g| FSRSItem { reviews: g })
            .collect()
    }

    fn delete_card(&mut self, card_id: i64) -> Result<()> {
        let index = self
            .cards
            .iter()
            .position(|c| c.id == card_id)
            .ok_or(anyhow!("card with id {} not found", card_id))?;
        self.cards.remove(index);
        self.reviews.remove(&card_id);
        Ok(())
    }

    fn get_review_info_for_card(&self, card_id: i64) -> Result<CardReviewIntervals> {
        let card = self.cards.iter().find(|c| c.id == card_id);
        if let Some(card) = card {
            let elapsed_days = match card.last_review {
                Some(timestamp) => {
                    let last_review_date = DateTime::from_timestamp_millis(timestamp)
                        .ok_or(anyhow!("cannot parse the time of the review"))?;
                    (Utc::now() - last_review_date).num_days() as u32
                }
                None => 0,
            };

            let memory_state = card.memory_state.clone().map(Into::into);

            let states =
                self.fsrs
                    .next_states(memory_state, self.desired_retention, elapsed_days)?;

            let review_info = CardReviewIntervals {
                again_interval: states.again.interval,
                hard_interval: states.hard.interval,
                good_interval: states.good.interval,
                easy_interval: states.easy.interval,
            };

            Ok(review_info)
        } else {
            Err(anyhow!(format!("card with id {} not found", card_id)))
        }
    }
}
#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
/// returns a "pointer" to the context
pub extern "system" fn new_fsrs(mut unowned_env: EnvUnowned<'_>, _class: JClass<'_>) -> i64 {
    let _ = unowned_env;
    let fsrs = FSRSContext::new(0.9).unwrap();
    let boxed = Box::new(Mutex::new(fsrs));
    let raw = Box::into_raw(boxed);
    raw as i64
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn free_fsrs(
    mut unowned_env: EnvUnowned<'_>,
    _class: JClass<'_>,
    ctx_ptr: i64,
) {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;

    let _ = unsafe {
        // just in case
        if ctx_ptr.is_null() {
            println!("trying to free null pointer for fsrs context, skipping");
            return;
        }

        Box::from_raw(ctx_ptr)
    };
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn create_card(
    mut unowned_env: EnvUnowned<'_>,
    _class: JClass<'_>,
    ctx_ptr: i64,
    id: i64,
) {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
    let mutex = unsafe { &*ctx_ptr };
    let mut ctx = mutex.lock().unwrap();
    ctx.create_card(id);
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn get_due_cards_as_json<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass<'caller>,
    ctx_ptr: i64,
) -> JString<'caller> {
    let outcome = unowned_env.with_env(|env| -> Result<_, Error> {
        let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
        let mutex = unsafe { &*ctx_ptr };
        let ctx = mutex.lock().unwrap();
        let cards = ctx.get_due_cards();
        let json = serde_json::to_string(&cards).unwrap();
        JString::from_str(env, json)
    });
    outcome.resolve::<ThrowRuntimeExAndDefault>()
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn optimize(mut unowned_env: EnvUnowned<'_>, _class: JClass<'_>, ctx_ptr: i64) {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
    let mutex = unsafe { &*ctx_ptr };
    let mut ctx = mutex.lock().unwrap();
    ctx.optimize().unwrap()
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn review_card(
    mut unowned_env: EnvUnowned<'_>,
    _class: JClass<'_>,
    ctx_ptr: i64,
    card_id: i32,
    review_type: i32, // maps to ReviewType
) {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
    let mutex = unsafe { &*ctx_ptr };
    let mut ctx = mutex.lock().unwrap();

    ctx.review_card(card_id as i64, ReviewType::try_from(review_type).unwrap())
        .unwrap();
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn get_review_info_for_card<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass<'_>,
    ctx_ptr: i64,
    card_id: i32,
) -> JString<'caller> {
    let outcome = unowned_env.with_env(|env| -> Result<_, Error> {
        let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
        let mutex = unsafe { &*ctx_ptr };
        let ctx = mutex.lock().unwrap();
        let review_info = ctx.get_review_info_for_card(card_id as i64).unwrap();
        let json = serde_json::to_string(&review_info).unwrap();
        let review_info = env.new_string(json).unwrap();
        Ok(review_info)
    });

    outcome.resolve::<ThrowRuntimeExAndDefault>()
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn dump_state<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass<'_>,
    ctx_ptr: i64,
) -> JByteArray<'caller> {
    let outcome = unowned_env.with_env(|env| -> Result<_, Error> {
        let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
        let mutex = unsafe { &*ctx_ptr };
        let ctx = mutex.lock().unwrap();
        let state = ctx.dump_state();
        let state = env.byte_array_from_slice(&state).unwrap();
        Ok(state)
    });

    outcome.resolve::<ThrowRuntimeExAndDefault>()
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn new_from_state<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass<'_>,
    state: JByteArray<'caller>,
) -> i64 {
    let outcome = unowned_env.with_env(|env| -> Result<_, Error> {
        let bytes = env.convert_byte_array(state).unwrap();
        let ctx = FSRSContext::from_state(bytes);
        let boxed = Box::new(Mutex::new(ctx));
        let raw = Box::into_raw(boxed);
        Ok(raw as i64)
    });
    outcome.resolve::<ThrowRuntimeExAndDefault>()
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn does_card_with_id_exist(
    mut unowned_env: EnvUnowned<'_>,
    _class: JClass<'_>,
    ctx_ptr: i64,
    id: i64,
) -> bool {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
    let mutex = unsafe { &*ctx_ptr };
    let ctx = mutex.lock().unwrap();
    ctx.cards.iter().any(|c| c.id == id)
}

#[jni_mangle("com.kaanb.fsrs_jni.FsrsJni")]
pub extern "system" fn delete_card(
    mut unowned_env: EnvUnowned<'_>,
    _class: JClass<'_>,
    ctx_ptr: i64,
    card_id: i64,
) {
    let _ = unowned_env;
    let ctx_ptr = ctx_ptr as *mut Mutex<FSRSContext>;
    let mutex = unsafe { &*ctx_ptr };
    let mut ctx = mutex.lock().unwrap();
    ctx.delete_card(card_id).unwrap();
}
