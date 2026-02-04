package com.example.androidroadmap.data.location.local

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.androidroadmap.domain.repository.location.repository.LocationRepository
import com.example.androidroadmap.domain.model.weather.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getCurrentLocation(): Location {
        val androidLocation = fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .await()
            ?: fusedLocationClient.lastLocation.await()
            ?: throw IllegalStateException("Location is null. Check GPS or permissions.")

        return Location(
            id = androidLocation.time,
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude,
            name = "",
            state = null,
            country = "",
        )
    }
}
