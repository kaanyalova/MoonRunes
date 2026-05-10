package com.kaanb.moonrunes.flash_cards.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kaanb.fsrs_jni.FsrsJni
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeout
import androidx.work.CoroutineWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@HiltWorker
class FlashCardOptimizerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val fsrsJni: FsrsJni
): CoroutineWorker(context, params) {
    private val TAG = "FlashCardOptimizerWorker"

    override suspend fun doWork(): Result {
        val time: Duration
        try {
            withTimeout(30.seconds) {
                time = measureTime {
                    fsrsJni.optimize()
                }
                Log.i(TAG, "optimized flash cards in ${time.inWholeMilliseconds}ms")
                return@withTimeout Result.success()
            }
            Log.e(TAG, "timeout after 30s when optimizing flash cards")
            return Result.failure()

        } catch (e: Exception) {
            Log.e(TAG, "error optimizing flash cards", e)
            return Result.failure()
        }
    }

}