package com.kaanb.moonrunes.dictionary.repository

import android.util.Log
import com.kaanb.moonrunes.dictionary.dao.DictionaryDao
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacter
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithStrokes
import com.kaanb.moonrunes.dictionary.dao.PathPiece
import com.kaanb.moonrunes.dictionary.dao.searchTopNEntriesByDefinitionRawQuery
import com.kaanb.moonrunes.dictionary.dao.searchTopNEntriesByKanjiRawQuery
import com.kaanb.moonrunes.dictionary.dao.searchTopNEntriesByReadingRawQuery
import com.kaanb.moonrunes.dictionary.dao.searchTopNEntriesByRomajiReadingRawQuery
import com.kaanb.moonrunes.dictionary.util.TextType
import com.kaanb.moonrunes.dictionary.util.detectTextType
import kotlinx.serialization.json.Json
import javax.inject.Inject

const val TAG = "DictionaryRepository"

class DictionaryRepository @Inject constructor(private val dictionaryDao: DictionaryDao) {

    val MAX_ENTRIES_PER_CATEGORY = 50

    fun searchDictionary(input: String): List<DictionaryDatabaseEntry> {
        val start = System.currentTimeMillis()

        if (input.isEmpty()) {
            return emptyList()
        }


        val textTypeDetectionStart = System.currentTimeMillis()
        val textType = detectTextType(input)
        val textTypeDetectionEnd = System.currentTimeMillis()
        Log.d(TAG, "text type detection took ${textTypeDetectionEnd - textTypeDetectionStart}ms")


        var combined: List<DictionaryDatabaseEntry> =
            mutableListOf<DictionaryDatabaseEntry>().toMutableList()
        Log.d(TAG, "search type is $textType")


        when (textType) {
            TextType.Romaji -> {
                val entriesByRomajiReading = searchTopNEntriesByRomajiReadingRawQuery(
                    dictionaryDao, input, MAX_ENTRIES_PER_CATEGORY
                )
                val entriesByDefinition = searchTopNEntriesByDefinitionRawQuery(
                    dictionaryDao, input, MAX_ENTRIES_PER_CATEGORY
                )

                // why the warnings, its mutable
                combined += entriesByRomajiReading
                combined += entriesByDefinition
            }


            TextType.ContainsKanji -> {
                val entriesByKanji =
                    searchTopNEntriesByKanjiRawQuery(dictionaryDao, input, MAX_ENTRIES_PER_CATEGORY)

                combined += entriesByKanji
            }

            TextType.HiraganaOrKatakana -> {

                val entriesByJapaneseReading = searchTopNEntriesByReadingRawQuery(
                    dictionaryDao, input, MAX_ENTRIES_PER_CATEGORY
                )
                combined += entriesByJapaneseReading
            }
        }


        //var combined = entriesByJapaneseReading +
        //        entriesByRomajiReading +
        //        entriesByDefinition +
        //        entriesByKanji

        combined = combined.distinctBy {
            it.entry
        }

        combined = combined.take(MAX_ENTRIES_PER_CATEGORY)

        val end = System.currentTimeMillis()


        Log.d(TAG, "Querying for dictionary took ${end - start}ms")

        //val json = Json.encodeToString(combined[2])
        //Log.d(TAG, json)

        return combined
    }

    fun getEntry(id: Long): DictionaryDatabaseEntry {
        return dictionaryDao.getEntryById(id)
    }

    fun getKanjiDicEntry(kanji: String): KanjiDicCharacterWithStrokes {
        val rawEntry = dictionaryDao.getKanjiDicEntry(kanji)

        val serializedCharacter = Json.decodeFromString<KanjiDicCharacter>(rawEntry.jsonData)
        val serializedStrokes = Json.decodeFromString<List<List<PathPiece>>>(rawEntry.svgData?: "")

        return KanjiDicCharacterWithStrokes(
            serializedCharacter, serializedStrokes
        )
    }

}