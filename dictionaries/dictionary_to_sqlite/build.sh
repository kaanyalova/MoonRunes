rm dict.db dict.db.zst ../../app/src/main/assets/dictionaries/dict.db.zst
cargo run --release
zstd -19 dict.db -o dict.db.zst
cp dict.db.zst ../../app/src/main/assets/dictionaries/dict.db.zst 