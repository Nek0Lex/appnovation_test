package com.example.appnovation_test.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnovation_test.model.ActionType
import com.example.appnovation_test.model.BankInfo
import com.example.appnovation_test.usecases.BanksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

interface BanksViewModel {
    val uiState: StateFlow<BanksUiState>
    val event: StateFlow<Event?>
    fun dispatch(action: Action)
    fun clearEvent()

    @HiltViewModel
    class Impl @Inject constructor (
        private val banksUseCase: BanksUseCase
    ) : BanksViewModel, ViewModel() {
        private val _uiState = MutableStateFlow(BanksUiState())
        override val uiState: StateFlow<BanksUiState> = _uiState.asStateFlow()
        
        private val _event = MutableStateFlow<Event?>(null)
        override val event: StateFlow<Event?> = _event.asStateFlow()

        override fun dispatch(action: Action) {
            // Handle user actions here
            when (action) {
                 is Action.OnClickActionDetail -> {
                     when (action.actionType) {
                         ActionType.LINK -> {
                             _event.tryEmit(Event.NavigateToDeepLink(url = action.url))
                         }
                         ActionType.PHONE -> {
                             _event.tryEmit(Event.NavigateToPhoneActivity(phoneNumber = action.url))
                         }

                         ActionType.OTHERS -> {}
                     }
                 }

                Action.Refresh -> {
                    _uiState.update { it.copy(isLoading = true) }
                    fetchBanks()
                }
            }
        }

        override fun clearEvent() {
            _event.value = null
        }

        init {
            fetchBanks()
        }

        private fun fetchBanks() {
            viewModelScope.launch {
                delay(2000L) // Simulate network delay
                val banksList = banksUseCase.invoke().getOrDefault(emptyList())
                _uiState.tryEmit(BanksUiState(banks = banksList, isLoading = false))
            }
        }
    }

    sealed interface Action {
        data class OnClickActionDetail(
            val actionType: ActionType,
            val url: String = "",
        ): Action
        data object Refresh : Action
    }

    sealed interface Event {
        data class NavigateToPhoneActivity(val phoneNumber: String) : Event
        data class NavigateToDeepLink(val url: String) : Event
    }

    data class BanksUiState(
        val banks: List<BankInfo> = emptyList(),
        val isLoading: Boolean = false,
    )
}