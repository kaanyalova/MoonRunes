package com.kaanb.moonrunes.dictionary.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class DictionaryEntryUiState(
    val entry: DictionaryEntry? = null
)

@HiltViewModel(assistedFactory = DictionaryEntryViewModel.Factory::class)
class DictionaryEntryViewModel @AssistedInject constructor(
    getDictionaryEntryByIdUseCase: GetDictionaryEntryByIdUseCase,
    @Assisted private val id: Long,
) : ViewModel() {


    private val _uiState = MutableStateFlow(DictionaryEntryUiState())
    val uiState = _uiState.asStateFlow()

    init {

        _uiState.update { state ->
            state.copy(
                entry = getDictionaryEntryByIdUseCase(id)
            )
        }
    }


    fun favorite() {

    }

    // https://github.com/android/nav3-recipes/blob/main/app/src/main/java/com/example/nav3recipes/passingarguments/viewmodels/hilt
    // how does google manage to make such disgusting apis wtf?
    @AssistedFactory
    interface Factory {
        fun create(navKey: Long): DictionaryEntryViewModel
    }

}