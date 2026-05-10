use std::ops::Not;
use std::path::Path;

use anyhow::Result;
use anyhow::anyhow;
use kuchikiki::{parse_html, traits::TendrilSink};
use sqlx::Pool;
use sqlx::Sqlite;
use sqlx::query;
use sqlx::query_as;

async fn get_audio_url_from_jisho(word: &str) -> Result<String> {
    let url = format!("https://jisho.org/search/{}", word);
    let webpage = reqwest::get(url).await?.text().await?;

    let document = parse_html().one(webpage);

    let audio = document.select_first("audio").unwrap();
    let mut children = audio.as_node().children();

    let source = children.next();

    if let Some(src) = source {
        let element = src.as_element();
        if let Some(element) = element {
            let attributes = element.attributes.clone();
            let inner = attributes.into_inner();

            let source = inner.get("src");

            if let Some(source) = source {
                let formatted = format!("https://{}", source);
                return Ok(formatted);
            }
        }
    }
    Err(anyhow!("not found"))
}

async fn download_audio(url: &str) -> Result<Vec<u8>> {
    let bytes = reqwest::get(url).await?.bytes().await?;
    Ok(bytes.to_vec())
}

#[derive(Debug, sqlx::FromRow)]
pub struct AudioRecord {
    pub word: String,
    pub audio: Vec<u8>,
}

pub async fn cached_get_audio_from_jisho(
    word: &str,
    pool: &Pool<Sqlite>,
) -> Result<Option<Vec<u8>>> {
    let queried: Option<AudioRecord> = query_as("SELECT * FROM AudioRecord WHERE word = ?")
        .bind(word)
        .fetch_optional(pool)
        .await?;

    if let Some(audio_record) = queried {
        return Ok(Some(audio_record.audio));
    } else {
        let audio_url = get_audio_url_from_jisho(word).await;

        if let Ok(url) = audio_url {
            println!("got audio url {}", &url);

            let audio = download_audio(&url).await?;
            query("INSERT INTO AudioRecord(word, audio) VALUES (?, ?)")
                .bind(word)
                .bind(audio.clone())
                .execute(pool)
                .await?;

            return Ok(Some(audio));
        }

        return Ok(None);
    }
}

#[tokio::test]
async fn test_audio_scraping() {
    let word = "watashi";
    let audio = get_audio_url_from_jisho(word).await.unwrap();
    dbg!(audio);
}
