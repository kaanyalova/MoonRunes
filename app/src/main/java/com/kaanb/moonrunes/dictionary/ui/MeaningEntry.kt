package com.kaanb.moonrunes.dictionary.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun MeaningEntry(
    modifier: Modifier = Modifier,
    primaryText: @Composable () -> Unit,
    additionalReadings: List<String>,
) {

    Column {
        primaryText()

        additionalReadings.forEach { additionalReading ->
            Text("Also $additionalReading")
        }
    }

}