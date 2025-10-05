package com.dreamsoft.desoft20.features.printer.models

import kotlinx.serialization.Serializable

@Serializable
data class PrintData(
    val printerName: String,
    val lines: List<PrinterLine>
)
