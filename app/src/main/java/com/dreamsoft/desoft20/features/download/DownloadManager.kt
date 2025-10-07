package com.dreamsoft.desoft20.features.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dreamsoft.desoft20.features.download.models.DownloadRequest
import com.dreamsoft.desoft20.features.download.models.DownloadResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val activeDownloads = mutableMapOf<Long, DownloadRequest>()

    /**
     * Start a download and return Flow for tracking progress
     */
    fun downloadFile(request: DownloadRequest): Flow<DownloadResult> = callbackFlow {
        try {
            // Validate URL
            if (!URLUtil.isValidUrl(request.url)) {
                trySend(DownloadResult(
                    code = DownloadResult.ERROR,
                    message = "URL inválida: ${request.url}"
                ))
                close()
                return@callbackFlow
            }

            // Generate filename
            val filename = request.filename ?: generateFilename(request.url, request.mimeType)

            // Create download request
            val downloadRequest = DownloadManager.Request(request.url.toUri()).apply {
                setTitle(filename)
                setDescription("Descargando archivo...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                // Add custom headers
                request.headers.forEach { (key, value) ->
                    addRequestHeader(key, value)
                }

                // Set destination
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    filename
                )

                // Allow download over mobile network
                setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
            }

            // Enqueue download
            val downloadId = downloadManager.enqueue(downloadRequest)
            activeDownloads[downloadId] = request

            // Send initial status
            trySend(DownloadResult(
                code = DownloadResult.IN_PROGRESS,
                message = "Descarga iniciada...",
                filename = filename
            ))

            // Register broadcast receiver for completion
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1

                    if (id == downloadId) {
                        val result = queryDownloadStatus(downloadId, filename)
                        trySend(result)

                        // Cleanup
                        activeDownloads.remove(downloadId)
                        context?.unregisterReceiver(this)
                        close()
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            // Cleanup on cancellation
            awaitClose {
                activeDownloads.remove(downloadId)
            }

        } catch (e: Exception) {
            Log.e("DownloadManager", "Error starting download", e)
            trySend(DownloadResult(
                code = DownloadResult.ERROR,
                message = "Error: ${e.message}"
            ))
            close()
        }
    }

    /**
     * Query download status
     */
    private fun queryDownloadStatus(downloadId: Long, filename: String): DownloadResult {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)

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
                    DownloadResult(
                        code = DownloadResult.SUCCESS,
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
                        else -> "Error desconocido"
                    }
                    DownloadResult(
                        code = DownloadResult.ERROR,
                        message = "Descarga fallida: $errorMsg"
                    )
                }
                else -> {
                    DownloadResult(
                        code = DownloadResult.IN_PROGRESS,
                        message = "Descargando...",
                        filename = filename
                    )
                }
            }
        } else {
            cursor?.close()
            DownloadResult(
                code = DownloadResult.ERROR,
                message = "No se pudo consultar el estado de descarga"
            )
        }
    }

    /**
     * Get download progress (0-100)
     */
    fun getDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)

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
     * Cancel download
     */
    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
        activeDownloads.remove(downloadId)
    }

    /**
     * Generate filename from URL
     */
    private fun generateFilename(url: String, mimeType: String?): String {
        // Try to get filename from URL
        val urlFilename = URLUtil.guessFileName(url, null, mimeType)

        // If still generic, create timestamp-based name
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
            filename
        )
        return file.exists()
    }

    /**
     * Open downloaded file with default app
     */
    fun openDownloadedFile(context: Context, filename: String): Boolean {
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)

        while (cursor.moveToNext()) {
            val filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)

            if (cursor.getString(filenameIndex) == filename) {
                val localUri = cursor.getString(uriIndex)
                cursor.close()

                return try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(localUri.toUri(), getMimeType(filename))
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(intent)
                    true
                } catch (e: Exception) {
                    Log.e("DownloadManager", "Error opening file", e)
                    false
                }
            }
        }
        cursor.close()
        return false
    }

    private fun getMimeType(filename: String): String {
        val extension = filename.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }
}