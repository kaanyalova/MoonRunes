package com.kaanb.moonrunes.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import com.kaanb.moonrunes.dictionary.dao.KanjiDicCharacterWithPaths
import com.kaanb.moonrunes.dictionary.usecase.GetKanjiDicEntryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class KanjiDictionaryEntryUiState(
    val entry: KanjiDicCharacterWithPaths? = null,
)

@HiltViewModel(assistedFactory = KanjiEntryViewModel.Factory::class)
class KanjiEntryViewModel @AssistedInject constructor(
    getKanjiDicEntryUseCase: GetKanjiDicEntryUseCase,
    @Assisted private val literal: String,
): ViewModel() {
    private val _uiState = MutableStateFlow(KanjiDictionaryEntryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                entry = getKanjiDicEntryUseCase(literal)
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(literal: String): KanjiEntryViewModel
    }
}