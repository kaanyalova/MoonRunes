package com.kaanb.moonrunes

import android.content.Context
import android.util.Log
import com.github.luben.zstd.Zstd
import java.io.File
import java.time.LocalDateTime


const val TAG = "DictionaryDecompressor"

fun decompressDictionaries(context: Context) {
    val uncompressedFile = File(context.filesDir, "dict.db")
    val start= System.currentTimeMillis()

    if (uncompressedFile.exists()) {
        Log.d(TAG,"dictionary already exists, skipping decompressing the dictionary ")
        return
    }

    val compressedDictionary = context.assets.open("dictionaries/dict.db.zst")
    val bytes = compressedDictionary.readBytes()
    val decompressedBytes = Zstd.decompress(bytes)
    uncompressedFile.writeBytes(decompressedBytes)
    val end = System.currentTimeMillis()

    Log.d(TAG, "decompressed the dictionary took ${end-start}ms ")
}