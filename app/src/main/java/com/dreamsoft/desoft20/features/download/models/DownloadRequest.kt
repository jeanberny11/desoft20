package com.dreamsoft.desoft20.features.download.models

import kotlinx.serialization.Serializable

@Serializable
data class DownloadRequest(
    val url: String,
    val filename: String? = null,  // Optional custom filename
    val mimeType: String? = null,   // Optional MIME type
    val headers: Map<String, String> = emptyMap()  // Optional headers
)

@Serializable
data class DownloadResult(
    val code: Int,
    val message: String,
    val filename: String = "",
    val filepath: String = "",
    val filesize: Long = 0
) {
    companion object {
        const val SUCCESS = 1
        const val IN_PROGRESS = 2
        const val ERROR = 3
    }
}