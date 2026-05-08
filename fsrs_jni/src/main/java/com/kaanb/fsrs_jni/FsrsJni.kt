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
        val memoryState: MemoryState?,
        @SerialName("scheduled_days")
        val scheduledDays: Int,
        @SerialName("last_review")
        val lastReview: Long,
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
    private external fun new_fsrs(): Long
    private external fun new_fsrs_from_state(state: ByteArray): Long
    private external fun free_fsrs(context_ptr: Long)
    private external fun dump_state(ctx_ptr: Long): ByteArray
    private external fun push_card(ctx_ptr: Long): Long
    private external fun get_due_cards_as_json(ctx_ptr: Long): String
    private external fun optimize(ctx_ptr: Long)
    private external fun review_card(ctx_ptr: Long, card_id: Long, review_type: Int)
    private external fun get_review_info_for_card(ctx_ptr: Long, card_id: Long): String

    private external fun does_card_with_id_exist(ctx_ptr: Long, card_id: Long): Boolean

    constructor(state: ByteArray? = null) {
        System.loadLibrary("fsrs_jni")
        if (state == null) {
            fsrsContextPointer = new_fsrs()
        } else {
            fsrsContextPointer = new_fsrs_from_state(state)
        }

        if (fsrsContextPointer == 0L) {
            Log.e(TAG ,"fsrsContextPointer is NULL on creation")
        }
    }

    /// why there is no destructors, or something like C#
    private fun free() {
        if (fsrsContextPointer != 0L) {
            free_fsrs(fsrsContextPointer)
            Log.e(TAG, "fsrsContextPointer is NULL and trying to free it")
        }
    }

    protected fun finalize(){
        free()
        Log.d(TAG, "freed fsrsContextPointer")
    }


    public fun pushCard(): Long {
        return push_card(fsrsContextPointer)
    }

    public fun getDueCards(): List<Card> {
        val dueCardsJson = get_due_cards_as_json(fsrsContextPointer)
        return Json.decodeFromString<List<Card>>(dueCardsJson)
    }

    public fun reviewCard(cardId: Long, reviewType: ReviewType) {
        val reviewTypeAsInt = reviewType.intoInt()
        review_card(fsrsContextPointer, cardId, reviewTypeAsInt)
    }

    public fun optimize() {
        optimize(fsrsContextPointer)
    }

    public fun dumpState(): ByteArray {
        return dump_state(fsrsContextPointer)
    }

    public fun getReviewInfoForCard(cardId: Long): CardReviewIntervals {
        val reviewInfoJson= get_review_info_for_card(fsrsContextPointer, cardId)
        return Json.decodeFromString<CardReviewIntervals>(reviewInfoJson)
    }

    public fun doesCardWithIdExist(cardId: Long):Boolean {
        return does_card_with_id_exist(fsrsContextPointer, cardId)
    }


    public fun recreateFromState(state: ByteArray) {
        free()
        fsrsContextPointer = new_fsrs_from_state(state)
    }


}