package com.kaanb.moonrunes.flash_cards.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FlashcardButon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    smallText: String = "",
    color: Color,
    onlyColorTheBorder: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.width(90.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text, style =
                    if (onlyColorTheBorder) {
                        MaterialTheme.typography.bodySmall
                    } else {
                        MaterialTheme.typography.bodySmall.copy(color = color)
                    }
            )

            Text(smallText, style = MaterialTheme.typography.bodySmall)
        }

    }
}