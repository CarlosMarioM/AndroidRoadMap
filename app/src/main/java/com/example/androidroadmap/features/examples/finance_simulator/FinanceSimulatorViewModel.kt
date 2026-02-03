package com.example.androidroadmap.features.examples.finance_simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.coin_layer.usecases.GetCoinLayerLiveUseCase
import com.example.androidroadmap.model.weather.CurrentWeatherApiRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceSimulatorViewModel @Inject constructor(
    private val getCoinLayerLiveUseCase: GetCoinLayerLiveUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CoinLayerUiState>(CoinLayerUiState.Loading)
    val uiState: StateFlow<CoinLayerUiState> = _uiState

    fun fetchRates(callback: String) {
        viewModelScope.launch {
            getCoinLayerLiveUseCase(callback)
                .collect { result ->
                    result.onSuccess { data ->
                        _uiState.value = CoinLayerUiState.Success(data)
                    }
                    result.onFailure { error ->
                        _uiState.value = CoinLayerUiState.Error(error)
                    }
                }
        }
    }
}

sealed interface CoinLayerUiState {
    data class Success(val data: String) : CoinLayerUiState
    data class Error(val exception: Throwable) : CoinLayerUiState
    data object Loading : CoinLayerUiState
}