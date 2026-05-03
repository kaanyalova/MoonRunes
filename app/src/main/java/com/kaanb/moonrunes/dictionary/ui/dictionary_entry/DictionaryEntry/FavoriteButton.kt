package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

@Composable
fun FavoriteButton(modifier: Modifier = Modifier, isFavorited: Boolean, onClick: () -> Unit) {
    OutlinedIconButton(onClick = onClick, modifier = modifier.size(48.dp)) {
            if (isFavorited) {
                Icon(
                    painter = painterResource(R.drawable.favorite_filled_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.favorite_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

    }
}


@Preview
@Composable
private fun FavoriteButtonPreview() {
    MoonRunesTheme() {
        Surface() {
            Row {
                FavoriteButton(isFavorited = true, onClick = {})
                FavoriteButton(isFavorited = false, onClick = {})
            }

        }
    }
}