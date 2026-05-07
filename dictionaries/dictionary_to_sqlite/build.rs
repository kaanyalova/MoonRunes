use std::{
    fs,
    io::{Cursor, Read},
    path::Path,
};

use flate2::bufread::GzDecoder;
use zip::ZipArchive;

fn main() {
    let JMDICT_URL = "http://ftp.edrdg.org/pub/Nihongo/JMdict_e.gz";
    let KANJIDIC_URL = "http://www.edrdg.org/kanjidic/kanjidic2.xml.gz";
    let KANJIVG_URL =
        "https://github.com/KanjiVG/kanjivg/releases/download/r20250816/kanjivg-20250816-all.zip";

    create_out_dir_if_missing();

    download_and_extract_gzip(JMDICT_URL, "./data/JMdict_e.xml");
    download_and_extract_gzip(KANJIDIC_URL, "./data/kanjidic2.xml");
    download_and_extract_zip(KANJIVG_URL, "./data/kanjivg");
}

fn download_and_extract_gzip(url: &str, output_path: &str) {
    if Path::new(output_path).exists() {
        return;
    }

    let got_bytes = reqwest::blocking::get(url).unwrap().bytes().unwrap();
    let mut decoder = GzDecoder::new(&got_bytes[..]);

    let mut out = vec![];
    decoder.read_to_end(&mut out).unwrap();
    fs::write(output_path, out).unwrap();
}

fn download_and_extract_zip(url: &str, output_path: &str) {
    if Path::new(output_path).exists() {
        return;
    }

    let got_bytes = reqwest::blocking::get(url).unwrap().bytes().unwrap();
    let mut archive = ZipArchive::new(Cursor::new(&got_bytes[..])).unwrap();
    archive.extract(output_path).unwrap();
}

fn create_out_dir_if_missing() {
    if !Path::new("./data").exists() {
        fs::create_dir("./data").unwrap();
    }
} //
