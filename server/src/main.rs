use std::fs;

use serde::{Deserialize, Serialize};

mod audio_cache;
mod db;

#[derive(Serialize, Deserialize)]
pub struct Config {
    db_path: String,
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();
    let config = fs::read_to_string("./config.toml").unwrap();
    let config: Config = toml::from_str(&config).unwrap();
}

fn derive_id_from_day() -> u32 {
    todo!()
}
