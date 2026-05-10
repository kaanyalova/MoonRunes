package com.kaanb.moonrunes.dictionary.util

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

const val TAG_ = "Audio"

fun playAudioForWord(context: Context, word: String) {
    val exoPlayer = ExoPlayer.Builder(context).build()
    val url = "http://10.0.2.2:3000/get_audio/${word}"
    val media = MediaItem.fromUri(url)
    Log.d(TAG_, "playing audio from $url")

    exoPlayer.setMediaItem(media)
    exoPlayer.prepare()
    exoPlayer.play()
}