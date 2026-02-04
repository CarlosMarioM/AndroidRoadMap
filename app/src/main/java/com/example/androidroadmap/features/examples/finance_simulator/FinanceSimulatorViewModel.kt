package com.example.androidroadmap.features.examples.finance_simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.model.massive.TickerResponse
import com.example.androidroadmap.domain.repository.finance.usecases.GetTickerDetails
import com.example.androidroadmap.domain.repository.finance.usecases.GetTickersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FinanceSimulatorViewModel @Inject constructor(
    private val getTickersUseCase: GetTickersUseCase,
    private val getTickerDetails: GetTickerDetails
) : ViewModel() {
    private val _uiState = MutableStateFlow<FinanceUiState>(FinanceUiState.Loading)
    val uiState: StateFlow<FinanceUiState> = _uiState

    private val _liveCache = mutableMapOf<String, TickerResponse>()
    private val _liveCacheFlow = MutableStateFlow<Map<String, TickerResponse>>(_liveCache)
    val liveCache : StateFlow<Map<String, TickerResponse>> = _liveCacheFlow

    init {
        fetchRates()
    }

    fun fetchRates(limit: Int? = null) {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
              getTickersUseCase(limit).let {
                  it.fold(
                      onSuccess = {ticker->
                          _liveCache[ticker.requestId] = ticker
                          _uiState.value = FinanceUiState.Success(ticker)
                      },
                      onFailure = { error->
                          _uiState.value = FinanceUiState.Error(error)
                      }
                  )
              }
        }
    }

}

sealed interface FinanceUiState {
    data class Success<T>(val data: T) : FinanceUiState
    data class Error(val exception: Throwable) : FinanceUiState
    data object Loading : FinanceUiState
}