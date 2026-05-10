package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiEntry

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacter
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.MainWordDisplay
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.SingleKanjiWithReading
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.KanjiWithSingleReading
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.serialization.json.Json

@Composable
fun BriefKanjiEntry(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    entry: KanjiDicCharacter,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Unspecified),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = entry.literal, style = MaterialTheme.typography.displayMedium)
            //Text(
            //    entry.meanings.first().values.joinToString("; "),
            //    style = MaterialTheme.typography.bodyLarge
            //
            //)

            Text("${entry.misc.strokeCount.first().toString()} ${stringResource(R.string.strokes)}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)


        }
    }
}


@Preview
@Composable
private fun BriefKanjiEntryPreview() {
    val moonRune = """
{"literal":"亜","codepoints":{"values":[{"codepoint_type":"ucs","body":"4e9c"},{"codepoint_type":"jis208","body":"1-16-01"}]},"radicals":{"values":[{"radical_type":"classical","body":"7"},{"radical_type":"nelson_c","body":"1"}]},"misc":{"grade":"8","stroke_count":["7"],"variants":[{"variant_type":"jis208","body":"1-48-19"}],"frequency":"1509","jlpt":"1"},"dictionary_references":{"entries":[{"dictionary_type":"nelson_c","volume":"","page":"","body":"43"},{"dictionary_type":"nelson_n","volume":"","page":"","body":"81"},{"dictionary_type":"halpern_njecd","volume":"","page":"","body":"3540"},{"dictionary_type":"halpern_kkd","volume":"","page":"","body":"4354"},{"dictionary_type":"halpern_kkld","volume":"","page":"","body":"2204"},{"dictionary_type":"halpern_kkld_2ed","volume":"","page":"","body":"2966"},{"dictionary_type":"heisig","volume":"","page":"","body":"1809"},{"dictionary_type":"heisig6","volume":"","page":"","body":"1950"},{"dictionary_type":"gakken","volume":"","page":"","body":"1331"},{"dictionary_type":"oneill_names","volume":"","page":"","body":"525"},{"dictionary_type":"oneill_kk","volume":"","page":"","body":"1788"},{"dictionary_type":"moro","volume":"1","page":"0525","body":"272"},{"dictionary_type":"henshall","volume":"","page":"","body":"997"},{"dictionary_type":"sh_kk","volume":"","page":"","body":"1616"},{"dictionary_type":"sh_kk2","volume":"","page":"","body":"1724"},{"dictionary_type":"jf_cards","volume":"","page":"","body":"1032"},{"dictionary_type":"tutt_cards","volume":"","page":"","body":"1092"},{"dictionary_type":"kanji_in_context","volume":"","page":"","body":"1818"},{"dictionary_type":"kodansha_compact","volume":"","page":"","body":"35"},{"dictionary_type":"maniette","volume":"","page":"","body":"1827"}]},"query_codes":{"entries":[{"query_code_type":"skip","body":"4-7-1"},{"query_code_type":"sh_desc","body":"0a7.14"},{"query_code_type":"four_corner","body":"1010.6"},{"query_code_type":"deroo","body":"3273"}]},"reading_meaning":{"group":{"readings":[{"reading_type":"pinyin","body":"ya4"},{"reading_type":"korean_r","body":"a"},{"reading_type":"korean_h","body":"아"},{"reading_type":"vietnam","body":"A"},{"reading_type":"vietnam","body":"Á"},{"reading_type":"ja_on","body":"ア"},{"reading_type":"ja_kun","body":"つ.ぐ"}],"meanings":[{"language":"","body":"Asia"},{"language":"","body":"rank next"},{"language":"","body":"come after"},{"language":"","body":"-ous"},{"language":"fr","body":"Asie"},{"language":"fr","body":"suivant"},{"language":"fr","body":"sub-"},{"language":"fr","body":"sous-"},{"language":"es","body":"pref. para indicar"},{"language":"es","body":"venir después de"},{"language":"es","body":"Asia"},{"language":"pt","body":"Ásia"},{"language":"pt","body":"próxima"},{"language":"pt","body":"o que vem depois"},{"language":"pt","body":"-ous"}]}},"make_up_radicals":["｜","一","口"]}""".trimIndent()
    val parsed = Json.decodeFromString<KanjiDicCharacter>(moonRune)


    MoonRunesTheme() {
        BriefKanjiEntry(entry = parsed)
    }

}
