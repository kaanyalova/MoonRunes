package com.kaanb.moonrunes.dictionary.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kaanb.moonrunes.dictionary.dao.DictionaryEntry
import kotlinx.serialization.json.Json

@Composable
fun DetailedDictionaryEntry(entry: DictionaryEntry, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        entry.kanjiElements.forEach { e ->
            Text(text = e.kanjiElement.body)
        }

        entry.readingElements.forEachIndexed { i, e ->
            Text(text = "$i ${e.readingElement.body}")
        }

        entry.senses.forEach { sense ->
            sense.definitions.forEach { definition ->
                Text(definition.body)
            }
        }
    }

}

@Preview
@Composable
private fun DetailedDictionaryEntryPreview() {

    // the reference is this
    // https://jisho.org/search/fish

    // reading elements other that idx 0 are "other forms"
    // entries need sorting based on the priority tags, maybe pre calculate the priorities in rust?

    // we won't have another pane like jisho instead we can have a button that shows
    // details about the kanji from kanjidic2 in another page

    val fishJson = """
      {"entry":{"id":1578010},"kanjiElements":[{"kanjiElement":{"id":56293,"body":"魚","entryFk":1578010},"priority":[{"id":91084,"elementFk":56293,"body":"ichi1"},{"id":91085,"elementFk":56293,"body":"ichi2"},{"id":91086,"elementFk":56293,"body":"news1"},{"id":91087,"elementFk":56293,"body":"nf03"}]}],"readingElements":[{"readingElement":{"id":66286,"body":"さかな","entryFk":1578010},"priority":[{"id":91088,"elementFk":66286,"body":"ichi1"},{"id":91089,"elementFk":66286,"body":"news1"},{"id":91090,"elementFk":66286,"body":"nf03"},{"id":103150,"elementFk":66286,"body":"spec1"}]},{"readingElement":{"id":66287,"body":"うお","entryFk":1578010},"priority":[{"id":91091,"elementFk":66287,"body":"ichi2"}]}],"senses":[{"sense":{"id":70985,"entryFk":1578010},"partsOfSpeech":[{"id":101827,"senseFk":70985,"body":"&n;"}],"definitions":[{"id":141893,"senseFk":70985,"body":"fish"}]}]}       """.trimIndent()

    val fish = Json.decodeFromString<DictionaryEntry>(fishJson);

    DetailedDictionaryEntry(fish)
}