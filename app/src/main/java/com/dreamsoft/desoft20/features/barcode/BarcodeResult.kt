package com.dreamsoft.desoft20.features.barcode

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeResult(
    val resultCode: Int,
    val message: String,
    val barcode: String ="",
    val fieldName: String=""
){
    companion object{
        fun success(barcode: String,fieldName: String) = BarcodeResult(1,"Exito",barcode,fieldName)
        fun error(message: String) = BarcodeResult(3,message)
        fun loading(message: String) = BarcodeResult(2,message)
    }
}