package com.dreamsoft.desoft20.features.download

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.dreamsoft.desoft20.features.download.models.DownloadRequest
import com.dreamsoft.desoft20.features.download.models.DownloadResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val activeDownloads = mutableMapOf<Long, DownloadCallback>()
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "DownloadManager"
        private const val POLL_INTERVAL_MS = 500L
    }

    data class DownloadCallback(
        val filename: String,
        val onProgress: (DownloadResult) -> Unit,
        val onComplete: (DownloadResult) -> Unit,
        val onError: (DownloadResult) -> Unit
    )

    /**
     * Start a download with polling-based tracking
     */
    fun downloadFile(
        request: DownloadRequest,
        onProgress: (DownloadResult) -> Unit,
        onComplete: (DownloadResult) -> Unit,
        onError: (DownloadResult) -> Unit
    ): Long {
        try {
            // Validate URL
            if (!URLUtil.isValidUrl(request.url)) {
                onError(DownloadResult.error("URL inválida: ${request.url}"))
                return -1
            }

            val filename = request.filename ?: generateFilename(request.url, request.mimeType)

            Log.d(TAG, "Starting download: $filename from ${request.url}")

            // Create download request
            val downloadRequest = DownloadManager.Request(request.url.toUri()).apply {
                setTitle(filename)
                setDescription("Descargando archivo...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                // Add custom headers
                request.headers.forEach { (key, value) ->
                    addRequestHeader(key, value)
                }

                // Save to Downloads/Desoftinf folder
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "Desoftinf/$filename"
                )

                // Allow all network types
                setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
            }

            // Enqueue download
            val downloadId = downloadManager.enqueue(downloadRequest)

            // Store callback
            activeDownloads[downloadId] = DownloadCallback(
                filename = filename,
                onProgress = onProgress,
                onComplete = onComplete,
                onError = onError
            )

            Log.d(TAG, "Download enqueued - ID: $downloadId")

            // Send initial progress
            onProgress(DownloadResult.inProgress(
                message = "Descarga iniciada...",
                filename = filename
            ))

            // Start polling
            startPolling(downloadId)

            return downloadId

        } catch (e: Exception) {
            Log.e(TAG, "Error starting download", e)
            onError(DownloadResult.error("Error: ${e.message}"))
            return -1
        }
    }

    /**
     * Poll download status every 500ms
     */
    private fun startPolling(downloadId: Long) {
        val pollRunnable = object : Runnable {
            override fun run() {
                val callback = activeDownloads[downloadId]

                // If callback removed, stop polling
                if (callback == null) {
                    Log.d(TAG, "Polling stopped - download $downloadId no longer active")
                    return
                }

                // Query download status
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(statusIndex)
                    cursor.close()

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.d(TAG, "Download $downloadId completed successfully")
                            val result = queryDownloadStatus(downloadId, callback.filename)
                            callback.onComplete(result)
                            activeDownloads.remove(downloadId)
                        }

                        DownloadManager.STATUS_FAILED -> {
                            Log.d(TAG, "Download $downloadId failed")
                            val result = queryDownloadStatus(downloadId, callback.filename)
                            callback.onError(result)
                            activeDownloads.remove(downloadId)
                        }

                        DownloadManager.STATUS_RUNNING -> {
                            // Get progress percentage
                            val progress = getDownloadProgress(downloadId)
                            callback.onProgress(DownloadResult.inProgress(
                                message = "Descargando... $progress%",
                                filename = callback.filename
                            ))
                            // Continue polling
                            handler.postDelayed(this, POLL_INTERVAL_MS)
                        }

                        else -> {
                            // PENDING or PAUSED - continue polling
                            handler.postDelayed(this, POLL_INTERVAL_MS)
                        }
                    }
                } else {
                    // Query failed
                    cursor?.close()
                    Log.e(TAG, "Failed to query download $downloadId")
                    callback.onError(DownloadResult.error("No se pudo consultar el estado"))
                    activeDownloads.remove(downloadId)
                }
            }
        }

        // Start polling after initial delay
        handler.postDelayed(pollRunnable, POLL_INTERVAL_MS)
        Log.d(TAG, "Polling started for download $downloadId")
    }

    /**
     * Get download progress percentage (0-100)
     */
    private fun getDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        return if (cursor != null && cursor.moveToFirst()) {
            val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

            val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
            val bytesTotal = cursor.getLong(bytesTotalIndex)
            cursor.close()

            if (bytesTotal > 0) {
                ((bytesDownloaded * 100) / bytesTotal).toInt()
            } else {
                0
            }
        } else {
            cursor?.close()
            0
        }
    }

    /**
     * Query detailed download status
     */
    private fun queryDownloadStatus(downloadId: Long, filename: String): DownloadResult {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        return if (cursor != null && cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

            val status = cursor.getInt(statusIndex)
            val reason = cursor.getInt(reasonIndex)
            val localUri = cursor.getString(uriIndex)
            val totalBytes = cursor.getLong(bytesIndex)
            cursor.close()

            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val filepath = localUri?.toUri()?.path ?: ""
                    DownloadResult.success(
                        message = "Descarga completada",
                        filename = filename,
                        filepath = filepath,
                        filesize = totalBytes
                    )
                }

                DownloadManager.STATUS_FAILED -> {
                    val errorMsg = when (reason) {
                        DownloadManager.ERROR_CANNOT_RESUME -> "No se puede reanudar"
                        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Almacenamiento no encontrado"
                        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Archivo ya existe"
                        DownloadManager.ERROR_FILE_ERROR -> "Error de archivo"
                        DownloadManager.ERROR_HTTP_DATA_ERROR -> "Error de datos HTTP"
                        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Espacio insuficiente"
                        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Demasiadas redirecciones"
                        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Código HTTP no manejado"
                        else -> "Error desconocido (code: $reason)"
                    }
                    DownloadResult.error("Descarga fallida: $errorMsg")
                }

                else -> {
                    DownloadResult.inProgress(
                        message = "Descargando...",
                        filename = filename
                    )
                }
            }
        } else {
            cursor?.close()
            DownloadResult.error("No se pudo consultar el estado de descarga")
        }
    }

    /**
     * Cancel a download
     */
    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
        activeDownloads.remove(downloadId)
        Log.d(TAG, "Download $downloadId cancelled")
    }

    /**
     * Generate filename from URL
     */
    private fun generateFilename(url: String, mimeType: String?): String {
        val urlFilename = URLUtil.guessFileName(url, null, mimeType)
        return if (urlFilename == "downloadfile.bin") {
            val timestamp = System.currentTimeMillis()
            val extension = mimeType?.let {
                MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
            } ?: "bin"
            "download_$timestamp.$extension"
        } else {
            urlFilename
        }
    }

    /**
     * Check if file exists in Downloads
     */
    fun isFileDownloaded(filename: String): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Desoftinf/$filename"
        )
        return file.exists()
    }

    /**
     * Open downloaded file with default app
     */
    fun openDownloadedFile(context: Context, filename: String): Boolean {
        try {
            // First, try to find the file in the Desoftinf folder
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Desoftinf/$filename"
            )

            if (!file.exists()) {
                Log.e(TAG, "File not found: ${file.absolutePath}")
                return false
            }

            // Use FileProvider to get a content URI (required for Android 7+)
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mimeType = getMimeType(filename)
            Log.d(TAG, "Opening file: $filename with MIME type: $mimeType")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(intent)
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error opening file: $filename", e)
            return false
        }
    }

    /**
     * Get MIME type from filename
     */
    private fun getMimeType(filename: String): String {
        val extension = filename.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }

    /**
     * Cleanup - cancel all active downloads
     */
    fun cleanup() {
        activeDownloads.keys.forEach { downloadId ->
            handler.removeCallbacksAndMessages(downloadId)
        }
        activeDownloads.clear()
        Log.d(TAG, "All downloads cleaned up")
    }
}