package com.kaanb.moonrunes.dictionary.usecase

import android.content.Context
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry

class SearchAndFormatDictionaryEntriesUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val context: Context
) {
    operator fun invoke(searchInput: String): List<DictionaryEntry> {
        val rawEntries = dictionaryRepository.searchDictionary(searchInput)
        return rawEntries.map { entry ->
            formatDictionaryEntry(entry, context)
        }
    }
}