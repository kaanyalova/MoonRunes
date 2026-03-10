package com.kaanb.moonrunes.dictionary.ui.dictionary_entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun NumberCircle(number: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(25.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.secondary

    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number.toString(),
            )
        }
    }
}

