package com.kaanb.moonrunes.dictionary.viewmodel

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.usecase.FavoriteDictionaryEntryUseCase
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.usecase.SearchAndFormatDictionaryEntriesUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.flash_cards.repository.FlashCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "DictionarySearchViewModel"

data class DictionarySearchUiState(
    val entries: List<DictionaryEntry> = listOf(),
    val selectedEntry: DictionaryEntry? = null,
    val dueCardCount: Int = 0
)

@HiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
class DictionarySearchViewModel @Inject constructor(
    private val searchAndFormatDictionaryEntriesUseCase: SearchAndFormatDictionaryEntriesUseCase,
    private val getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase,
    private val favoriteDictionaryEntryUseCase: FavoriteDictionaryEntryUseCase,
    private val flashCardRepository: FlashCardRepository
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

            _uiState.update { state ->
                state.copy(
                    dueCardCount = flashCardRepository.getDueCards().count()
                )
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


    fun selectEntry(id: Long) {
        _uiState.update { state ->
            state.copy(
                selectedEntry = getEntryById(id)
            )
        }
    }

    fun onFavoriteButtonPressed() {
        val favoriteState = _uiState.value.selectedEntry?.isFavorited ?: false
        val id = _uiState.value.selectedEntry?.entryId

        if (id == null) {
            Log.e(TAG, "selected entry is null when pressing the favorite button")
            return
        }


        viewModelScope.launch {
            favoriteDictionaryEntryUseCase(_uiState.value.selectedEntry!!.entryId, !favoriteState)
        }

        _uiState.update { state ->
            state.copy(
                selectedEntry = _uiState.value.selectedEntry?.copy(isFavorited = !favoriteState)
            )
        }

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