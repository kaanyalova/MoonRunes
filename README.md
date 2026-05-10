# How to Build

1 - Install rust toolchain 
https://rustup.rs/

2- Install cargo-ndk 
`cargo install cargo-ndk`

3- Install the android targets for rust
```
rustup target install x86_64-linux-android
rustup target install aarch64-linux-android
rustup target install armv7-linux-androideabi
```

4- Install the Android Ndk
Go to Tools > Sdk Manager > Sdk Tools then install the NDK

5- Set up the ANDROID_NDK_HOME to the NDK path 
for example
``` 
export ANDROID_NDK_HOME="/home/kaan/Android/Sdk/ndk/30.0.14904198/"
```

6- Run `fsrs_jni_rs/build.sh`

7- Run `dictionaries/dictionary_to_sqlite/build.sh`

8- Build the project in Android Studio