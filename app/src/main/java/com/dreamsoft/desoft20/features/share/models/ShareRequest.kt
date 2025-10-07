package com.dreamsoft.desoft20.features.share.models

import kotlinx.serialization.Serializable

@Serializable
data class ShareRequest(
    val imageName: String,
    val message: String = "",
    val packageName: String = "com.whatsapp" // Default to WhatsApp
)

@Serializable
data class ShareResult(
    val code: Int,
    val message: String
) {
    companion object {
        const val SUCCESS = 1
        const val ERROR = 2
    }
}