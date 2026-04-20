package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.serialization.json.Json

@Composable
fun BriefDictionaryEntry(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    entry: DictionaryEntry,
    onClick: (id: Long) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(), onClick = { onClick(entry.entryId) },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Unspecified)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            MainWordDisplay(word = entry.mainWordDisplay.word)
            Text(
                entry.meanings.first().values.joinToString("; "),
                style = MaterialTheme.typography.bodyLarge

            )

        }
    }
}

@Preview
@Composable
private fun BriefDictionaryEntryPreview() {
    MoonRunesTheme() {
        val fishJson = """
{
  "entry": {
    "id": 1578010
  },
  "kanjiElements": [
    {
      "kanjiElement": {
        "id": 56293,
        "body": "魚",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91084,
          "elementFk": 56293,
          "body": "ichi1"
        },
        {
          "id": 91085,
          "elementFk": 56293,
          "body": "ichi2"
        },
        {
          "id": 91086,
          "elementFk": 56293,
          "body": "news1"
        },
        {
          "id": 91087,
          "elementFk": 56293,
          "body": "nf03"
        }
      ]
    }
  ],
  "readingElements": [
    {
      "readingElement": {
        "id": 66286,
        "body": "さかな",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91088,
          "elementFk": 66286,
          "body": "ichi1"
        },
        {
          "id": 91089,
          "elementFk": 66286,
          "body": "news1"
        },
        {
          "id": 91090,
          "elementFk": 66286,
          "body": "nf03"
        },
        {
          "id": 103150,
          "elementFk": 66286,
          "body": "spec1"
        }
      ]
    },
    {
      "readingElement": {
        "id": 66287,
        "body": "うお",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91091,
          "elementFk": 66287,
          "body": "ichi2"
        }
      ]
    }
  ],
  "senses": [
    {
      "sense": {
        "id": 70985,
        "entryFk": 1578010
      },
      "partsOfSpeech": [
        {
          "id": 101827,
          "senseFk": 70985,
          "body": "&n;"
        }
      ],
      "definitions": [
        {
          "id": 141893,
          "senseFk": 70985,
          "body": "fish"
        }
      ]
    }
  ]
}      
      """.trimIndent()


        val fish = Json.decodeFromString<DictionaryDatabaseEntry>(fishJson);
        val fishForDisplay = formatDictionaryEntry(fish, LocalContext.current)

        Log.d("fish", fishForDisplay.toString())

        BriefDictionaryEntry(entry = fishForDisplay)
    }


}