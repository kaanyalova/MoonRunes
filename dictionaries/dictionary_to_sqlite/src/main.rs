use std::fs;

use serde::{Deserialize, Serialize};
use sqlx::{
    Connection, Pool, Sqlite, SqliteConnection, SqlitePool, query, query_as,
    sqlite::{SqliteConnectOptions, SqlitePoolOptions},
};

#[derive(Debug, Serialize, Deserialize)]
struct JmDictEntries {
    #[serde(rename = "entry")]
    entries: Vec<Entry>,
}

#[derive(Debug, Serialize, Deserialize)]
struct Entry {
    #[serde(rename = "ent_seq")]
    entry_sequence: i64,
    #[serde(rename = "k_ele", default)]
    kanji_elements: Vec<KanjiElement>,
    #[serde(rename = "r_ele")]
    reading_elements: Vec<ReadingElement>,
    #[serde(rename = "sense")]
    senses: Vec<Sense>,
    //relevancy_score: i64,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiElement {
    #[serde(rename = "keb")]
    body: String,
    #[serde(rename = "ke_pri", default)]
    priorities: Vec<String>,
    #[serde(rename = "ke_inf", default)]
    kanji_info: Vec<String>,
}
#[derive(Debug, Serialize, Deserialize)]
struct ReadingElement {
    #[serde(rename = "reb")]
    body: String,
    #[serde(rename = "re_pri", default)]
    priorities: Vec<String>,
    #[serde(rename = "re_restr", default)]
    // this is here when there is multiple kanji that maps to the same/multiple readings, might not be there as well
    // which means it just maps to all kanji
    maps_to_kanji: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
struct Sense {
    #[serde(rename = "pos", default)]
    parts_of_speech: Vec<String>,
    #[serde(rename = "gloss", default)]
    definitions: Vec<String>,
}

// Kanjidic

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCharacter {
    #[serde(rename = "literal")]
    literal: String,
    #[serde(rename = "codepoint")]
    codepoints: KanjiCodepointGroup,
    #[serde(rename = "radical")]
    radicals: KanjiRadicalGroup,
    misc: KanjiMisc,
    #[serde(rename = "dic_number", default)]
    dictionary_references: KanjiDictionaryReferenceGroup,
    #[serde(rename = "query_code", default)]
    query_codes: KanjiQueryCodeGroup,
    #[serde(default)]
    reading_meaning: KanjiReadingMeaning,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiRadicalGroup {
    #[serde(rename = "rad_value")]
    values: Vec<KanjiRadical>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCodepointGroup {
    #[serde(rename = "cp_value", default)]
    values: Vec<KanjiCodePoint>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCodePoint {
    #[serde(rename = "@cp_type")]
    codepoint_type: String,
    #[serde(rename = "$text")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiRadical {
    #[serde(rename = "@rad_type")]
    radical_type: String,
    #[serde(rename = "$text")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMiscVariant {
    #[serde(rename = "@var_type")]
    variant_type: String,
    #[serde(rename = "$text")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiDictionaryReferenceGroup {
    #[serde(rename = "dic_ref")]
    entries: Vec<KanjiDictionaryReference>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiDictionaryReference {
    #[serde(rename = "@dr_type")]
    dictionary_type: String,
    #[serde(rename = "@m_vol", default)]
    volume: String,
    #[serde(rename = "@m_page", default)]
    page: String,
    #[serde(rename = "$text")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMisc {
    #[serde(default)]
    grade: String,
    stroke_count: Vec<String>,
    #[serde(rename = "variant", default)]
    variants: Vec<KanjiMiscVariant>,
    #[serde(rename = "freq", default)]
    frequency: String,
    #[serde(default)]
    jlpt: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiQueryCode {
    #[serde(rename = "@qc_type")]
    query_code_type: String,
    #[serde(rename = "$text")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiQueryCodeGroup {
    #[serde(rename = "q_code")]
    entries: Vec<KanjiQueryCode>,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiReadingMeaning {
    #[serde(rename = "rmgroup")]
    group: KanjiReadingMeaningGroup,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiReadingMeaningGroup {
    #[serde(rename = "reading", default)]
    readings: Vec<KanjiReading>,
    #[serde(rename = "meaning", default)]
    meanings: Vec<KanjiMeaning>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiReading {
    #[serde(rename = "@r_type")]
    reading_type: String,
    #[serde(rename = "$value")]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMeaning {
    #[serde(rename = "@m_lang", default)]
    language: String,
    #[serde(rename = "$value")]
    body: String,
}
#[derive(Debug, Serialize, Deserialize)]
struct KanjiDictEntries {
    #[serde(rename = "character")]
    entries: Vec<KanjiCharacter>,
}

async fn dump_to_sqlite(pool: &Pool<Sqlite>, entries: &JmDictEntries) {
    let create_tables = fs::read_to_string("create_tables.sql").unwrap();

    query(&create_tables).execute(pool).await.unwrap();

    let mut tx = pool.begin().await.unwrap();

    for entry in &entries.entries {
        let entry_id = entry.entry_sequence;

        query("INSERT INTO Entry(id) VALUES (?)")
            .bind(&entry_id)
            .execute(&mut *tx)
            .await
            .unwrap()
            .last_insert_rowid();

        for kanji_element in &entry.kanji_elements {
            let kanji_element_id = query("INSERT INTO KanjiElement(entry_fk, body) VALUES (?, ?)")
                .bind(&entry_id)
                .bind(&kanji_element.body)
                .execute(&mut *tx)
                .await
                .unwrap()
                .last_insert_rowid();

            for kanji_priority in &kanji_element.priorities {
                query("INSERT INTO Priority(element_fk, body) VALUES (?, ?)")
                    .bind(&kanji_element_id)
                    .bind(&kanji_priority)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }

            for kanji_information in &kanji_element.kanji_info {
                query("INSERT INTO KanjiInformation(kanji_element_fk, body) VALUES (?, ?)")
                    .bind(&kanji_element_id)
                    .bind(&kanji_information)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }
        }

        for reading_element in &entry.reading_elements {
            let reading_element_id =
                query("INSERT INTO ReadingElement(entry_fk, body) VALUES (?, ?)")
                    .bind(&entry_id)
                    .bind(&reading_element.body)
                    .execute(&mut *tx)
                    .await
                    .unwrap()
                    .last_insert_rowid();

            for priority in &reading_element.priorities {
                query("INSERT INTO Priority(element_fk, body) VALUES (?, ?)")
                    .bind(&reading_element_id)
                    .bind(&priority)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }

            for kanji_map in &reading_element.maps_to_kanji {
                query("INSERT INTO MapsToKanji(reading_fk, body) VALUES (?, ?)")
                    .bind(&reading_element_id)
                    .bind(kanji_map)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }
        }

        for sense in &entry.senses {
            let sense_id = query("INSERT INTO Sense(entry_fk) VALUES (?)")
                .bind(&entry_id)
                .execute(&mut *tx)
                .await
                .unwrap()
                .last_insert_rowid();

            for definition in &sense.definitions {
                query("INSERT INTO Definition(sense_fk, body) VALUES (?, ?)")
                    .bind(&sense_id)
                    .bind(&definition)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }

            for part_of_speech in &sense.parts_of_speech {
                query("INSERT INTO PartOfSpeech(sense_fk, body) VALUES (?, ?)")
                    .bind(&sense_id)
                    .bind(&part_of_speech)
                    .execute(&mut *tx)
                    .await
                    .unwrap();
            }
        }
    }

    tx.commit().await.unwrap();
}

#[derive(Debug, sqlx::FromRow)]
struct ReadingElementDbEntry {
    id: i64,
    body: String,
    entry_fk: i64,
}

async fn create_romaji_indexes(pool: &Pool<Sqlite>) {
    let all_reading_elements: Vec<ReadingElementDbEntry> = query_as("SELECT * FROM ReadingElement")
        .fetch_all(pool)
        .await
        .unwrap();

    let all_romaji: Vec<ReadingElementDbEntry> = all_reading_elements
        .into_iter()
        .map(|l| {
            let romaji = wana_kana::ConvertJapanese::to_romaji(l.body.as_str());

            ReadingElementDbEntry {
                id: l.id,
                body: romaji,
                entry_fk: l.entry_fk,
            }
        })
        .collect();

    let mut tx = pool.begin().await.unwrap();

    for romaji in all_romaji {
        query("INSERT INTO RomajiReadingElement (body, entry_fk) VALUES (?, ?)")
            .bind(&romaji.body)
            .bind(&romaji.entry_fk)
            .execute(&mut *tx)
            .await
            .unwrap();
    }

    tx.commit().await.unwrap();
}

async fn create_indexes(pool: &Pool<Sqlite>) {
    let create_indexes = fs::read_to_string("create_indexes.sql").unwrap();
    query(&create_indexes).execute(pool).await.unwrap();
}

async fn dump_kanjidic(pool: &Pool<Sqlite>) {}

#[tokio::main]
async fn main() {
    let dictionary_xml = fs::read_to_string("../JMdict_e.xml").unwrap();
    let dictionary_xml = dictionary_xml.replace("&", "&amp;");
    let entries: JmDictEntries = quick_xml::de::from_str(&dictionary_xml).unwrap();

    let options = SqliteConnectOptions::new()
        .create_if_missing(true)
        .filename("dict.db");

    let pool = SqlitePoolOptions::new()
        .connect_with(options)
        .await
        .unwrap();

    //let kanji_xml = fs::read_to_string("../kanjidic2.xml").unwrap();
    //let kanji_entries: KanjiDictEntries = quick_xml::de::from_str(&kanji_xml).unwrap();

    //println!("{:?}", kanji_entries.entries.iter().take(10));

    dump_to_sqlite(&pool, &entries).await;
    create_romaji_indexes(&pool).await;
    create_indexes(&pool).await;
}

// 37.4 mb with no indexes
// 43.2 mb with romaji indexes
// 69.9 mb with romaji indexes + fts indexes
// 91.0 mb with romaji indexes + fts indexes + fk indexes
// 103.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes
// 104.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes + kanji information
