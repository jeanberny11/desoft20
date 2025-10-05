package com.dreamsoft.desoft20.features.printer.models

enum class PrinterTextSize(val width: Int,val height: Int) {
    SMALLTEXT(-1,-1),
    NORMALTEXT(1,1),
    BIGTEXT(2,2),
    EXTRABIGTEXT(3,3);

    companion object {
        fun fromCode(code: Int): PrinterTextSize {
            return when (code) {
                0 -> SMALLTEXT
                1 -> NORMALTEXT
                2 -> BIGTEXT
                3 -> EXTRABIGTEXT
                else -> NORMALTEXT
            }
        }
    }
}