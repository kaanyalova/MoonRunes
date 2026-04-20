package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.NumberCircle
import com.kaanb.moonrunes.dictionary.util.Meaning
import com.kaanb.moonrunes.dictionary.util.PartOfSpeech
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun MeaningList(modifier: Modifier = Modifier, meanings: List<Meaning>) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dictionary_meanings),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )



            meanings.forEachIndexed { i, meaning ->

                // parts of speech
                Row {
                    val joined =
                        meaning.partsOfSpeech.map { meaning -> meaning.toInternationalizedString() }
                            .filter { meaning ->
                                meaning != "" // todo: actually figure out why some entries are missing
                            }.distinct()
                            .joinToString("; ")

                    Text(
                        joined, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(4.dp)
                ) {

                    // these are aligned by the baseline of the numbers, maybe it would be better to align by
                    // the top of the circle if the text is more than one line better, but it would be
                    // a pain to implement, google translate also aligns by baseline, where the design was
                    // "inspired" from
                    NumberCircle(i + 1, modifier = Modifier.alignByBaseline())

                    val value = meaning.values.joinToString("; ")
                    Text(
                        text = value,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .alignByBaseline(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeaningListPreview() {

    val context = LocalContext.current
    val sampleMeanings = listOf(
        Meaning(
            partsOfSpeech = listOf(PartOfSpeech(context, "noun")),
            crossRefs = listOf(),
            values = listOf("first meaning", "second meaning")
        ), Meaning(
            partsOfSpeech = listOf(PartOfSpeech(context, "verb")),
            crossRefs = listOf(),
            values = listOf("third meaning")
        )
    )
    MoonRunesTheme {
        MeaningList(meanings = sampleMeanings)
    }
}
