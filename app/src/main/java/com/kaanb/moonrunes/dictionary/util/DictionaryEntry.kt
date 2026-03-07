package com.kaanb.moonrunes.dictionary.util

import android.content.Context
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry

data class KanjiWithReadings(
    val readings: MutableList<String>,
    val kanjiInfo: MutableList<String>,
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

sealed class WordDisplay {
    data class KanjiWordDisplay(val value: KanjiWithSingleReading) : WordDisplay()
    data class NormalWordDisplay(val value: String) : WordDisplay()
}

data class WordDisplayWithInfo(
    val word: WordDisplay,
    val priorities: List<String>,
)


class PartOfSpeech(val context: Context, val id: String) {
    fun toInternationalizedString(): String {
        val resources = context.resources;
        val resourceId = resources.getIdentifier(id, "string", context.packageName)
        return context.getString(resourceId);
    }
}


// easier to display version of the other DictionaryEntry that comes from the database

data class DictionaryEntry(
    // the kanji/whatever that is displayed at top of the page, the first kanji or
    // the ReadingElement if the kanji doesn't exist.
    val mainWordDisplay: WordDisplayWithInfo,
    // (sub)meanings, parts of speech and crossrefs(related words?) for that specific meaning
    val meanings: List<Meaning>,
    // other forms, their priorities(
    val otherForms: List<WordDisplayWithInfo>
)


// create the mappings for kanji -> Readings
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
                e.value.readings.add(readingElement.readingElement.body)
            }
        } else {
            readingElement.kanjiMappings.forEach { kanjiMapping ->
                entries[kanjiMapping.body]?.readings?.add(readingElement.readingElement.body)
            }
        }

    }


    return entries
}


/*
// The parts of speech are mapped to the senses that come after them, unless another part of speech is introduced
// i am assuming it also maps to all of the sequential parts of speech that come after it as well
// sample from the raw xml of the dictionary, see the explanation of <pos> in the xml version of the dictionary
// for more info

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
            val doesCurrentSenseHavePartsOfSpeech = sense.partsOfSpeech.isEmpty()

            if (doesCurrentSenseHavePartsOfSpeech) {
                mappings[sense.sense.id]?.addAll(sense.partsOfSpeech.map { it -> it.body })

            } else {
                val previousSense = entry.senses[i - 1]
                val previousSensePartsOfSpeech = previousSense.partsOfSpeech.map { it.body }
                mappings[sense.sense.id]?.addAll(previousSensePartsOfSpeech)
            }
        }
    }

    return mappings
}


fun getDisplayData(entry: DictionaryDatabaseEntry, context: Context): DictionaryEntry {
    val kanjiToReadings = mapKanjiToReading(entry)
    val sensesToPartsOfSpeech = mapSensesToPartsOfSpeech(entry)

    val hasKanjiEntry = entry.kanjiElements.isNotEmpty()

    // first kanji or the reading element if that doesn't exist
    val mainWordDisplay: WordDisplayWithInfo = if (hasKanjiEntry) {
        val firstKanji = entry.kanjiElements.first()
        val kanjiBody = firstKanji.kanjiElement.body
        val readingsOfTheFirstKanji = kanjiToReadings[firstKanji.kanjiElement.body]

        val kanjiWithReading =
            KanjiWithSingleReading(kanjiBody, readingsOfTheFirstKanji?.readings?.first() ?: "")

        WordDisplayWithInfo(
            WordDisplay.KanjiWordDisplay(kanjiWithReading),
            firstKanji.priority.map { priority -> priority.body })

    } else {
        val firstReading = entry.readingElements.first()
        val readingBody = firstReading.readingElement.body


        WordDisplayWithInfo(
            WordDisplay.NormalWordDisplay(readingBody),
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
                    WordDisplay.NormalWordDisplay(readingElement.readingElement.body),
                    readingElement.priority.map { priority -> priority.body })
            }
        } else {
            // filter

            // need the filter the combination of kanji + reading, not just kanji
            // all combinations except the one in the mainWordDisplay

            val mainKanjiReading = when (mainWordDisplay.word) {
                is WordDisplay.KanjiWordDisplay -> mainWordDisplay.word
                is WordDisplay.NormalWordDisplay -> null
            }


            entry.kanjiElements.flatMap { kanjiElement ->
                val kanjiBody = kanjiElement.kanjiElement.body
                val readingsOfTheKanji = kanjiToReadings[kanjiBody]


                readingsOfTheKanji?.readings?.filter { reading ->
                    val sameReading = reading == mainKanjiReading?.value?.reading
                    val sameKanji = kanjiBody == mainKanjiReading?.value?.kanji

                    !(sameReading && sameKanji)
                }?.map { reading ->
                    WordDisplayWithInfo(
                        word = WordDisplay.KanjiWordDisplay(
                            KanjiWithSingleReading(
                                kanjiBody,
                                reading
                            )
                        ),
                        priorities = kanjiElement.priority.map { priority -> priority.body }
                    )
                } ?: listOf()
            }

        }


    return DictionaryEntry(
        mainWordDisplay,
        meanings,
        otherForms
    )
}

