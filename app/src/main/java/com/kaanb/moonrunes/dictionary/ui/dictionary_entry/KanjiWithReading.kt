package com.kaanb.moonrunes.dictionary.ui.dictionary_entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SingleKanjiWithReading(
    kanji: String,
    reading: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column {
            Text(
                reading, style = MaterialTheme.typography.bodyLarge,
                // this is disgusting, but cjk fonts are all equal squares, so it should work
                modifier = Modifier.offset(y = 10.dp, x = 1.dp)
            )
            Text(
                kanji, style = MaterialTheme.typography.displayMedium
            )
        }
    }

}

@Preview
@Composable
private fun SingleKanjiWithReadingPreview() {
    SingleKanjiWithReading("魚", "さかな")
}

