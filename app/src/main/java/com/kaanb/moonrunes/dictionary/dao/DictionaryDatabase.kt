package com.kaanb.moonrunes.dictionary.dao

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import kotlinx.serialization.Serializable
import java.io.File

@Entity
@Serializable
data class Entry(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "is_favorited") val isFavorited: Boolean?
)

@Entity(
    indices = [Index(
        value = ["entry_fk"], name = "idx_kanjielement_entry"
    )]
)
@Serializable
data class KanjiElement(
    @PrimaryKey val id: Long, val body: String, @ColumnInfo(name = "entry_fk") val entryFk: Long,

    )


@Serializable
data class KanjiElementWithPrioritiesAndInformation(
    @Embedded val kanjiElement: KanjiElement,
    @Relation(parentColumn = "id", entityColumn = "element_fk") val priority: List<Priority>,
    @Relation(
        parentColumn = "id",
        entityColumn = "kanji_element_fk"
    ) val kanjiInfo: List<KanjiInformation> = mutableListOf(),
)


@Entity
data class KanjiDicEntry(
    @PrimaryKey val id: Long,
    val body: String,
    @ColumnInfo(name = "json_data")
    val jsonData: String,
    @ColumnInfo(name= "svg_data")
    val svgData: String?
)


@Serializable
data class ReadingElementWithPrioritiesAndKanjiMappings(
    @Embedded val readingElement: ReadingElement,
    @Relation(parentColumn = "id", entityColumn = "element_fk") val priority: List<Priority>,
    @Relation(
        parentColumn = "id",
        entityColumn = "reading_fk"
    ) val kanjiMappings: List<MapsToKanji> = arrayListOf()
)


@Entity(
    indices = [Index(
        value = ["element_fk"], name = "idx_priority_element"
    )]
)
@Serializable
data class Priority(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "element_fk") val elementFk: Long,
    val body: String,
)

@Entity
@Serializable
data class KanjiInformation(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "kanji_element_fk") val kanjiElementFk: Long,
    val body: String,
)

@Entity(
    indices = [Index(
        value = ["entry_fk"], name = "idx_readingelement_entry"
    )]
)
@Serializable
data class ReadingElement(
    @PrimaryKey val id: Long,
    val body: String,
    @ColumnInfo(name = "entry_fk") val entryFk: Long,
)


@Entity(
    indices = [Index(
        value = ["entry_fk"], name = "idx_sense_entry"
    )]
)
@Serializable
data class Sense(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "entry_fk") val entryFk: Long,
)


@Entity(
    indices = [Index(
        value = ["sense_fk"], name = "idx_partofspeech_sense"
    )]
)
@Serializable
data class PartOfSpeech(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "sense_fk") val senseFk: Long,
    val body: String,
)

@Entity(
    indices = [Index(
        value = ["sense_fk"], name = "idx_definition_sense"
    )]
)
@Serializable
data class Definition(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "sense_fk") val senseFk: Long,
    val body: String,
)

@Serializable
data class DictionaryDatabaseEntry(
    @Embedded val entry: Entry,


    @Relation(
        parentColumn = "id", entityColumn = "entry_fk", entity = KanjiElement::class
    ) val kanjiElements: List<KanjiElementWithPrioritiesAndInformation>,

    @Relation(
        parentColumn = "id", entityColumn = "entry_fk", entity = ReadingElement::class
    ) val readingElements: List<ReadingElementWithPrioritiesAndKanjiMappings>,

    @Relation(
        parentColumn = "id", entityColumn = "entry_fk", entity = Sense::class
    ) val senses: List<SenseWithInfo>,


    )

@Serializable
data class SenseWithInfo(
    @Embedded val sense: Sense, @Relation(
        parentColumn = "id", entityColumn = "sense_fk"
    ) val partsOfSpeech: List<PartOfSpeech>,

    @Relation(
        parentColumn = "id", entityColumn = "sense_fk"
    ) val definitions: List<Definition>

)

// fake table because google sucks
// https://issuetracker.google.com/issues/146824830
@Serializable
@Entity
data class ReadingElementFts(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowId: Long, val body: String
)


@Serializable
@Entity
// this is here when there is multiple kanji that maps to the same/multiple readings,
// so it maps the reading to the kanji's body, so the current reading element is bound to the body here
data class MapsToKanji(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "reading_fk") val readingFk: Long,
    val body: String,
)

@Dao
interface DictionaryDao {
    @Transaction
    @Query("SELECT * FROM Entry LIMIT 10")
    fun getFirst10(): List<DictionaryDatabaseEntry>

    @Transaction
    @RawQuery
    fun searchTopNEntriesByReading(query: RoomRawQuery): List<DictionaryDatabaseEntry>

    @Transaction
    @RawQuery
    fun searchTopNEntriesByKanji(query: RoomRawQuery): List<DictionaryDatabaseEntry>

    @Transaction
    @RawQuery
    fun searchTopNEntriesByDefinition(query: RoomRawQuery): List<DictionaryDatabaseEntry>

    @Transaction
    @RawQuery
    fun searchTopNEntriesByRomajiReading(query: RoomRawQuery): List<DictionaryDatabaseEntry>

    @Transaction
    @Query(
        """
           SELECT e.* FROM Entry e WHERE e.id = :id
    """
    )
    fun getEntryById(id: Long): DictionaryDatabaseEntry

    @Transaction
    @Query(
        """
           SELECT * FROM Entry WHERE is_favorited = 1
    """
    )
    fun getFavoritedEntries(): List<DictionaryDatabaseEntry>

    @Query("SELECT * FROM KanjiDicEntry WHERE body = :body")
    fun getKanjiDicEntry(body: String): KanjiDicEntry

    @Query("UPDATE Entry SET is_favorited = :state WHERE id = :id")
    fun setEntryFavoriteState(id: Long, state: Boolean)
}




@Database(
    entities = [
        Entry::class,
        KanjiElement::class,
        Priority::class,
        ReadingElement::class,
        Sense::class,
        PartOfSpeech::class,
        Definition::class,
        KanjiInformation::class,
        MapsToKanji::class,
        KanjiDicEntry::class
    ],
    version = 1
)


abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}

