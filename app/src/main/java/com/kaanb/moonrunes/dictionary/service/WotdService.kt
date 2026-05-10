package com.kaanb.moonrunes.dictionary.service

import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface WotdService {
    @GET("/get_wotd/")
    suspend fun getWotd(): WordOfTheDay
}


@Serializable
data class WordOfTheDay (
    val id: Long
)