package com.kaanb.fsrs_jni

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NativeLibraryTest {
    val fsrs = FsrsJni()

    @Test
    fun addCard() {
        val pushedIds = mutableListOf<Long>()
        pushedIds.add(fsrs.pushCard())
        pushedIds.add(fsrs.pushCard())
        pushedIds.add(fsrs.pushCard())
    }
}
