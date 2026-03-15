package com.kaanb.moonrunes.dictionary.usecase

import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import jakarta.inject.Inject

class GetKanjiDicEntryUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    operator fun invoke(kanji: String){
        dictionaryRepository.getKanjiDicEntry(kanji)
    }
}