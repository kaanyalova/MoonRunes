package com.kaanb.moonrunes.dictionary.dao

import androidx.room.RoomRawQuery

// dont order by fts.rank if the query is only a single letter
// query seems to take ~300ms if you run it with the order ranking
// ~50ms without it

fun searchTopNEntriesByReadingRawQuery(
    dao: DictionaryDao, input: String, n: Int
): List<DictionaryDatabaseEntry> {
    val query = RoomRawQuery(
        sql = """
            SELECT e.* FROM Entry e
            JOIN ReadingElement re ON re.entry_fk = e.id
            JOIN ReadingElementFts re_fts ON re_fts.rowid = re.id
            WHERE re_fts.body MATCH :input
            ORDER BY re.body = :input DESC, length(re.body) ASC
            LIMIT :n
        """.trimIndent(),

        onBindStatement = {
            it.bindText(1, input)
            it.bindInt(2, n)
        })


    val topN = dao.searchTopNEntriesByReading(query)
    return topN
}


fun searchTopNEntriesByDefinitionRawQuery(
    dao: DictionaryDao, input: String, n: Int
): List<DictionaryDatabaseEntry> {
    val query = RoomRawQuery(
        sql = """
            SELECT e.* FROM Entry e
            JOIN Sense s ON s.entry_fk = e.id
            JOIN Definition d ON d.sense_fk = s.id
            JOIN DefinitionFts d_fts ON d_fts.rowid = d.id
            WHERE d_fts.body MATCH :input
            ORDER BY d.body = :input DESC, length(d.body) ASC
            LIMIT :n
        """.trimIndent(),

        onBindStatement = {
            it.bindText(1, input)
            it.bindInt(2, n)
        })

    val topN = dao.searchTopNEntriesByDefinition(query)
    return topN
}


fun searchTopNEntriesByKanjiRawQuery(
    dao: DictionaryDao, input: String, n: Int
): List<DictionaryDatabaseEntry> {
    val query = RoomRawQuery(
        sql = """
        SELECT e.* FROM Entry e
        JOIN KanjiElement ke ON ke.entry_fk = e.id
        JOIN KanjiElementFts ke_fts ON ke_fts.rowid = ke.id
        WHERE ke_fts.body MATCH :input
        ORDER BY ke.body = :input DESC, length(ke.body) ASC
        LIMIT :n
    """.trimIndent(),

        onBindStatement = {
            it.bindText(1, input)
            it.bindInt(2, n)
        })

    val topN = dao.searchTopNEntriesByKanji(query)
    return topN
}


fun searchTopNEntriesByRomajiReadingRawQuery(
    dao: DictionaryDao, input: String, n: Int
): List<DictionaryDatabaseEntry> {
    val query = RoomRawQuery(
        sql = """
            SELECT e.* FROM Entry e
            JOIN RomajiReadingElement re ON re.entry_fk = e.id
            JOIN RomajiReadingElementFts re_fts ON re_fts.rowid = re.id
            WHERE re_fts.body MATCH :input_wildcard
            ORDER BY re.body = :input DESC, length(re.body) ASC
            LIMIT :n
        """.trimIndent(),

        onBindStatement = {
            it.bindText(1, "$input*")
            it.bindText(2, input)
            it.bindInt(3, n)
        })

    val topN = dao.searchTopNEntriesByRomajiReading(query)
    return topN
}