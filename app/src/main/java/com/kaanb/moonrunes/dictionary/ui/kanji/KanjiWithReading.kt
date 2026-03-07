package com.kaanb.moonrunes.dictionary.ui.kanji

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KanjiWithReading(kanji: List<String>, readings: List<String>, modifier: Modifier = Modifier) {

}


@Composable
fun SingleKanjiWithReading(
    kanji: String,
    reading: String,
) {
    Column() {
        Text(
            reading, style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 6.sp
            ),
            // this is disgusting, but cjk fonts are all equal squares, so it should work
            modifier = Modifier.offset(y = 6.dp, x = 1.dp)
        )
        Text(
            kanji, style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview
@Composable
private fun SingleKanjiWithReadingPreview() {
    SingleKanjiWithReading("魚", "さかな")
}

