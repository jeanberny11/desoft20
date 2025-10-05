package com.dreamsoft.desoft20.features.printer.models

enum class PrinterTextAlignment(val value:Int) {
    CENTER(1),
    LEFT(0),
    RIGHT(2);

    companion object {
        fun fromValue(value: Int): PrinterTextAlignment {
            return entries.firstOrNull { it.value == value } ?: LEFT
        }
    }
}