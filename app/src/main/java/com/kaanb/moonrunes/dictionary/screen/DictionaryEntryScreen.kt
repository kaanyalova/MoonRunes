package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.DetailedDictionaryEntry
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.DictionaryEntryViewModel


@Composable
fun DictionaryEntryScreen(
    innerPadding: PaddingValues,
    entry: DictionaryEntry?,
    navigateToKanji: (String) -> Unit
) {
    if (entry != null) {
        DetailedDictionaryEntry(
            modifier = Modifier.padding(innerPadding),
            entry = entry,
            navigateToKanji = navigateToKanji
        )
    }
}

@Composable
fun DictionaryEntryScreen(
    innerPadding: PaddingValues,
    viewModel: DictionaryEntryViewModel = viewModel(),
    navigateToKanji: (String) ->Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DictionaryEntryScreen(
        innerPadding = innerPadding,
        entry = uiState.entry,
        navigateToKanji
    )

}