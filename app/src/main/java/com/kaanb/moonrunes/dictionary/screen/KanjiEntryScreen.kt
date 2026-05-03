package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithPaths
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiEntry.KanjiEntry
import com.kaanb.moonrunes.dictionary.viewmodel.KanjiEntryViewModel

@Composable
fun KanjiDictionaryEntryScreen(innerPadding: PaddingValues, entry: KanjiDicCharacterWithPaths?) {

    if (entry != null) {
        KanjiEntry(
            modifier = Modifier.padding(innerPadding),
            kanjiDicCharacter = entry.kanjiDicEntry,
            strokes = entry.paths
        )
    }

}

@Composable
fun KanjiDictionaryEntryScreen(
    innerPadding: PaddingValues,
    viewModel: KanjiEntryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KanjiDictionaryEntryScreen(
        //innerPadding = innerPadding,
        innerPadding = PaddingValues(0.dp), // TODO: wtf? where is the padding coming from
        entry = uiState.entry                   // why is the outlined boxes offset to bottom
    )
}

