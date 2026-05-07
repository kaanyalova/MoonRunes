package com.kaanb.moonrunes.dictionary.usecase

import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import javax.inject.Inject

class FavoriteDictionaryEntryUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    //private val flashcardRepository: DictionaryRepository,
) {
    operator fun invoke(id: Long, state: Boolean) {
        dictionaryRepository.setFavoriteState(id, state)
    }
}