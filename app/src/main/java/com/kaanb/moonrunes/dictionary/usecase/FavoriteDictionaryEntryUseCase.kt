package com.kaanb.moonrunes.dictionary.usecase

import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.WordDisplayData
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDao
import com.kaanb.moonrunes.flash_cards.repository.FlashCardRepository
import javax.inject.Inject

class FavoriteDictionaryEntryUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val flashCardDao: FlashCardDao,
    private val getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase,
    private val flashCardRepository: FlashCardRepository
) {
    suspend operator fun invoke(dictionaryId: Long, isFavorite: Boolean) {
        dictionaryRepository.setFavoriteState(dictionaryId, isFavorite)

        if (!isFavorite) {
            flashCardDao.deleteCardById(dictionaryId)
            flashCardRepository.deleteCard(dictionaryId)
        }

        val entry = getDictionaryEntryByIdUseCase(dictionaryId)
        val word = entry.mainWordDisplay.word

        val hasKanji = word is WordDisplayData.KanjiWordDisplay
        val innerWord = when (word) {
            is WordDisplayData.KanjiWordDisplay -> word.value.kanji
            is WordDisplayData.NormalWordDisplay -> word.value
        }


        // TODO
        // - fetch audio
        // - fetch sentences
        val flashCard = FlashCard(
            id = dictionaryId,
            word = innerWord,
            wordReading = if (hasKanji) {
                word.value.kanji
            } else {
                null
            },
            containsKanji = hasKanji,
            exampleSentence = null,
            exampleSentenceFuriganaListJson = listOf(),
            exampleSentenceMeaning = null,
            audio = byteArrayOf(),
            meaning = entry.meanings.first().values.joinToString(", ")
        )

        flashCardRepository.addCard(flashCard)
    }

}