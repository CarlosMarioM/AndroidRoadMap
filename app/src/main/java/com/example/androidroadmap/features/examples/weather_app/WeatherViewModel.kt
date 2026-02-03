package com.example.androidroadmap.features.examples.weather_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidroadmap.domain.location.usecases.GetCurrentLocationUseCase
import com.example.androidroadmap.domain.weather.usecases.GetCurrentWeatherUseCase
import com.example.androidroadmap.model.weather.CurrentWeatherApiRes
import com.example.androidroadmap.model.weather.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            viewModelScope.launch {
                _uiState.value = WeatherUiState.Loading
                getLocation()
            }
        }
    }

    suspend fun getLocation() {
        try {
            val location = getCurrentLocationUseCase()
            getCurrentWeather(location)
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(e)
        }
    }

    suspend fun getCurrentWeather(location: Location) {
        try {
            getCurrentWeatherUseCase(location.latitude, location.longitude).let {
                it.fold(
                    onSuccess = { weather ->
                        _uiState.value = WeatherUiState.Success(weather)
                    }, onFailure = { error ->
                        _uiState.value = WeatherUiState.Error(error)
                    })
            }
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(e)
        }
    }
}

sealed interface WeatherUiState {
    data class Success(val data: CurrentWeatherApiRes) : WeatherUiState
    data class Error(val exception: Throwable) : WeatherUiState
    data object Loading : WeatherUiState
}