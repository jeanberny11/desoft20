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
        fun success(message: String) = ShareResult(code = 1, message = message)
        fun error(message: String) = ShareResult(code = 3, message = message)
        fun loading(message: String) = ShareResult(code = 2, message = message)



    }
}