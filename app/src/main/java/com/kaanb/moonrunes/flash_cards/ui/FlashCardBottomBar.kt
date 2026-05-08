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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FlashCard
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun FlashCardBottomBar(
    flashCard: FlashCard?,
    modifier: Modifier = Modifier,
    onClickReview: (FsrsJni.ReviewType) -> Unit,
    onClickReveal: () -> Unit,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlashcardButon(
                    text = "Again",
                    color = Color(0xffa8403d),
                    smallText = "10m",
                    onClick = { onClickReview(FsrsJni.ReviewType.Again) })
                FlashcardButon(
                    text = "Hard",
                    color = Color(0xffe28743),
                    smallText = "1d",
                    onClick = { onClickReview(FsrsJni.ReviewType.Hard) })
                FlashcardButon(
                    text = "Good",
                    color = Color(0xff298831),
                    smallText = "3d",
                    onClick = { onClickReview(FsrsJni.ReviewType.Good) })
                FlashcardButon(
                    text = "Easy",
                    color = Color(0xff44a9e3),
                    smallText = "8d",
                    onClick = {
                        onClickReview(
                            FsrsJni.ReviewType.Easy
                        )
                    })
            }

            Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), onClick = onClickReveal) {
                Text("Show Answer")
            }
        }


    }

}


@Preview
@Composable
private fun FlashCardBottomBarPreview() {
    MoonRunesTheme() {
        FlashCardBottomBar(flashCard = null, onClickReview = {}, onClickReveal = {})
    }
}