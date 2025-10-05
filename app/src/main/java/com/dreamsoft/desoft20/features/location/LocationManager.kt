package com.dreamsoft.desoft20.features.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.dreamsoft.desoft20.features.location.models.LocationResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
) {

    private fun getFusedLocationClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(allOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"])
    suspend fun getLastLocation(context: Context): LocationResult {
        return try {
            if (!hasLocationPermission(context)) {
                return LocationResult.PermissionDenied
            }
            val fusedLocationClient = getFusedLocationClient(context)
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                LocationResult.Success(location)
            } else {
                LocationResult.LocationNotAvailable
            }
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown location error")
        }
    }

    @RequiresPermission(allOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"])
    suspend fun getCurrentLocation(context: Context): LocationResult {
        return try {
            if (!hasLocationPermission(context)) {
                return LocationResult.PermissionDenied
            }

            val cancellationTokenSource = CancellationTokenSource()
            val fusedLocationClient = getFusedLocationClient(context)
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                LocationResult.Success(location)
            } else {
                LocationResult.LocationNotAvailable
            }
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown location error")
        }
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}