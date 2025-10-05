package com.dreamsoft.desoft20.features.printer.models

import kotlinx.serialization.Serializable

@Serializable
data class PrinterLine(
    val type: PrintType = PrintType.TEXT,              // default type
    val text: String = "",                             // empty string by default
    val alignment: PrinterTextAlignment = PrinterTextAlignment.LEFT,
    val size: PrinterTextSize = PrinterTextSize.NORMALTEXT,
    val bold: Boolean = false,
    val underline: Boolean = false,
    val columnText: List<String> = emptyList(),
    val columnAlign: List<Int> = emptyList()
)