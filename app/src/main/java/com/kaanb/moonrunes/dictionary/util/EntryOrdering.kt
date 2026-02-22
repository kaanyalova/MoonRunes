package com.kaanb.moonrunes.dictionary.util

import com.kaanb.moonrunes.dictionary.dao.DictionaryEntry


fun orderEntries(entries: List<DictionaryEntry>, entryType: TextType): List<DictionaryEntry> {
    // - get top N from the database ranked
    // - put exact matches on front
    // - order by how common the entries are

    var entryMap = HashMap<Long, DictionaryEntry>()

    entries.forEach { it ->
        entryMap.put(it.entry.id, it)
    }

    /*
    val entriesThatAreExactMatches = when(entryType) {
        TextType.Romaji -> {
            entries.filter { entry ->

            }
        }
        TextType.ContainsKanji -> TODO()
        TextType.HiraganaOrKatakana -> TODO()
    }

     */

    TODO()
}