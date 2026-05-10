package com.kaanb.moonrunes.flash_cards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanb.fsrs_jni.FsrsJni
import com.kaanb.moonrunes.flash_cards.dao.FlashCardState
import com.kaanb.moonrunes.flash_cards.repository.FlashCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashCardUiState(
    val dueCards: List<FlashCardState> = listOf(),
    val showAnswer: Boolean = false,
)



@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val flashCardRepository: FlashCardRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(FlashCardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    dueCards = flashCardRepository.getDueCards()
                )
            }
        }
    }


    fun onClickReview(reviewType: FsrsJni.ReviewType){
        viewModelScope.launch {
            val cardId = _uiState.value.dueCards.first().card.id
            flashCardRepository.reviewCard(cardId, reviewType)

            val dueCards = flashCardRepository.getDueCards()
            _uiState.update {
                state -> state.copy(
                    dueCards = dueCards,
                    showAnswer = false
                )
            }
        }

    }

    fun didRanOutOfCards(): Boolean{
        return _uiState.value.dueCards.isEmpty()
    }

    fun showAnswer() {
        _uiState.update {
            state ->
            state.copy(
                showAnswer = !state.showAnswer
            )
        }
    }


}