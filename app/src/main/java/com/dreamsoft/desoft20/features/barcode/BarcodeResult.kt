package com.dreamsoft.desoft20.features.barcode

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeResult(
    val resultCode: Int,
    val message: String,
    val barcode: String ="",
)