package com.kaanb.moonrunes.flash_cards.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.util.playAudioForWord
import com.kaanb.moonrunes.flash_cards.dao.FlashCardState
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun FlashCard(modifier: Modifier = Modifier, flashCard: FlashCardState, showAnswer: Boolean, onPressReview: (FsrsJni.ReviewType) -> Unit, onClickReveal: () -> Unit) {
    val context = LocalContext.current
    Surface() {
        Column() {
            Surface(modifier = modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    //if (flashCard != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(flashCard.card.word, style = MaterialTheme.typography.displaySmall)

                        if (showAnswer) {
                            OutlinedCard(modifier = Modifier.padding(16.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        flashCard.card.meaning,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        "Example sentence blah blah",
                                        modifier = Modifier.padding(8.dp)
                                    )

                                    Button(onClick = { playAudioForWord(context, flashCard.card.word) }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                modifier = Modifier.padding(end = 8.dp),
                                                painter = painterResource(R.drawable.play_circle_24px),
                                                contentDescription = null
                                            )
                                            Text(stringResource(R.string.dictionary_play_audio))
                                        }
                                    }
                                }

                            }
                        }
                    }
                    //}
                }
            }
            FlashCardBottomBar(
                onClickReview = onPressReview,
                flashCard = flashCard,
                onClickReveal = onClickReveal
            )
        }

    }
}

@Preview
@Composable
private fun FlashCardPreview() {
    MoonRunesTheme() {
        //FlashCard(flashCard = null, showAnswer = true)
    }
}