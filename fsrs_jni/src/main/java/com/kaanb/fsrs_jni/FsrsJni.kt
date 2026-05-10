package com.kaanb.fsrs_jni

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


const val TAG = "FsrsJni"

@Suppress("FunctionName")
class FsrsJni {
    public enum class ReviewType {
        Again,
        Hard,
        Good,
        Easy;


        fun intoInt(): Int {
            return when (this) {
                ReviewType.Again -> 0
                ReviewType.Hard -> 1
                ReviewType.Good -> 2
                ReviewType.Easy -> 3
            }
        }


        companion object {
            fun fromInt(value: Int): ReviewType {
                return when (value) {
                    0 -> ReviewType.Again
                    1 -> ReviewType.Hard
                    2 -> ReviewType.Good
                    3 -> ReviewType.Easy
                    else -> throw IllegalArgumentException("Invalid value for ReviewType: $value")
                }
            }
        }
    }

    @Serializable
    data class MemoryState(
        val stability: Float,
        val difficulty:Float
    )

    @Serializable
    data class Card(
        val id: Long,
        val due: Long,
        @SerialName("memory_state")
        val memoryState: MemoryState?,
        @SerialName("scheduled_days")
        val scheduledDays: Int,
        @SerialName("last_review")
        val lastReview: Long?,
    )

    @Serializable
    data class CardReviewIntervals (
        @SerialName("again_interval")
        val againInterval: Float,
        @SerialName("hard_interval")
        val hardInterval: Float,
        @SerialName("good_interval")
        val goodInterval: Float,
        @SerialName("easy_interval")
        val easyInterval: Float,
    )


    private var fsrsContextPointer: Long

    // External functions provided by the rust implementation see the fsrs_jni_rs file
    // for the implementation
    private external fun newFsrs(): Long
    private external fun newFromState(state: ByteArray): Long
    private external fun freeFsrs(context_ptr: Long)
    private external fun dumpState(ctx_ptr: Long): ByteArray
    private external fun createCard(ctx_ptr: Long, id: Long)
    private external fun getDueCardsAsJson(ctx_ptr: Long): String
    private external fun optimize(ctx_ptr: Long)
    private external fun reviewCard(ctx_ptr: Long, card_id: Long, review_type: Int)
    private external fun getReviewInfoForCard(ctx_ptr: Long, card_id: Long): String

    private external fun doesCardWithIdExist(ctx_ptr: Long, card_id: Long): Boolean
    private external fun deleteCard(ctx_ptr: Long, card_id: Long)

    constructor(state: ByteArray? = null) {
        System.loadLibrary("fsrs_jni")
        if (state == null) {
            fsrsContextPointer = newFsrs()
        } else {
            fsrsContextPointer = newFromState(state)
        }

        if (fsrsContextPointer == 0L) {
            Log.e(TAG ,"fsrsContextPointer is NULL on creation")
        }
    }

    /// why there is no destructors, or something like C#
    private fun free() {
        if (fsrsContextPointer != 0L) {
            freeFsrs(fsrsContextPointer)
            Log.e(TAG, "fsrsContextPointer is NULL and trying to free it")
        }
    }

    protected fun finalize(){
        free()
        Log.d(TAG, "freed fsrsContextPointer")
    }


    public fun createCard(id: Long) {
        createCard(fsrsContextPointer, id)
    }

    public fun getDueCards(): List<Card> {
        val dueCardsJson = getDueCardsAsJson(fsrsContextPointer)
        return Json.decodeFromString<List<Card>>(dueCardsJson)
    }

    public fun reviewCard(cardId: Long, reviewType: ReviewType) {
        val reviewTypeAsInt = reviewType.intoInt()
        reviewCard(fsrsContextPointer, cardId, reviewTypeAsInt)
    }

    public fun optimize() {
        optimize(fsrsContextPointer)
    }

    public fun dumpState(): ByteArray {
        return dumpState(fsrsContextPointer)
    }

    public fun getReviewInfoForCard(cardId: Long): CardReviewIntervals {
        try {
            val reviewInfoJson= getReviewInfoForCard(fsrsContextPointer, cardId)
            return Json.decodeFromString<CardReviewIntervals>(reviewInfoJson)
        } catch (e: Exception) {
            Log.d(TAG,"getReviewInfoForCard failed with $e")
        }

        return CardReviewIntervals(
            againInterval = 0f,
            hardInterval = 0f,
            goodInterval = 0f,
            easyInterval = 0f
        )

    }

    public fun doesCardWithIdExist(cardId: Long):Boolean {
        return doesCardWithIdExist(fsrsContextPointer, cardId)
    }

    public fun deleteCard(cardId: Long) {
        deleteCard(fsrsContextPointer, cardId)
    }


    public fun recreateFromState(state: ByteArray) {
        free()
        fsrsContextPointer = newFromState(state)
    }


}
