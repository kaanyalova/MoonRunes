package com.kaanb.moonrunes.dictionary.util

import android.content.Context
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacter
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithStrokes

data class KanjiWithReadings(
    val readings: MutableList<ReadingWithPriorities>,
    val kanjiInfo: MutableList<String>,
)

data class ReadingWithPriorities(
    val reading: String,
    val priorities: List<String>,
)

// used for mapping priorities, not for output
data class KanjiWithSingleReadingAndPriorities(
    val kanji: String,
    val reading: ReadingWithPriorities,
)

data class KanjiWithSingleReading(
    val kanji: String,
    val reading: String,
)


data class Meaning(
    val partsOfSpeech: List<PartOfSpeech>,
    val crossRefs: List<String>,
    val values: List<String> // this is separated with ;'s in jisho
)

sealed class WordDisplayData {
    data class KanjiWordDisplay(val value: KanjiWithSingleReading) : WordDisplayData()
    data class NormalWordDisplay(val value: String) : WordDisplayData()


    fun asNormalString(): String {
        when (this) {
            is KanjiWordDisplay -> return value.kanji
            is NormalWordDisplay -> return value
        }
    }
}

data class WordDisplayWithInfo(
    val word: WordDisplayData,
    val priorities: List<String>,
)


// the id is whatever was in the database
// ie &n; -> dictionary_part_of_speech_n
class PartOfSpeech(val context: Context, val id: String) {

    private fun getInternationalizedStringName(): String {
        val stripped = id.removePrefix("&").removeSuffix(";").replace("-", "_")
        return "dictionary_part_of_speech_$stripped"
    }

    fun toInternationalizedString(): String {
        val resources = context.resources;
        val resourceId =
            resources.getIdentifier(getInternationalizedStringName(), "string", context.packageName)

        if (resourceId == 0) {
            return ""
        }

        return context.getString(resourceId);
    }
}


// easier to display version of the other DictionaryEntry that comes from the database

data class DictionaryEntry(
    val entryId: Long,
    // the kanji/whatever that is displayed at top of the page, the first kanji or
    // the ReadingElement if the kanji doesn't exist.
    val mainWordDisplay: WordDisplayWithInfo,
    // (sub)meanings, parts of speech and crossrefs(related words?) for that specific meaning
    val meanings: List<Meaning>,
    // other forms, their priorities(
    val otherForms: List<WordDisplayWithInfo>,
    // split kanji for kanjidic entries
    val kanjiLiterals: List<String>,
    var kanjiDicEntries: List<KanjiDicCharacterWithStrokes> = listOf(),
    var isFavorited: Boolean = false
)


/**
 * create the mappings for kanji -> Readings
 * the reading elements map to all kanji if they don't have any entries in mapsToKanji
 * if they do have entries they map to the specific kanji
 */
private fun mapKanjiToReading(entry: DictionaryDatabaseEntry): Map<String, KanjiWithReadings> {
    // if the reading element doesn't have any MapsToKanji that means it maps to all kanji (at least thats what
    // jisho does

    var entries: Map<String, KanjiWithReadings> = hashMapOf() // kanji -> KanjiWithReading

    entries = entry.kanjiElements.associate { kanjiElement ->
        val kanjiWithReadings = KanjiWithReadings(
            mutableListOf(), kanjiElement.kanjiInfo.map { it -> it.body }.toMutableList()
        )

        kanjiElement.kanjiElement.body to kanjiWithReadings
    }


    entry.readingElements.forEach { readingElement ->
        // maps to all if the list is empty
        if (readingElement.kanjiMappings.isEmpty()) {

            entries.forEach { e ->
                val readingWithPriorities = ReadingWithPriorities(
                    readingElement.readingElement.body,
                    readingElement.priority.map { priority -> priority.body })
                e.value.readings.add(readingWithPriorities)
            }
        } else {
            // map to specific kanji
            readingElement.kanjiMappings.forEach { kanjiMapping ->
                val readingWithPriorities = ReadingWithPriorities(
                    readingElement.readingElement.body,
                    readingElement.priority.map { priority -> priority.body })

                entries[kanjiMapping.body]?.readings?.add(readingWithPriorities)
            }
        }

    }


    return entries
}


/**
The parts of speech are mapped to the senses that come after them, unless another part of speech is introduced
i am assuming it also maps to all of the sequential parts of speech that come after it as well
sample from the raw xml of the dictionary, see the explanation of <pos> in the xml version of the dictionary
for more info

<entry>
<ent_seq>1000280</ent_seq>
<k_ele>
<keb>論う</keb>
</k_ele>
<r_ele>
<reb>あげつらう</reb>
</r_ele>
<sense>
<pos>&v5u;</pos>
<pos>&vt;</pos>
<misc>&uk;</misc>
<gloss>to discuss</gloss>
</sense>
<sense>
<pos>&v5u;</pos>
<pos>&vt;</pos>
<misc>&uk;</misc>
<gloss>to find fault with</gloss>
<gloss>to criticize</gloss>
<gloss>to criticise</gloss>
</sense>
</entry>
 */
private fun mapSensesToPartsOfSpeech(entry: DictionaryDatabaseEntry): Map<Long, MutableList<String>> { // sense id -> parts of speech

    val mappings: Map<Long, MutableList<String>> = entry.senses.associate { sense ->
        sense.sense.id to (sense.partsOfSpeech.map { partOfSpeech -> partOfSpeech.body }
            .toMutableList())
    }

    entry.senses.mapIndexed { i, sense ->
        // does the previous sense have any parts of speech and the current sense has none,
        // map it to the current body if it does
        if (i > 0) {
            if (sense.partsOfSpeech.isEmpty()) {
                val previousSense = entry.senses[i - 1]
                val previousSensePartsOfSpeech = mappings[previousSense.sense.id] ?: emptyList()
                mappings[sense.sense.id]?.addAll(previousSensePartsOfSpeech)
            }
        }
    }

    return mappings
}


fun formatDictionaryEntry(entry: DictionaryDatabaseEntry, context: Context): DictionaryEntry {
    val kanjiToReadings = mapKanjiToReading(entry)
    val sensesToPartsOfSpeech = mapSensesToPartsOfSpeech(entry)

    val hasKanjiEntry = entry.kanjiElements.isNotEmpty()

    // first kanji or the reading element if that doesn't exist
    val mainWordDisplay: WordDisplayWithInfo = if (hasKanjiEntry) {
        val firstKanji = entry.kanjiElements.first()
        val kanjiBody = firstKanji.kanjiElement.body
        val readingsOfTheFirstKanji = kanjiToReadings[firstKanji.kanjiElement.body]

        val kanjiWithReading =
            KanjiWithSingleReadingAndPriorities(
                kanjiBody,
                readingsOfTheFirstKanji?.readings?.first() ?: ReadingWithPriorities("", listOf())
            )

        WordDisplayWithInfo(
            WordDisplayData.KanjiWordDisplay(
                KanjiWithSingleReading(
                    kanjiWithReading.kanji,
                    kanjiWithReading.reading.reading
                )
            ),
            firstKanji.priority.map { priority -> priority.body })

    } else {
        val firstReading = entry.readingElements.first()
        val readingBody = firstReading.readingElement.body


        WordDisplayWithInfo(
            WordDisplayData.NormalWordDisplay(readingBody),
            firstReading.priority.map { priority -> priority.body })

    }


    val meanings = entry.senses.map { sense ->
        val partsOfSpeech = sensesToPartsOfSpeech[sense.sense.id]
        val partsOfSpeechMapped =
            partsOfSpeech?.map { partOfSpeech -> PartOfSpeech(context, partOfSpeech) }
                ?: listOf()

        Meaning(
            partsOfSpeechMapped,
            listOf(), // todo import these in the rust code as well
            sense.definitions.map { definition -> definition.body })
    }


    // we want combinations of readingElements -> kanji here, ignoring the first "mainWordDisplay"
    // kanji are always related with readings
    // reading elements are always related with at at least one kanji except when there is no kanji at all
    // todo handle re_nokanji entries

    val otherForms =
        // display alternate readings if the entry doesn't have any kanji entries
        if (!hasKanjiEntry) {
            val allExceptMain = entry.readingElements.slice(1..<entry.readingElements.size)

            allExceptMain.map { readingElement ->
                WordDisplayWithInfo(
                    WordDisplayData.NormalWordDisplay(readingElement.readingElement.body),
                    readingElement.priority.map { priority -> priority.body })
            }
        } else {
            // filter

            // need the filter the combination of kanji + reading, not just kanji
            // all combinations except the one in the mainWordDisplay

            val mainKanjiReading = when (mainWordDisplay.word) {
                is WordDisplayData.KanjiWordDisplay -> mainWordDisplay.word
                is WordDisplayData.NormalWordDisplay -> null
            }


            entry.kanjiElements.flatMap { kanjiElement ->
                val kanjiBody = kanjiElement.kanjiElement.body
                val readingsOfTheKanji = kanjiToReadings[kanjiBody]


                readingsOfTheKanji?.readings?.filter { reading ->
                    val sameReading = reading.reading == mainKanjiReading?.value?.reading
                    val sameKanji = kanjiBody == mainKanjiReading?.value?.kanji

                    !(sameReading && sameKanji)
                }?.map { reading ->
                    WordDisplayWithInfo(
                        word = WordDisplayData.KanjiWordDisplay(
                            KanjiWithSingleReading(
                                kanjiBody,
                                reading.reading
                            )
                        ),
                        priorities = reading.priorities
                    )
                } ?: listOf()
            }

        }


    val mainWord = mainWordDisplay.word

    val kanjiInMainWord: List<String> = when (mainWord) {
        is WordDisplayData.KanjiWordDisplay -> extractKanji(mainWord.value.kanji)
        is WordDisplayData.NormalWordDisplay -> listOf()
    }

    return DictionaryEntry(
        entry.entry.id,
        mainWordDisplay,
        meanings,
        otherForms,
        kanjiInMainWord,
        isFavorited = entry.entry.isFavorited ?: false
    )
}

