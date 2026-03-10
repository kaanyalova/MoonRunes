package com.kaanb.moonrunes.dictionary.ui.dictionary_entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PriorityTag(tag: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(modifier = Modifier.padding(4.dp), text = tag)
        }
    }

}


@Composable
fun PriorityTagOutlined(tag: String, modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(modifier = Modifier.padding(4.dp), text = tag)
        }
    }

}

@Composable
fun SmallPriorityTagOutlined(tag: String, modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = tag,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

}

@Preview
@Composable
private fun PriorityTagPreview() {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PriorityTag("never")
        PriorityTag("gonna")
        PriorityTag("give")
        PriorityTag("you")
        PriorityTag("up")

        SmallPriorityTagOutlined("never")
        SmallPriorityTagOutlined("gonna")
        SmallPriorityTagOutlined("give")
        SmallPriorityTagOutlined("you")
        SmallPriorityTagOutlined("up")
    }
}