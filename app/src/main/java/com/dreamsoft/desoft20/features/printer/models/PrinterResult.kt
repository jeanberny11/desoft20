package com.dreamsoft.desoft20.features.printer.models

import kotlinx.serialization.Serializable


@Serializable
data class PrinterResult(
    val code:Int,
    val message:String
)
