package com.dreamsoft.desoft20.features.bluetooth.models

import kotlinx.serialization.Serializable

@Serializable
data class BluetoothDeviceInfo(
    val name: String,
    val address: String
)
