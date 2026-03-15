package com.kaanb.moonrunes.dictionary.usecase

import android.content.Context
import com.kaanb.moonrunes.dictionary.dao.KanjiCharacter
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


data class DictionaryEntryWithKanjiDicData(
    val dictionaryEntry: DictionaryEntry,
    val kanjiDicEntry: Map<String, KanjiCharacter>
)

class SearchAndFormatDictionaryEntriesUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    @ApplicationContext private val context : Context
) {
    operator fun invoke(searchInput: String): List<DictionaryEntry> {
        val rawEntries = dictionaryRepository.searchDictionary(searchInput)

        return rawEntries.map { entry ->
            formatDictionaryEntry(entry, context)
        }
    }
}