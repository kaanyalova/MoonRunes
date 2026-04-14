package com.kaanb.moonrunes.dictionary.usecase

import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithPaths
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithStrokes
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.kanjiStrokesToComposePaths
import jakarta.inject.Inject

class GetKanjiDicEntryUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    operator fun invoke(kanji: String) {
        val entry = dictionaryRepository.getKanjiDicEntry(kanji)
        KanjiDicCharacterWithPaths(
            entry.kanjiDicEntry,
            kanjiStrokesToComposePaths(entry.strokes)
        )
    }
}