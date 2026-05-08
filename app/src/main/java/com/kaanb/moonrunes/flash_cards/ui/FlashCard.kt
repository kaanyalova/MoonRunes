package com.kaanb.moonrunes.flash_cards.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun FlashCard(modifier: Modifier = Modifier, flashCard: FlashCard?, showAnswer: Boolean) {
    Surface() {
        Column() {
            Surface(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    //if (flashCard != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hello", style = MaterialTheme.typography.displaySmall)

                        if (showAnswer) {
                            OutlinedCard(modifier = Modifier.padding(16.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .padding()
                                ) {
                                    Text(
                                        "AnswerPlaceHolder",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        "Example sentence blah blah",
                                        modifier = Modifier.padding(8.dp)
                                    )

                                    Button(onClick = {}) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                modifier = Modifier.padding(end = 8.dp),
                                                painter = painterResource(R.drawable.play_circle_24px),
                                                contentDescription = null
                                            )
                                            Text("Play Audio")
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
                flashCard = null,
                onClickReview = {},
                onClickReveal = {}
            )
        }

    }
}

@Preview
@Composable
private fun FlashCardPreview() {
    MoonRunesTheme() {
        FlashCard(flashCard = null, showAnswer = true)
    }
}