use std::{
    fs,
    io::{BufReader, Cursor, Read},
};

use flate2::bufread::{self, GzDecoder};

fn main() {
    let JMDICT_URL = "http://ftp.edrdg.org/pub/Nihongo/JMdict_e.gz";
    let KANJIDIC_URL = "http://www.edrdg.org/kanjidic/kanjidic2.xml.gz";
    let KANJIVG_URL =
        "https://github.com/KanjiVG/kanjivg/releases/download/r20250816/kanjivg-20250816-all.zip";

    download_and_extract_gzip(JMDICT_URL, "./data/JMdict_e.xml");
    download_and_extract_gzip(KANJIVG_URL, "./data/kanjidic2.xml");
}

fn download_and_extract_gzip(url: &str, output_path: &str) {
    let got_bytes = reqwest::blocking::get(url).unwrap().bytes().unwrap();
    let mut decoder = GzDecoder::new(&got_bytes[..]);

    let mut out = vec![];
    decoder.read_to_end(&mut out).unwrap();
    fs::write(output_path, out).unwrap();
}
