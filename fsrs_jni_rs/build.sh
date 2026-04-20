export ANDROID_NDK_HOME="/home/kaan/Belgeler/8000_DevTools/android_sdk/ndk/28.0.12674087"
cargo ndk -t armeabi-v7a -t arm64-v8a -t x86_64 -o ../fsrs_jni/src/main/jniLibs/ build --release