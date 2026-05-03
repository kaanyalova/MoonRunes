package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaanb.moonrunes.dictionary.util.WordDisplayData

@Composable
fun MainWordDisplay(modifier: Modifier = Modifier, word: WordDisplayData) {
    when (word) {
        is WordDisplayData.KanjiWordDisplay -> {
            SingleKanjiWithReading(modifier = modifier , kanji = word.value.kanji, reading =  word.value.reading)
        }

        is WordDisplayData.NormalWordDisplay -> Text(modifier = modifier, text =  word.value, style = MaterialTheme.typography.displayMedium)
    }
}