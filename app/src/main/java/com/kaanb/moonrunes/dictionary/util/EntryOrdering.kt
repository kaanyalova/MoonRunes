package com.kaanb.moonrunes.dictionary.util

import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry


fun orderEntries(entries: List<DictionaryDatabaseEntry>, entryType: TextType): List<DictionaryDatabaseEntry> {
    // - get top N from the database ranked
    // - put exact matches on front
    // - order by how common the entries are

    var entryMap = HashMap<Long, DictionaryDatabaseEntry>()

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