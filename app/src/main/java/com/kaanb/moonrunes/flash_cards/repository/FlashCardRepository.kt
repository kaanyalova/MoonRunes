package com.kaanb.moonrunes.flash_cards.repository

import android.util.Log
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FSRSStateDump
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.flash_cards.dao.FlashCardDao
import com.kaanb.moonrunes.flash_cards.dao.FlashCardWithReviewInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "FsrsJniRepository"

@Singleton
class FlashCardRepository @Inject constructor(
    private val flashCardDao: FlashCardDao,
    private val fsrsJni: FsrsJni
) {

    init {
        GlobalScope.launch {
            loadState()
        }
    }

    suspend fun getFlashCardById(id: Long): FlashCardWithReviewInfo? {
        val data = flashCardDao.getFlashCardById(id)
        val doesTheCardExist = fsrsJni.doesCardWithIdExist(id)

        if (doesTheCardExist) {
            val reviewInfo = fsrsJni.getReviewInfoForCard(id)
            return FlashCardWithReviewInfo(data.into(), reviewInfo)
        } else {
            return null
        }

        //return data.into()
    }

    suspend fun getDueCards(): List<FlashCardWithReviewInfo> {
        val cards = fsrsJni.getDueCards()
        return cards.map { card ->
            // these must exist if they are in the context so it should never throw
            getFlashCardById(card.id)!!
        }
    }

    suspend fun addCard(
        card: FlashCard
    ) {
        val dbCard = card.into()
        val cardId = fsrsJni.pushCard()
        val dbCardWithId = dbCard.copy(id = cardId)
        flashCardDao.addFlashCard(dbCardWithId)
    }

    suspend fun saveState() {
        val state = fsrsJni.dumpState()
        flashCardDao.updateFSRSDump(FSRSStateDump(data = state))
    }


    suspend fun reviewCard(cardId: Long, reviewType: FsrsJni.ReviewType) {
        fsrsJni.reviewCard(cardId, reviewType)
    }


    suspend fun loadState() {
        val state = flashCardDao.getFSRSDump()

        if (state?.data != null) {
            fsrsJni.recreateFromState(state.data);
        } else {
            Log.w(TAG, "Could not load fsrs state from db")
        }
    }


}