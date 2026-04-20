package com.kaanb.moonrunes.flash_cards.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.serialization.json.Json

data class Furigana(
    val index: Int, val text: String
)


@Entity
data class FlashCardDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val wordReading: String,
    val containsKanji: Boolean,

    // there are fetched form a server
    val exampleSentence: String?,
    val exampleSentenceFuriganaListJson: String?,
    val exampleSentenceMeaning: String?,

    val audio: ByteArray
) {
    fun intoFlashCard(): FlashCard {
        val decodedFuriganaList = if (exampleSentenceFuriganaListJson != null) {
            val list = exampleSentenceFuriganaListJson;
            Json.decodeFromString<List<Furigana>>(list)
        } else {
            listOf<Furigana>()
        }

        val flashCard = FlashCard(
            word,
            wordReading,
            containsKanji,
            exampleSentence,
            decodedFuriganaList,
            exampleSentenceMeaning,
            audio
        )


        return flashCard
    }
}

data class FlashCard(
    val word: String,
    val wordReading: String,
    val containsKanji: Boolean,

    // there are fetched form a server
    val exampleSentence: String?,
    val exampleSentenceFuriganaListJson: List<Furigana>,
    val exampleSentenceMeaning: String?,

    val audio: ByteArray


) {
    public fun intoFlashCardDb(): FlashCardDb {
        val jsonFuriganaList = Json.encodeToString(exampleSentenceFuriganaListJson)
        return FlashCardDb(
            word = word,
            wordReading = wordReading,
            containsKanji = containsKanji,
            exampleSentence = exampleSentence,
            exampleSentenceFuriganaListJson = jsonFuriganaList,
            exampleSentenceMeaning = exampleSentenceMeaning,
            audio = audio
        )
    }
}


@Entity
data class FSRSStateDump(
    // there should only be a single entry so keep the
    // primary key as 1
    @PrimaryKey val id: Int = 1,
    val data: ByteArray?
)


@Dao
interface FlashCardDao {
    @Query(
        """
            SELECT * FROM FlashCardDb WHERE id = :id  
        """
    )
    fun getFlashCardById(id: Long): FlashCardDb


    @Insert
    fun addFlashCard(vararg flashCard: FlashCardDb)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateFSRSDump(vararg dump: FSRSStateDump)

    @Query(
        """
        SELECT * FROM FSRSStateDump LIMIT 1; 
    """
    )
    fun getFSRSDump(): FSRSStateDump?
}


@Database(
    entities = [FlashCardDb::class, FSRSStateDump::class], version = 1
)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
}




