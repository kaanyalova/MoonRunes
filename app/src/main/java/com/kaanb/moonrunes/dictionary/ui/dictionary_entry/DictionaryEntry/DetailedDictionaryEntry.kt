package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.OtherForms
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.PriorityTagOutlined
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.serialization.json.Json

@Composable
fun DetailedDictionaryEntry(entry: DictionaryEntry, modifier: Modifier = Modifier) {

    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val mainWord = entry.mainWordDisplay.word

            MainWordDisplay(word = mainWord)

            val mainPriorities = entry.mainWordDisplay.priorities

            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                mainPriorities.forEach { priority ->
                    PriorityTagOutlined(priority)
                }
            }


            MeaningList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), entry.meanings
            )


            if (entry.otherForms.isNotEmpty()) {
                OtherForms(otherForms = entry.otherForms, modifier = Modifier.fillMaxWidth())
            }

            if (entry.kanji.isNotEmpty()) {
                KanjiList(
                    entry = entry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

        }

    }


}


@Preview
@Composable
private fun DetailedDictionaryEntryPreview() {
    MoonRunesTheme() {
        // the reference is this
        // https://jisho.org/search/fish

        // reading elements other that idx 0 are "other forms"
        // entries need sorting based on the priority tags, maybe pre calculate the priorities in rust?

        // we won't have another pane like jisho instead we can have a button that shows
        // details about the kanji from kanjidic2 in another page

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

        DetailedDictionaryEntry(fishForDisplay)
    }

}