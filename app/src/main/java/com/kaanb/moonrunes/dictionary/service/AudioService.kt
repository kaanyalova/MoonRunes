package com.kaanb.moonrunes.dictionary.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface AudioService {
    @GET("/get_audio/{word}")
    suspend fun getAudio(word: String): Call<ResponseBody>
}