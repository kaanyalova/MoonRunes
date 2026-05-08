package com.kaanb.moonrunes.flash_cards.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanb.moonrunes.flash_cards.dao.Furigana
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

const val TAG = "FuriganaRenderer"

@Composable
fun FuriganaText(
    modifier: Modifier = Modifier,
    text: String,
    furigana: List<Furigana>,
    mainTextTypography: TextStyle = MaterialTheme.typography.bodyLarge,
    furiganaTypography: TextStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
) {
    val measurer = rememberTextMeasurer()
    val furiganaOffsets =
        measureFuriganaOffsets(LocalDensity.current, mainTextTypography, measurer, text, furigana);
    val height = measureHeight(LocalDensity.current, measurer, mainTextTypography, text)


    Column() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box {
                Text(text = text, style = mainTextTypography, modifier = Modifier.padding())



                furigana.forEachIndexed { i, furigana ->
                    Text(
                        text = furigana.text,
                        style = furiganaTypography,
                        modifier = Modifier.offset(x = furiganaOffsets[i], y = -height + 12.dp)
                    )
                }

                Log.d(TAG, "FuriganaText: $furiganaOffsets")

            }

            }

    }


}


fun measureFuriganaOffsets(
    density: Density,
    mainTextTypography: TextStyle,
    textMeasurer: TextMeasurer,
    text: String,
    furigana: List<Furigana>
): List<Dp> {
    return furigana.map { furigana ->
        val measured = textMeasurer.measure(
            text = text.slice(0..<furigana.index),
            style = mainTextTypography
        )

        with(density) { measured.size.width.toDp() }
    }
}

fun measureHeight(
    density: Density,
    textMeasurer: TextMeasurer,
    typography: TextStyle,
    text: String
): Dp {
    val measured = textMeasurer.measure(
        text = text,
        style = typography
    )

    return with(density) { measured.size.height.toDp() }
}


@Preview
@Composable
private fun Furigana() {
    MoonRunesTheme() {
        Surface() {
            FuriganaText(
                text = "私は　日本語　好き　です" , furigana =
                    listOf(
                    Furigana(0, "わたし"),
                    Furigana(index = 3, text = "にほんご"),
                    Furigana(index = 7, text = "すき")
                )
            )
        }

    }

}