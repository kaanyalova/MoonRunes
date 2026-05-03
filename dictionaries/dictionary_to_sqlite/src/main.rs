use std::collections::HashMap;
use std::fs;

use serde::{Deserialize, Serialize};
use sqlx::{
    Connection, Pool, Sqlite, SqliteConnection, SqlitePool, query, query_as,
    sqlite::{SqliteConnectOptions, SqlitePoolOptions},
    types::Json,
};

#[derive(Debug, Serialize, Deserialize)]
struct JmDictEntries {
    #[serde(rename(deserialize = "entry"))]
    entries: Vec<Entry>,
}

#[derive(Debug, Serialize, Deserialize)]
struct Entry {
    #[serde(rename(deserialize = "ent_seq"))]
    entry_sequence: i64,
    #[serde(rename(deserialize = "k_ele"), default)]
    kanji_elements: Vec<KanjiElement>,
    #[serde(rename(deserialize = "r_ele"))]
    reading_elements: Vec<ReadingElement>,
    #[serde(rename(deserialize = "sense"))]
    senses: Vec<Sense>,
    //relevancy_score: i64,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiElement {
    #[serde(rename(deserialize = "keb"))]
    body: String,
    #[serde(rename(deserialize = "ke_pri"), default)]
    priorities: Vec<String>,
    #[serde(rename(deserialize = "ke_inf"), default)]
    kanji_info: Vec<String>,
}
#[derive(Debug, Serialize, Deserialize)]
struct ReadingElement {
    #[serde(rename(deserialize = "reb"))]
    body: String,
    #[serde(rename(deserialize = "re_pri"), default)]
    priorities: Vec<String>,
    #[serde(rename(deserialize = "re_restr"), default)]
    // this is here when there is multiple kanji that maps to the same/multiple readings, might not be there as well
    // which means it just maps to all kanji
    maps_to_kanji: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
struct Sense {
    #[serde(rename(deserialize = "pos"), default)]
    parts_of_speech: Vec<String>,
    #[serde(rename(deserialize = "gloss"), default)]
    definitions: Vec<String>,
}

// Kanjidic

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCharacter {
    #[serde(rename(deserialize = "literal"))]
    literal: String,
    #[serde(rename(deserialize = "codepoint"))]
    codepoints: KanjiCodepointGroup,
    #[serde(rename(deserialize = "radical"))]
    radicals: KanjiRadicalGroup,
    misc: KanjiMisc,
    #[serde(rename(deserialize = "dic_number"), default)]
    dictionary_references: KanjiDictionaryReferenceGroup,
    #[serde(rename(deserialize = "query_code"), default)]
    query_codes: KanjiQueryCodeGroup,
    #[serde(default)]
    reading_meaning: KanjiReadingMeaning,
    #[serde(default)]
    make_up_radicals: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiRadicalGroup {
    #[serde(rename(deserialize = "rad_value"))]
    values: Vec<KanjiRadical>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCodepointGroup {
    #[serde(rename(deserialize = "cp_value"), default)]
    values: Vec<KanjiCodePoint>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiCodePoint {
    #[serde(rename(deserialize = "@cp_type"))]
    codepoint_type: String,
    #[serde(rename(deserialize = "$text"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiRadical {
    #[serde(rename(deserialize = "@rad_type"))]
    radical_type: String,
    #[serde(rename(deserialize = "$text"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMiscVariant {
    #[serde(rename(deserialize = "@var_type"))]
    variant_type: String,
    #[serde(rename(deserialize = "$text"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiDictionaryReferenceGroup {
    #[serde(rename(deserialize = "dic_ref"))]
    entries: Vec<KanjiDictionaryReference>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiDictionaryReference {
    #[serde(rename(deserialize = "@dr_type"))]
    dictionary_type: String,
    #[serde(rename(deserialize = "@m_vol"), default)]
    volume: String,
    #[serde(rename(deserialize = "@m_page"), default)]
    page: String,
    #[serde(rename(deserialize = "$text"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMisc {
    #[serde(default)]
    grade: String,
    stroke_count: Vec<String>,
    #[serde(rename(deserialize = "variant"), default)]
    variants: Vec<KanjiMiscVariant>,
    #[serde(rename(deserialize = "freq"), default)]
    frequency: String,
    #[serde(default)]
    jlpt: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiQueryCode {
    #[serde(rename(deserialize = "@qc_type"))]
    query_code_type: String,
    #[serde(rename(deserialize = "$text"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiQueryCodeGroup {
    #[serde(rename(deserialize = "q_code"))]
    entries: Vec<KanjiQueryCode>,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiReadingMeaning {
    #[serde(rename(deserialize = "rmgroup"))]
    group: KanjiReadingMeaningGroup,
}

#[derive(Debug, Serialize, Deserialize, Default)]
struct KanjiReadingMeaningGroup {
    #[serde(rename(deserialize = "reading"), default)]
    readings: Vec<KanjiReading>,
    #[serde(rename(deserialize = "meaning"), default)]
    meanings: Vec<KanjiMeaning>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiReading {
    #[serde(rename(deserialize = "@r_type"))]
    reading_type: String,
    #[serde(rename(deserialize = "$value"))]
    body: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiMeaning {
    #[serde(rename(deserialize = "@m_lang"), default)]
    language: String,
    #[serde(rename(deserialize = "$value"))]
    body: String,
}
#[derive(Debug, Serialize, Deserialize)]
struct KanjiDictEntries {
    #[serde(rename(deserialize = "character"))]
    entries: Vec<KanjiCharacter>,
}

async fn dump_jmdict_to_sqlite(pool: &Pool<Sqlite>, entries: &JmDictEntries) {
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

#[derive(Debug, Serialize, Deserialize)]
struct KanjiDictDbJsonEntry {
    body: String,
    json_data: String,
    svg_data: Option<String>,
}

async fn dump_kanjidic_to_sqlite(pool: &Pool<Sqlite>, entries: &KanjiDictEntries) {
    // just dump it to json, i dont need to search through this,
    // the schema is bloated (like 20mb for 2mb of xml) but i dont care

    let json_mapped: Vec<KanjiDictDbJsonEntry> = entries
        .entries
        .iter()
        .map(|e| KanjiDictDbJsonEntry {
            body: e.literal.clone(),
            json_data: serde_json::to_string(e).unwrap(),
            svg_data: None,
        })
        .collect();

    let mut tx = pool.begin().await.unwrap();

    for json_entry in json_mapped {
        query("INSERT INTO KanjiDicEntry(body, json_data, svg_data) VALUES (?, ?, ?)")
            .bind(&json_entry.body)
            .bind(&json_entry.json_data)
            .bind(&json_entry.svg_data)
            .execute(&mut *tx)
            .await
            .unwrap();
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

#[derive(Debug, Serialize, Deserialize)]
struct Coord {
    x: f32,
    y: f32,
}

#[derive(Debug, Serialize, Deserialize)]
struct Path {
    #[serde(flatten)]
    paths: Vec<Coord>,
}

#[derive(Debug, Serialize, Deserialize)]
struct KanjiStrokes {
    #[serde(flatten)]
    strokes: Vec<Path>,
}

use usvg::tiny_skia_path::PathSegment as UsvgPathSegment;

#[derive(Serialize)]
#[serde(tag = "type")]
pub enum CanvasPathSegment {
    MoveTo {
        x: f32,
        y: f32,
    },
    LineTo {
        x: f32,
        y: f32,
    },
    QuadTo {
        x1: f32,
        y1: f32,
        x: f32,
        y: f32,
    },
    CubicTo {
        x1: f32,
        y1: f32,
        x2: f32,
        y2: f32,
        x: f32,
        y: f32,
    },
    Close,
}

pub fn paths_to_json(paths: Vec<Vec<CanvasPathSegment>>, pretty: bool) -> String {
    if pretty {
        serde_json::to_string_pretty(&paths).unwrap()
    } else {
        serde_json::to_string(&paths).unwrap()
    }
}

pub fn extract_svg_paths(svg_data: &str) -> Vec<Vec<CanvasPathSegment>> {
    // i hate xml, i hate xml, i hate xml, i hate xml, i hate xml
    let cleaned_svg = svg_data.replace("kvg:", "");

    let options = usvg::Options::default();
    let tree = usvg::Tree::from_str(&cleaned_svg, &options).unwrap();

    let mut paths_vec = Vec::new();
    walk_group(&tree.root(), &mut paths_vec);

    paths_vec
}

fn walk_group(group: &usvg::Group, paths_vec: &mut Vec<Vec<CanvasPathSegment>>) {
    for node in group.children() {
        match node {
            usvg::Node::Path(path) => {
                let mut segments = Vec::new();
                for seg in path.data().segments() {
                    match seg {
                        UsvgPathSegment::MoveTo(p) => {
                            segments.push(CanvasPathSegment::MoveTo { x: p.x, y: p.y })
                        }
                        UsvgPathSegment::LineTo(p) => {
                            segments.push(CanvasPathSegment::LineTo { x: p.x, y: p.y })
                        }
                        UsvgPathSegment::QuadTo(p1, p) => {
                            segments.push(CanvasPathSegment::QuadTo {
                                x1: p1.x,
                                y1: p1.y,
                                x: p.x,
                                y: p.y,
                            })
                        }
                        UsvgPathSegment::CubicTo(p1, p2, p) => {
                            segments.push(CanvasPathSegment::CubicTo {
                                x1: p1.x,
                                y1: p1.y,
                                x2: p2.x,
                                y2: p2.y,
                                x: p.x,
                                y: p.y,
                            })
                        }
                        UsvgPathSegment::Close => segments.push(CanvasPathSegment::Close),
                    }
                }
                if !segments.is_empty() {
                    paths_vec.push(segments);
                }
            }
            usvg::Node::Group(g) => walk_group(g, paths_vec),
            _ => {}
        }
    }
}

fn filename_to_kanji(filename: &str) -> Option<char> {
    let hex_part = filename.strip_suffix(".svg").unwrap_or(filename);

    if let Ok(num) = u32::from_str_radix(hex_part, 16) {
        std::char::from_u32(num)
    } else {
        None
    }
}

fn parse_kanji_svgs() -> Vec<(String, String)> {
    let kanji_svgs = fs::read_dir("./data/kanjivg").unwrap();

    kanji_svgs
        .filter_map(|svg| {
            let path = svg.unwrap().path();
            let file_name = path.file_name()?.to_str()?;
            let kanji_char = filename_to_kanji(file_name)?;

            let file = fs::read_to_string(&path).unwrap();
            let svg_paths = extract_svg_paths(&file);
            let to_json = paths_to_json(svg_paths, false);

            Some((kanji_char.to_string(), to_json))
        })
        .collect()
}

async fn update_kanji_svg_data(pool: &Pool<Sqlite>, svgs: &[(String, String)]) {
    let mut tx = pool.begin().await.unwrap();

    for (kanji_str, svg_json) in svgs {
        query("UPDATE KanjiDicEntry SET svg_data = ? WHERE body = ?")
            .bind(svg_json)
            .bind(kanji_str)
            .execute(&mut *tx)
            .await
            .unwrap();
    }

    tx.commit().await.unwrap();
}

fn parse_kradfile(path: &str) -> HashMap<String, Vec<String>> {
    let bytes = fs::read(path).unwrap();
    let (decoded, _, _) = encoding_rs::EUC_JP.decode(&bytes);

    let mut kanji_to_radicals: HashMap<String, Vec<String>> = HashMap::new();

    for line in decoded.lines() {
        if line.starts_with('#') {
            continue;
        }

        let parts: Vec<&str> = line.split(" : ").collect();
        if parts.len() == 2 {
            let kanji = parts[0].to_string();
            let radicals = parts[1].split_whitespace().map(|s| s.to_string()).collect();
            kanji_to_radicals.insert(kanji, radicals);
        }
    }

    kanji_to_radicals
}

fn populate_radicals(
    krad_map: &HashMap<String, Vec<String>>,
    kanji_entries: &mut KanjiDictEntries,
) {
    for entry in &mut kanji_entries.entries {
        if let Some(radicals) = krad_map.get(&entry.literal) {
            entry.make_up_radicals = radicals.clone();
        }
    }
}

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

    let kanji_xml = fs::read_to_string("../kanjidic2.xml").unwrap();
    let mut kanji_entries: KanjiDictEntries = quick_xml::de::from_str(&kanji_xml).unwrap();

    let krad_map = parse_kradfile("kradfile");
    populate_radicals(&krad_map, &mut kanji_entries);

    let kanji_svgs = parse_kanji_svgs();

    dump_jmdict_to_sqlite(&pool, &entries).await;
    create_romaji_indexes(&pool).await;
    dump_kanjidic_to_sqlite(&pool, &kanji_entries).await;
    update_kanji_svg_data(&pool, &kanji_svgs).await;
    create_indexes(&pool).await;
}

// 37.4 mb with no indexes
// 43.2 mb with romaji indexes
// 69.9 mb with romaji indexes + fts indexes
// 91.0 mb with romaji indexes + fts indexes + fk indexes
// 103.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes
// 104.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes + kanji information
// 123.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes + kanji information + kanjidic stuff
// 146.0 mb with romaji indexes + fts indexes + fk indexes + first letter indexes + kanji information + kanjidic stuff + svgs
