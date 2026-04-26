package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiEntry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacter
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.MainWordDisplay
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.SingleKanjiWithReading
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.KanjiWithSingleReading

@Composable
fun BriefKanjiEntry(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    entry: KanjiDicCharacter,
    onClick: (id: Long) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(), onClick = {  },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Unspecified)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = entry.literal, style = MaterialTheme.typography.displayMedium)
            //Text(
            //    entry.meanings.first().values.joinToString("; "),
            //    style = MaterialTheme.typography.bodyLarge
            //
            //)

        }
    }
}


