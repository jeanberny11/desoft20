package com.dreamsoft.desoft20.features.location.models

import android.location.Location

sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationNotAvailable : LocationResult()
    data class Error(val message: String) : LocationResult()
}

