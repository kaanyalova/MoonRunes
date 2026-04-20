package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.NumberCircle
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry

@Composable
fun KanjiList(modifier: Modifier = Modifier, entry: DictionaryEntry) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dictionary_kanji_list_header),
                style = MaterialTheme.typography.titleMedium
            )


            entry.kanji.forEachIndexed { i, kanji ->
                Row {
                    NumberCircle(
                        i, modifier = Modifier
                            .alignByBaseline()
                            .padding(end = 8.dp)
                    )
                    Text(
                        kanji,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.alignByBaseline()
                    )
                }

                val isCollapsed by remember { mutableStateOf(false) }


            }
        }


    }

}


@Preview
@Composable
private fun KanjiListPreview() {
    //KanjiList()
}