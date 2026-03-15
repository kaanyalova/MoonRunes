package com.kaanb.moonrunes.dictionary.usecase

import android.content.Context
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetDictionaryEntryByIdUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(id: Long): DictionaryEntry {
        val databaseEntry = dictionaryRepository.getEntry(id)
        return formatDictionaryEntry(databaseEntry, context)
    }
}