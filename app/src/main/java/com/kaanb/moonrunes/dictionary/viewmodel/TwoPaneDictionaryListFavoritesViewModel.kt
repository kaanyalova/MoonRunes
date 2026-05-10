package com.kaanb.moonrunes.dictionary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.usecase.FavoriteDictionaryEntryUseCase
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val FAVORITES_TAG = "DictionaryFavoritesViewModel"

data class FavoritesListUiState(
    val entries: List<DictionaryEntry> = listOf(),
    val selectedEntry: DictionaryEntry? = null,
)

@HiltViewModel
class TwoPaneDictionaryListFavoritesViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase,
    private val favoriteDictionaryEntryUseCase: FavoriteDictionaryEntryUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val rawEntries = dictionaryRepository.getFavoritedEntries()
        val formatted = rawEntries.map { entry ->
            formatDictionaryEntry(entry, context)
        }
        _uiState.update { state ->
            state.copy(entries = formatted)
        }
    }

    fun selectEntry(id: Long) {
        val entry = getDictionaryEntryByIdUseCase(id)
        _uiState.update { state ->
            state.copy(selectedEntry = entry)
        }
    }

    fun onFavoriteButtonPressed() {
        val selectedEntry = _uiState.value.selectedEntry ?: run {
            Log.e(FAVORITES_TAG, "selected entry is null when pressing the favorite button")
            return
        }

        val currentFavoriteState = selectedEntry.isFavorited

        viewModelScope.launch {
            favoriteDictionaryEntryUseCase(selectedEntry.entryId, !currentFavoriteState)
        }

        _uiState.update { state ->
            state.copy(
                selectedEntry = selectedEntry.copy(isFavorited = !currentFavoriteState),
                // remove from list if unfavorited
                entries = if (currentFavoriteState) {
                    state.entries.filter { it.entryId != selectedEntry.entryId }
                } else {
                    state.entries
                }
            )
        }
    }
}