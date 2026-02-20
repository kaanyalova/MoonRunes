package com.kaanb.moonrunes.dao

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import java.io.File

@Entity
data class Entry(
    @PrimaryKey
    val id: Long,
)

@Entity
data class KanjiElement(
    @PrimaryKey
    val id: Long,
    val body: String,
    @ColumnInfo(name = "entry_fk")
    val entryFk: Long,
)

@Entity
data class Priority(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "element_fk")
    val elementFk: Long,
    val body: String,
)

@Entity
data class ReadingElement(
    @PrimaryKey
    val id: Long,
    val body: String,
    @ColumnInfo(name = "entry_fk")
    val entryFk: Long,
)


@Entity
data class Sense(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "entry_fk")
    val entryFk: Long,
)


@Entity
data class PartOfSpeech(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "sense_fk")
    val senseFk: Long,
    val body: String,
)

@Entity
data class Definition(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "sense_fk")
    val senseFk: Long,
    val body: String,
)

data class DictionaryEntry(
    @Embedded
    val entry: Entry,

    @Relation(
        parentColumn = "id",
        entityColumn = "entry_fk"
    )
    val kanjiElements: List<KanjiElement>,

    @Relation(
        parentColumn = "id",
        entityColumn = "entry_fk"
    )
    val readingElements: List<ReadingElement>,

    @Relation(
        parentColumn = "id",
        entityColumn = "entry_fk",
        entity = Sense::class
    )

    val senses: List<SenseWithInfo>,
)

data class SenseWithInfo(
    @Embedded
    val sense: Sense,
    @Relation(
        parentColumn = "id",
        entityColumn = "sense_fk"
    )
    val partsOfSpeech: List<PartOfSpeech>,

    @Relation(
        parentColumn = "id",
        entityColumn = "sense_fk"
    )
    val definitions: List<Definition>

)

// fake table because google sucks
// https://issuetracker.google.com/issues/146824830
@Entity
data class ReadingElementFts(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Long,
    val body: String
)


@Dao
interface DictionaryDao {
    @Transaction
    @Query("SELECT * FROM Entry LIMIT 10")
    fun getFirst10(): List<DictionaryEntry>
    @Transaction
    @RawQuery("""
       SELECT e.* FROM Entry e
    JOIN ReadingElement re ON re.entry_fk = e.id
    JOIN ReadingElementFts re_fts ON re_fts.rowid = re.id
    WHERE re_fts.body MATCH :input
    ORDER BY bm25(re_fts) -- Pass the table alias 're_fts', not 're_fts.body'
    LIMIT 10
    """)
    fun searchReadingElementTop10(input: String): List<DictionaryEntry>
}

@Database(
    entities = [Entry::class, KanjiElement::class, Priority::class, ReadingElement::class, Sense::class, PartOfSpeech::class, Definition::class, ReadingElementFts::class],
    version = 1
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}

fun connectToDb(context: Context): DictionaryDatabase {
    val file = File(context.filesDir, "dict.db")

    val db = Room.databaseBuilder(
        context,
        DictionaryDatabase::class.java,
        file.absolutePath
    ).allowMainThreadQueries()
        .build()

    return db
}