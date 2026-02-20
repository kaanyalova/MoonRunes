package com.kaanb.moonrunes.ui.daily_word

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

data class DailyWordBrief(
    val kanji: String,
    val pronunciation: String,
    val meaning: String,
)


@Composable
fun DailyWord(modifier: Modifier = Modifier, word: DailyWordBrief) {
    

}


@Preview
@Composable
private fun DailyWordPreview() {
    
}