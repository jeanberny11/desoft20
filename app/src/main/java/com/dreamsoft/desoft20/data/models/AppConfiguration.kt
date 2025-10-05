package com.dreamsoft.desoft20.data.models

data class AppConfiguration(
    val localUrl: String = "",
    val remoteUrl: String = "",
    val printerName: String = "",
    val useLocalUrl: Boolean = false,
    val enableCache: Boolean = false,
    val enableZoom: Boolean = false,
    val enableWebNavigation: Boolean = false
)
