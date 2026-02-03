package com.example.androidroadmap.domain.location.usecases

import com.example.androidroadmap.domain.location.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke() = repository.getCurrentLocation()

}