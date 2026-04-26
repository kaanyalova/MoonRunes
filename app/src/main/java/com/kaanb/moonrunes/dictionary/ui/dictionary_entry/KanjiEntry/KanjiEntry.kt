package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiEntry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.KangxiRadicalTable
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacter
import com.kaanb.moonrunes.dictionary.dao.PathPiece
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiEntry.KanjiStrokeRenderer.KanjiStrokeGrid
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.NumberCircle
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.SingleKanjiWithReading
import com.kaanb.moonrunes.dictionary.util.kanjiStrokesToComposePaths
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.serialization.json.Json

@Composable
fun KanjiEntry(
    modifier: Modifier = Modifier, kanjiDicCharacter: KanjiDicCharacter, strokes: List<Path>
) {
    Surface(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SingleKanjiWithReading(
                kanji = kanjiDicCharacter.literal,
                reading = "" // No reading here duh!
            )

            // Small Misc
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "${kanjiDicCharacter.misc.strokeCount.first().toString()} Strokes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium

                )

                Text(
                    text = "Taught in grade ${kanjiDicCharacter.misc.grade}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            // TODO: Add JLPT level here


            // Radicals

            val hasRadicalData = kanjiDicCharacter.makeUpRadicals.isNotEmpty()

            if (hasRadicalData) {
                OutlinedCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Radicals",
                        modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                       val underlineColor = MaterialTheme.colorScheme.onSurfaceVariant


                        kanjiDicCharacter.makeUpRadicals.forEachIndexed { i, radical ->
                            val radicalName = KangxiRadicalTable.get(radical)


                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),

                            ) {

                                NumberCircle(
                                    i + 1,
                                    modifier = Modifier
                                        .alignByBaseline()
                                        .padding(end = 4.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.drawWithContent {
                                        drawContent()
                                        drawLine(
                                            color = underlineColor,
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height),
                                            strokeWidth = 1.5f
                                        )
                                    }.clickable(onClick = {}),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        radical,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (radicalName?.meaning != null) {
                                        Text(
                                            radicalName.meaning,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Text(
                                            "(${radicalName.hiragana})",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }


                                //Text(
                                //    "Go to definition",
                                //    style = MaterialTheme.typography.bodyMedium,
                                //    textDecoration = TextDecoration.Underline,
                                //    modifier = Modifier
                                //        .alignByBaseline()
                                //        .clickable { }
                                //        .wrapContentHeight()
                                //)
                            }
                        }
                    }

                }
            }


            // Readings, we only care about ja_on and ja_kun

            OutlinedCard(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Readings",
                    modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                val onReadings = kanjiDicCharacter.readingMeaning.group.readings
                    .filter { r -> r.readingType == "ja_on" }
                    .map { r -> r.body }
                    .joinToString(", ")

                val kunReadings = kanjiDicCharacter.readingMeaning.group.readings
                    .filter { r -> r.readingType == "ja_kun" }
                    .map { r -> r.body }
                    .joinToString(", ")


                if (kunReadings.isNotEmpty()) {
                    Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 4.dp)) {
                        Text(
                            "Kun:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(end = 4.dp)
                        )
                        Text(kunReadings, modifier = Modifier.alignByBaseline())
                    }

                }

                if (onReadings.isNotEmpty()) {
                    Row(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                        Text(
                            "On:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(end = 4.dp)
                        )
                        Text(onReadings, modifier = Modifier.alignByBaseline())

                    }
                }

            }


            // the language "" is english, for some reason
            kanjiDicCharacter.readingMeaning.group.meanings.filter { it -> it.language == "" }

            val meanings =
                kanjiDicCharacter.readingMeaning.group.meanings.filter { m -> m.language == "" }
                    .map { m -> m.body }

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                Text(
                    "Meanings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                )

                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    meanings.forEachIndexed { i, meaning ->
                        Row() {
                            NumberCircle(
                                i + 1,
                                modifier = Modifier
                                    .alignByBaseline()
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = meaning,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.alignByBaseline()
                            )
                        }

                    }

                }
            }


            // Radicals


            KanjiStrokeGrid(
                modifier = Modifier.padding(vertical = 8.dp),
                strokes = strokes,
                scale = 2f,
                showNumber = false,
            )


        }
    }


}

@Preview
@Composable
private fun KanjiEntryPreview() {
    val someWeirdLetter = """
{"literal":"亜","codepoints":{"values":[{"codepoint_type":"ucs","body":"4e9c"},{"codepoint_type":"jis208","body":"1-16-01"}]},"radicals":{"values":[{"radical_type":"classical","body":"7"},{"radical_type":"nelson_c","body":"1"}]},"misc":{"grade":"8","stroke_count":["7"],"variants":[{"variant_type":"jis208","body":"1-48-19"}],"frequency":"1509","jlpt":"1"},"dictionary_references":{"entries":[{"dictionary_type":"nelson_c","volume":"","page":"","body":"43"},{"dictionary_type":"nelson_n","volume":"","page":"","body":"81"},{"dictionary_type":"halpern_njecd","volume":"","page":"","body":"3540"},{"dictionary_type":"halpern_kkd","volume":"","page":"","body":"4354"},{"dictionary_type":"halpern_kkld","volume":"","page":"","body":"2204"},{"dictionary_type":"halpern_kkld_2ed","volume":"","page":"","body":"2966"},{"dictionary_type":"heisig","volume":"","page":"","body":"1809"},{"dictionary_type":"heisig6","volume":"","page":"","body":"1950"},{"dictionary_type":"gakken","volume":"","page":"","body":"1331"},{"dictionary_type":"oneill_names","volume":"","page":"","body":"525"},{"dictionary_type":"oneill_kk","volume":"","page":"","body":"1788"},{"dictionary_type":"moro","volume":"1","page":"0525","body":"272"},{"dictionary_type":"henshall","volume":"","page":"","body":"997"},{"dictionary_type":"sh_kk","volume":"","page":"","body":"1616"},{"dictionary_type":"sh_kk2","volume":"","page":"","body":"1724"},{"dictionary_type":"jf_cards","volume":"","page":"","body":"1032"},{"dictionary_type":"tutt_cards","volume":"","page":"","body":"1092"},{"dictionary_type":"kanji_in_context","volume":"","page":"","body":"1818"},{"dictionary_type":"kodansha_compact","volume":"","page":"","body":"35"},{"dictionary_type":"maniette","volume":"","page":"","body":"1827"}]},"query_codes":{"entries":[{"query_code_type":"skip","body":"4-7-1"},{"query_code_type":"sh_desc","body":"0a7.14"},{"query_code_type":"four_corner","body":"1010.6"},{"query_code_type":"deroo","body":"3273"}]},"reading_meaning":{"group":{"readings":[{"reading_type":"pinyin","body":"ya4"},{"reading_type":"korean_r","body":"a"},{"reading_type":"korean_h","body":"아"},{"reading_type":"vietnam","body":"A"},{"reading_type":"vietnam","body":"Á"},{"reading_type":"ja_on","body":"ア"},{"reading_type":"ja_kun","body":"つ.ぐ"}],"meanings":[{"language":"","body":"Asia"},{"language":"","body":"rank next"},{"language":"","body":"come after"},{"language":"","body":"-ous"},{"language":"fr","body":"Asie"},{"language":"fr","body":"suivant"},{"language":"fr","body":"sub-"},{"language":"fr","body":"sous-"},{"language":"es","body":"pref. para indicar"},{"language":"es","body":"venir después de"},{"language":"es","body":"Asia"},{"language":"pt","body":"Ásia"},{"language":"pt","body":"próxima"},{"language":"pt","body":"o que vem depois"},{"language":"pt","body":"-ous"}]}},"make_up_radicals":["｜","一","口"]}""".trimIndent()


    val parsed = Json.decodeFromString<KanjiDicCharacter>(someWeirdLetter)

    val json = """
        
        [[{"type":"MoveTo","x":23.38,"y":21.68},{"type":"CubicTo","x1":26.37,"y1":22.33,"x2":29.36,"y2":22.26,"x":32.39,"y":22.01},{"type":"CubicTo","x1":45.5,"y1":20.97,"x2":62.51,"y2":19.28,"x":78.38,"y":18.86},{"type":"CubicTo","x1":81.19,"y1":18.79,"x2":84.11,"y2":18.79,"x":86.88,"y":19.39}],[{"type":"MoveTo","x":20.0,"y":43.14},{"type":"CubicTo","x1":21.19,"y1":44.1,"x2":21.48,"y2":44.84,"x":21.82,"y":46.24},{"type":"CubicTo","x1":23.13,"y1":51.63,"x2":24.26,"y2":59.2,"x":25.25,"y":65.75},{"type":"CubicTo","x1":25.55,"y1":67.75,"x2":25.82,"y2":69.31,"x":26.06,"y":70.75}],[{"type":"MoveTo","x":22.75,"y":45.54},{"type":"CubicTo","x1":45.75,"y1":42.75,"x2":64.29,"y2":41.08,"x":83.64,"y":39.7},{"type":"CubicTo","x1":87.12,"y1":39.45,"x2":90.38,"y2":40.15,"x":89.03,"y":45.24},{"type":"CubicTo","x1":87.77,"y1":50.01,"x2":85.62,"y2":56.25,"x":83.76,"y":62.0}],[{"type":"MoveTo","x":26.5,"y":67.0},{"type":"CubicTo","x1":46.14,"y1":65.34,"x2":57.84,"y2":64.33,"x":76.75,"y":63.2},{"type":"CubicTo","x1":79.38,"y1":62.95,"x2":82.01,"y2":62.92,"x":84.64,"y":63.11}],[{"type":"MoveTo","x":42.12,"y":24.0},{"type":"CubicTo","x1":43.21,"y1":25.56,"x2":43.69,"y2":27.23,"x":43.58,"y":29.01},{"type":"CubicTo","x1":43.58,"y1":36.23,"x2":43.61,"y2":79.44,"x":43.61,"y":88.0}],[{"type":"MoveTo","x":62.62,"y":22.75},{"type":"CubicTo","x1":63.53,"y1":24.38,"x2":63.94,"y2":25.84,"x":63.83,"y":27.63},{"type":"CubicTo","x1":63.83,"y1":35.92,"x2":63.86,"y2":79.8,"x":63.86,"y":86.51}],[{"type":"MoveTo","x":15.88,"y":89.23},{"type":"CubicTo","x1":19.5,"y1":89.88,"x2":23.13,"y2":89.94,"x":26.5,"y":89.72},{"type":"CubicTo","x1":45.11,"y1":88.51,"x2":69.0,"y2":87.38,"x":86.62,"y":87.16},{"type":"CubicTo","x1":89.83,"y1":87.12,"x2":92.96,"y2":87.39,"x":96.12,"y":88.07}]]
    """.trimIndent()

    val value = Json.decodeFromString<List<List<PathPiece>>>(json)

    val paths = kanjiStrokesToComposePaths(value)

    MoonRunesTheme() {
        KanjiEntry(kanjiDicCharacter = parsed, strokes = paths)

    }

}