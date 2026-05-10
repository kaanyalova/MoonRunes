package com.kaanb.moonrunes.dictionary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanb.moonrunes.dictionary.service.WotdService
import com.kaanb.moonrunes.dictionary.usecase.FavoriteDictionaryEntryUseCase
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WotdState(
    val word: DictionaryEntry?,
    val didFail: Boolean = false
)

@HiltViewModel
class WordOfTheDayViewModel @Inject constructor(
    private val wotdService: WotdService,
    private val getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase,
    private val favoriteDictionaryEntryUseCase: FavoriteDictionaryEntryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(WotdState(null))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val response = wotdService.getWotd()
                val id = response.id
                Log.d("WotdViewModel", "got wotd with id $id")
                val entry = getDictionaryEntryByIdUseCase(id)

                _uiState.update { state ->
                    state.copy(word = entry)
                }
            } catch (e: Exception) {
                Log.e("WotdViewModel", "error getting wotd", e)
                _uiState.update { state ->
                    state.copy(didFail = true)
                }
                return@launch
            }


        }
    }

    fun onFavoriteButtonPressed() {
        val favoriteState = _uiState.value.word?.isFavorited ?: false
        val id = _uiState.value.word?.entryId

        if (id == null) {
            //Log.e(_TAG, "selected entry is null when pressing the favorite button")
            return
        }


        viewModelScope.launch {
            favoriteDictionaryEntryUseCase(_uiState.value.word!!.entryId, !favoriteState)
        }

        _uiState.update { state ->
            state.copy(
                word = _uiState.value.word?.copy(isFavorited = !favoriteState)
            )
        }

    }


}