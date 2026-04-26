package com.kaanb.moonrunes.dictionary.usecase

import android.content.Context
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * This one also inserts the kanjidic entries unlike the one used to search, which that would be unnescessary
 */
class GetDictionaryEntryByIdUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(id: Long): DictionaryEntry {
        val databaseEntry = dictionaryRepository.getEntry(id)

        val formattedEntry = formatDictionaryEntry(databaseEntry, context)
        val kanjiDicEntries = formattedEntry.kanjiLiterals.map { literal -> dictionaryRepository.getKanjiDicEntry(literal) }
        formattedEntry.kanjiDicEntries = kanjiDicEntries

        return formattedEntry
    }
}