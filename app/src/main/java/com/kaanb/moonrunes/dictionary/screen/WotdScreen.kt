package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.DetailedDictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.WordOfTheDayViewModel

@Composable
fun WordOfTheDayScreen(modifier: Modifier = Modifier, viewModel: WordOfTheDayViewModel = hiltViewModel(), navigateToKanji: (String) -> Unit ) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val word = state.value.word

    if (word != null) {
        DetailedDictionaryEntry(onFavoriteButtonPressed = viewModel::onFavoriteButtonPressed, entry = word, navigateToKanji = navigateToKanji)
    }

    if (word == null && state.value.didFail) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.wotd_failed), style = MaterialTheme.typography.titleLarge)
        }
    }
}