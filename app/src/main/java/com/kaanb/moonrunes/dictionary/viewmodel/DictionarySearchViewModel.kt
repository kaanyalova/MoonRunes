package com.kaanb.moonrunes.dictionary.viewmodel

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.lifecycle.ViewModel
import com.kaanb.moonrunes.dictionary.usecase.SearchAndFormatDictionaryEntriesUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

const val TAG = "DictionarySearchViewModel"

data class DictionarySearchUiState(
    val entries: List<DictionaryEntry> = listOf()
)

@HiltViewModel
class DictionarySearchViewModel @Inject constructor(
    private val searchAndFormatDictionaryEntriesUseCase: SearchAndFormatDictionaryEntriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DictionarySearchUiState())
    val uiState = _uiState.asStateFlow()

    val searchInputState = TextFieldState()

    fun search(searchInput: String) {
        _uiState.update { state ->
            state.copy(
                entries = searchAndFormatDictionaryEntriesUseCase(searchInput)
            )
        }

        Log.d(TAG, "searching dictionary entries with input ${searchInput}")

    }

}