use std::collections::HashMap;
use std::iter;
use std::mem::transmute;

use anyhow::Ok;
use anyhow::Result;
use chrono::Date;
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
use jni::objects::JClass;
use jni::objects::JString;
use jni::sys::jstring;
use rand::{Rng, rngs::ThreadRng};
use rkyv::Archive;
use rkyv::vec;
use serde::Deserialize;
use serde::Serialize;

mod fsrs_context;

#[derive(Debug, rkyv::Serialize, rkyv::Deserialize, Archive)]
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
    fn to_serializable(self) -> SerializableFSRSContext {
        SerializableFSRSContext {
            desired_retention: self.desired_retention,
            cards: self.cards,
            reviews: self.reviews,
            optimized_data: self.optimized_data,
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

enum ReviewType {
    Again = 0,
    Hard = 1,
    Good = 2,
    Easy = 3,
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

    fn push_card(&mut self) -> i64 {
        let card = Card::new();
        let id = card.id;
        self.cards.push(card);
        id
    }

    fn dump_state(self) -> Vec<u8> {
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

    fn get_due_cards(&self) -> Card {}

    fn review_card(&self, card_id: i64) {
        let card = self.cards.iter().find(|c| c.id == card_id).unwrap();

        let memory_state = card.memory_state.clone().unwrap();

        let last_review = DateTime::from_timestamp_millis(card.last_review.unwrap()).unwrap();
        let elapsed_days = (Utc::now() - last_review).num_days();

        let next_states = self.fsrs.next_states(
            Some(memory_state.into()),
            self.desired_retention,
            elapsed_days as u32,
        );

        let hard = next_states.unwrap().hard;
    }

    /// groups the review history by cards, strips the card ids adds the time between the reviews of the same card
    /// it assumes that each card's reviews are sorted by timestamps (which would be if its inserted by the user)
    fn prepare_training_set(reviews: &HashMap<i64, Vec<Review>>) -> Vec<FSRSItem> {
        let grouped: Vec<Vec<FSRSReview>> = reviews
            .iter()
            .filter(|(_, e)| !e.is_empty()) // won't panic
            .map(|(_, e)| {
                let first = e.first().unwrap().clone();
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
}
#[jni_mangle("com.kaanb.MoonRunes")]
/// returns a "pointer" to the context
pub extern "system" fn new_fsrs() -> i64 {
    let fsrs = FSRSContext::new(0.9).unwrap();
    let boxed = Box::new(fsrs);
    let raw = Box::into_raw(boxed);
    raw as i64
}

#[jni_mangle("com.kaanb.MoonRunes")]
pub extern "system" fn free_fsrs(ctx_ptr: i64) {
    let ctx_ptr = ctx_ptr as *mut FSRSContext;

    let _ = unsafe { Box::from_raw(ctx_ptr) };
}

#[jni_mangle("com.kaanb.MoonRunes")]
pub extern "system" fn push_card(ctx_ptr: i64) -> i64 {
    let ctx_ptr = ctx_ptr as *mut FSRSContext;
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.push_card()
}

#[jni_mangle("com.kaanb.MoonRunes")]
pub extern "system" fn get_due_cards_as_json<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass<'caller>,
    ctx_ptr: i64,
) -> JString<'caller> {
    let outcome = unowned_env.with_env(|env| -> Result<_, Error> {
        let ctx_ptr = ctx_ptr as *mut FSRSContext;
        let ctx = unsafe { &mut *ctx_ptr };
        let cards = ctx.get_due_cards();
        let json = serde_json::to_string(&cards).unwrap();
        JString::from_str(env, json)
    });
    outcome.resolve::<ThrowRuntimeExAndDefault>()
}

#[jni_mangle("com.kaanb.MoonRunens")]
pub extern "system" fn optimize(ctx_ptr: i64) {
    let ctx_ptr = ctx_ptr as *mut FSRSContext;
    let ctx = unsafe { &mut *ctx_ptr };
    ctx.optimize().unwrap()
}
