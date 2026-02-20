use std::fs;

use serde::{Deserialize, Serialize};
use sqlx::{
    Connection, SqliteConnection, SqlitePool, query,
    sqlite::{SqliteConnectOptions, SqlitePoolOptions},
};

#[derive(Debug, Serialize, Deserialize)]
struct Entries {
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
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiElement {
    #[serde(rename = "keb")]
    body: String,
    #[serde(rename = "ke_pri", default)]
    priorities: Vec<String>,
}
#[derive(Debug, Serialize, Deserialize)]
struct ReadingElement {
    #[serde(rename = "reb")]
    body: String,
    #[serde(rename = "re_pri", default)]
    priorities: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
struct Sense {
    #[serde(rename = "pos", default)]
    parts_of_speech: Vec<String>,
    #[serde(rename = "gloss", default)]
    definitions: Vec<String>,
}

async fn dump_to_sqlite(entries: &Entries, path: &str) {
    let options = SqliteConnectOptions::new()
        .create_if_missing(true)
        .filename(path);

    let pool = SqlitePoolOptions::new()
        .connect_with(options)
        .await
        .unwrap();

    let create_tables = fs::read_to_string("create_tables.sql").unwrap();

    query(&create_tables).execute(&pool).await.unwrap();

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

    let create_indexes = fs::read_to_string("create_indexes.sql").unwrap();
    query(&create_indexes).execute(&pool).await.unwrap();
}

#[tokio::main]
async fn main() {
    let dictionary_xml = fs::read_to_string("../JMdict_e.xml").unwrap();
    let dictionary_xml = dictionary_xml.replace("&", "&amp;");
    let entries: Entries = quick_xml::de::from_str(&dictionary_xml).unwrap();

    dump_to_sqlite(&entries, "dict.db").await;
}
