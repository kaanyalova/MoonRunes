package com.kaanb.moonrunes.flash_cards.repository

import android.util.Log
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FSRSStateDump
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDao
import javax.inject.Inject

const val TAG = "FsrsJniRepository"

class FlashCardRepository @Inject constructor(
    private val flashCardDao: FlashCardDao,
    private val fsrsJni: FsrsJni
) {
    suspend fun getFlashCardById(id: Long): FlashCard {
        val data = flashCardDao.getFlashCardById(id)
        return data.intoFlashCard()
    }

    suspend fun getDueCards(): List<FlashCard> {
        val cards = fsrsJni.getDueCards()
        return cards.map { card ->
            getFlashCardById(card.id)
        }
    }

    suspend fun addCard(
        card: FlashCard
    ) {
        val dbCard = card.intoFlashCardDb()
        flashCardDao.addFlashCard(dbCard)
    }

    suspend fun saveState() {
        val state = fsrsJni.dumpState()
        flashCardDao.updateFSRSDump(FSRSStateDump(data = state))
    }

    suspend fun loadState() {
        val state = flashCardDao.getFSRSDump()

        if (state?.data != null) {
            fsrsJni.recreateFromState(state.data);
        } else {
            Log.w(TAG,"Could not load fsrs state from db")
        }
    }


}