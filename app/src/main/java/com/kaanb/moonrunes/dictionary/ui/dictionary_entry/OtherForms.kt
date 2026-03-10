package com.kaanb.moonrunes.dictionary.ui.dictionary_entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.util.KanjiWithSingleReading
import com.kaanb.moonrunes.dictionary.util.KanjiWithSingleReadingAndPriorities
import com.kaanb.moonrunes.dictionary.util.ReadingWithPriorities
import com.kaanb.moonrunes.dictionary.util.WordDisplayData
import com.kaanb.moonrunes.dictionary.util.WordDisplayWithInfo
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme


// should i display the priorities or not?, they provide more info, but they look terrible
@Composable
fun OtherForms(modifier: Modifier = Modifier, otherForms: List<WordDisplayWithInfo>) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dictionary_other_forms),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            otherForms.forEachIndexed { i, otherForm ->
                Column {
                    val word = otherForm.word

                    //Row {
                    //    otherForm.priorities.forEach { priority ->
                    //        SmallPriorityTagOutlined(
                    //            priority,
                    //            modifier = Modifier.padding(horizontal = 2.dp)
                    //        )
                    //    }
                    //}

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        NumberCircle(
                            i + 1,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .alignByBaseline()
                        )

                        when (word) {
                            is WordDisplayData.KanjiWordDisplay -> {
                                Text(word.value.kanji, modifier = Modifier.alignByBaseline())
                                Icon(
                                    painter = painterResource(R.drawable.arrow_right_alt),
                                    contentDescription = null,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(word.value.reading, modifier = Modifier.alignByBaseline())
                            }

                            is WordDisplayData.NormalWordDisplay -> {
                                Text(word.value, modifier = Modifier.alignByBaseline())
                            }
                        }
                    }
                }


            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun OtherFormsPreview() {
    MoonRunesTheme {
        val sampleOtherForms = listOf(
            WordDisplayWithInfo(
                word = WordDisplayData.KanjiWordDisplay(
                    KanjiWithSingleReading("魚", "さかな")
                ), priorities = listOf("news1", "nf01")
            ), WordDisplayWithInfo(
                word = WordDisplayData.NormalWordDisplay(value = "たべる"), priorities = listOf()
            )
        )
        OtherForms(otherForms = sampleOtherForms)
    }
}
