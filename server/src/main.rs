use std::{fs, net::TcpListener};

use axum::{
    Json, Router,
    body::Body,
    extract::{Path, State},
    http::Response,
    response::IntoResponse,
    routing::get,
};
use reqwest::StatusCode;
use serde::{Deserialize, Serialize};
use sqlx::{Pool, Sqlite, SqlitePool, query, sqlite::SqliteConnectOptions};

use crate::audio_cache::cached_get_audio_from_jisho;

mod audio_cache;
mod db;

#[derive(Serialize, Deserialize)]
pub struct Config {
    db_path: String,
    host: bool,
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();
    let config = fs::read_to_string("./config.toml").unwrap();
    let config: Config = toml::from_str(&config).unwrap();

    let db = SqliteConnectOptions::new()
        .filename(config.db_path)
        .create_if_missing(true);

    let pool = SqlitePool::connect_with(db).await.unwrap();
    create_tables(&pool).await;

    let app = Router::new()
        .route("/get_audio/{word}", get(get_audio))
        .route("/get_wotd/", get(get_wotd))
        .with_state(pool.clone());

    let address = if config.host {
        "127.0.0.1:3000"
    } else {
        "0.0.0.0:3000"
    };
    let listener = tokio::net::TcpListener::bind(address).await.unwrap();
    println!("serving on {}", address);
    axum::serve(listener, app).await.unwrap();
}

async fn create_tables(pool: &Pool<Sqlite>) {
    query("CREATE TABLE IF NOT EXISTS AudioRecord (word TEXT PRIMARY KEY, audio BLOB)")
        .execute(pool)
        .await
        .unwrap();
}

async fn get_audio(Path(word): Path<String>, State(pool): State<SqlitePool>) -> impl IntoResponse {
    println!("got word {}", word);
    let audio = cached_get_audio_from_jisho(&word, &pool).await.unwrap();
    if let Some(audio) = audio {
        let mime = infer::get(&audio).unwrap().mime_type();
        return Response::builder()
            .header("Content-Type", mime)
            .body(Body::from(audio))
            .unwrap();
    }

    StatusCode::NOT_FOUND.into_response()
}

#[derive(Serialize, Deserialize)]
struct WotdResponse {
    pub id: i64,
}

async fn get_wotd() -> Json<WotdResponse> {
    return Json(WotdResponse { id: 1000140 });
}
