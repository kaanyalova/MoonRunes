package com.kaanb.moonrunes.flash_cards.repository

import android.util.Log
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FSRSStateDump
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDao
import com.kaanb.moonrunes.flash_cards.dao.FlashCardState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

const val TAG = "FsrsJniRepository"

@Singleton
class FlashCardRepository @Inject constructor(
    private val flashCardDao: FlashCardDao,
    private val fsrsJni: FsrsJni
) {

    init {
        GlobalScope.launch {
            val time = measureTimeMillis {
                loadState()
            }
            Log.d(TAG, "loaded flash card native code in $time ms")
        }

    }

    /*
    suspend fun getFlashCardFromDbById(id: Long): FlashCardWithReviewInfo? {
        val dbData = flashCardDao.getFlashCardById(id)

        val doesTheCardExist = fsrsJni.doesCardWithIdExist(id)

        if (doesTheCardExist) {
            val reviewInfo = fsrsJni.getReviewInfoForCard(id)
            return FlashCardWithReviewInfo(dbData.into(), reviewInfo)
        } else {
            return null
        }

        //return data.into()
    }

     */

    suspend fun getDueCards(): List<FlashCardState> {
        val cards = fsrsJni.getDueCards()
        return cards.map { card ->
            val databaseEntry = flashCardDao.getFlashCardById(card.id)
            val reviewIntervals = fsrsJni.getReviewInfoForCard(card.id)
            FlashCardState(databaseEntry.into(), card, reviewIntervals)
        }
    }

    suspend fun addCard(
        card: FlashCard
    ) {
        val dbCard = card.into()
        flashCardDao.addFlashCard(dbCard)
        fsrsJni.createCard(card.id)
        saveState()
    }

    suspend fun saveState() {
        var size = 0
        val time = measureTimeMillis {
            val state = fsrsJni.dumpState()
            size = state.size
            flashCardDao.updateFSRSDump(FSRSStateDump(data = state))
        }

        Log.d(TAG, "saved fsrs state with size $size bytes in ${time}ms")
    }


    suspend fun reviewCard(cardId: Long, reviewType: FsrsJni.ReviewType) {
        fsrsJni.reviewCard(cardId, reviewType)
        saveState()
    }


    suspend fun loadState() {
        val state = flashCardDao.getFSRSDump()

        if (state?.data != null) {
            fsrsJni.recreateFromState(state.data);

        } else {
            Log.w(TAG, "Could not load fsrs state from db")
        }
    }

    suspend fun deleteCard(id: Long) {
        flashCardDao.deleteCardById(id)
        fsrsJni.deleteCard(id)
        saveState()
    }




}
