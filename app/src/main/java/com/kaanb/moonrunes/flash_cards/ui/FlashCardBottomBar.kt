package com.kaanb.moonrunes.flash_cards.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.flash_cards.dao.FlashCardState
import com.kaanb.moonrunes.flash_cards.util.daysToShortReadableTime
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun FlashCardBottomBar(
    flashCard: FlashCardState,
    modifier: Modifier = Modifier,
    onClickReview: (FsrsJni.ReviewType) -> Unit,
    onClickReveal: () -> Unit,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 8.dp), ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlashcardButon(
                    modifier = Modifier.weight(1f),
                    text = "Again",
                    color = Color(0xffa8403d),
                    smallText = daysToShortReadableTime(flashCard.reviewIntervals.againInterval),
                    onClick = { onClickReview(FsrsJni.ReviewType.Again) })

                FlashcardButon(
                    modifier = Modifier.weight(1f),

                    text = "Hard",
                    color = Color(0xffe28743),
                    smallText = daysToShortReadableTime(flashCard.reviewIntervals.hardInterval),
                    onClick = { onClickReview(FsrsJni.ReviewType.Hard) })
                FlashcardButon(
                    modifier = Modifier.weight(1f),
                    text = "Good",
                    color = Color(0xff298831),
                    smallText = daysToShortReadableTime(flashCard.reviewIntervals.goodInterval),
                    onClick = { onClickReview(FsrsJni.ReviewType.Good) })
                FlashcardButon(
                    modifier = Modifier.weight(1f),
                    text = "Easy",
                    color = Color(0xff44a9e3),
                    smallText = daysToShortReadableTime(flashCard.reviewIntervals.easyInterval),
                    onClick = {
                        onClickReview(
                            FsrsJni.ReviewType.Easy
                        )
                    })
            }

            Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), onClick = onClickReveal) {
                Text(stringResource(R.string.show_answer))
            }
        }


    }

}


@Preview
@Composable
private fun FlashCardBottomBarPreview() {
    MoonRunesTheme() {
        //FlashCardBottomBar(flashCard = null, onClickReview = {}, onClickReveal = {})
    }
}