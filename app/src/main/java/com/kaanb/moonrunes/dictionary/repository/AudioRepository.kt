package com.kaanb.moonrunes.dictionary.repository

import com.kaanb.moonrunes.dictionary.service.AudioService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepository @Inject constructor(private val audioService: AudioService) {

}