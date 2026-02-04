package com.example.androidroadmap.features.examples.finance_simulator.ticker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.repository.finance.usecases.GetTickerDetails
import com.example.androidroadmap.features.examples.finance_simulator.FinanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickerViewModel @Inject constructor(
    savedInstanceState: SavedStateHandle,
    private val getTickerDetails: GetTickerDetails
) : ViewModel() {
    private val _uiState = MutableStateFlow<TickerUiState>(TickerUiState.Loading)
    val uiState: StateFlow<TickerUiState> = _uiState

    // 1. Automatically grab the "ticker_symbol" from the Intent
    private val tickerSymbol: String = savedInstanceState.get<String>("ticker")
        ?: throw IllegalArgumentException("Ticker symbol is required")

    init { fetchTickerDetails() }

    fun fetchTickerDetails(date: String? = null) {
        viewModelScope.launch {
            _uiState.value = TickerUiState.Loading
            getTickerDetails(tickerSymbol, date).let {
                it.fold(
                    onSuccess = { details ->
                        _uiState.value = TickerUiState.Success(details)
                    },
                    onFailure = { exception ->
                        _uiState.value = TickerUiState.Error(exception.message ?: "Unknown error")
                    }
                )
            }
        }
    }
}

sealed interface TickerUiState {
    data class Success<T>(val data: T) : TickerUiState
    data object Loading : TickerUiState
    data class Error(val error: String) : TickerUiState
}