package com.kaanb.moonrunes.dictionary.viewmodel

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.usecase.SearchAndFormatDictionaryEntriesUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "DictionarySearchViewModel"

data class DictionarySearchUiState(
    val entries: List<DictionaryEntry> = listOf()
)

@HiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
class DictionarySearchViewModel @Inject constructor(
    private val searchAndFormatDictionaryEntriesUseCase: SearchAndFormatDictionaryEntriesUseCase,
    private val getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DictionarySearchUiState())
    val uiState = _uiState.asStateFlow()

    val textFieldState = TextFieldState()


    init {
        viewModelScope.launch {
            snapshotFlow { textFieldState.text }
                .collectLatest { text ->
                    search(text.toString())
                }
        }

        _uiState.update {
            state ->
            state.copy(
                entries = searchAndFormatDictionaryEntriesUseCase("a")
            )
        }
    }

    fun getEntryById(id: Long): DictionaryEntry {
        return getDictionaryEntryByIdUseCase(id)
    }

    fun search(searchInput: String) {
        _uiState.update { state ->
            state.copy(
                entries = searchAndFormatDictionaryEntriesUseCase(searchInput)
            )
        }

        Log.d(TAG, "searching dictionary entries with input ${searchInput}")


        }


}