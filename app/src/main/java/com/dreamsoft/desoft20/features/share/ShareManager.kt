package com.dreamsoft.desoft20.features.share

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.dreamsoft.desoft20.features.share.models.ShareRequest
import com.dreamsoft.desoft20.features.share.models.ShareResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ShareManager"
        private const val DESOFTINF_FOLDER = "Desoftinf"
    }

    /**
     * Share image to WhatsApp (or other apps)
     */
    fun shareImage(request: ShareRequest): ShareResult {
        return try {
            // Get the image URI based on Android version
            val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getImageUriFromMediaStore(request.imageName)
            } else {
                getImageUriFromFile(request.imageName)
            }

            if (imageUri == null) {
                return ShareResult(
                    code = ShareResult.ERROR,
                    message = "Imagen '${request.imageName}' no encontrada"
                )
            }

            // Create share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // Add message if provided
                if (request.message.isNotEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, request.message)
                }

                putExtra(Intent.EXTRA_STREAM, imageUri)

                // Set specific package if provided
                if (request.packageName.isNotEmpty()) {
                    setPackage(request.packageName)
                }
            }

            // Check if app is installed
            if (request.packageName.isNotEmpty() && !isAppInstalled(request.packageName)) {
                return ShareResult(
                    code = ShareResult.ERROR,
                    message = "WhatsApp no está instalado"
                )
            }

            // Start share activity
            val chooserIntent = Intent.createChooser(shareIntent, "Compartir imagen")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            ShareResult(
                code = ShareResult.SUCCESS,
                message = "Compartiendo imagen..."
            )

        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Activity not found", e)
            ShareResult(
                code = ShareResult.ERROR,
                message = "No se pudo abrir la aplicación"
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception", e)
            ShareResult(
                code = ShareResult.ERROR,
                message = "Permisos insuficientes"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing image", e)
            ShareResult(
                code = ShareResult.ERROR,
                message = e.message ?: "Error desconocido"
            )
        }
    }

    /**
     * Get image URI from MediaStore (Android 10+)
     */
    private fun getImageUriFromMediaStore(imageName: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null

        val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH
        )

        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/$DESOFTINF_FOLDER%")

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->

            if (cursor.count == 0) {
                Log.w(TAG, "No files found in Downloads/$DESOFTINF_FOLDER/")
                return null
            }

            while (cursor.moveToNext()) {
                val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)

                val fileName = cursor.getString(displayNameIndex)

                if (fileName == imageName) {
                    val id = cursor.getLong(idIndex)
                    return ContentUris.withAppendedId(contentUri, id)
                }
            }

            Log.w(TAG, "Image '$imageName' not found in MediaStore")
            return null
        }

        return null
    }

    /**
     * Get image URI from file (Android 9 and below)
     */
    private fun getImageUriFromFile(imageName: String): Uri? {
        // Try Downloads/Desoftinf first
        var file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$DESOFTINF_FOLDER/$imageName"
        )

        if (!file.exists()) {
            // Try root Desoftinf folder as fallback
            file = File(
                Environment.getExternalStorageDirectory(),
                "$DESOFTINF_FOLDER/$imageName"
            )
        }

        if (!file.exists()) {
            Log.w(TAG, "File not found: ${file.absolutePath}")
            return null
        }

        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file URI", e)
            null
        }
    }

    /**
     * Check if an app is installed
     */
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Share multiple images
     */
    fun shareMultipleImages(imageNames: List<String>, message: String = "", packageName: String = ""): ShareResult {
        return try {
            val uris = ArrayList<Uri>()

            imageNames.forEach { imageName ->
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getImageUriFromMediaStore(imageName)
                } else {
                    getImageUriFromFile(imageName)
                }

                if (uri != null) {
                    uris.add(uri)
                }
            }

            if (uris.isEmpty()) {
                return ShareResult(
                    code = ShareResult.ERROR,
                    message = "No se encontraron imágenes"
                )
            }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                if (message.isNotEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, message)
                }

                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)

                if (packageName.isNotEmpty()) {
                    setPackage(packageName)
                }
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Compartir imágenes")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            ShareResult(
                code = ShareResult.SUCCESS,
                message = "Compartiendo ${uris.size} imágenes..."
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error sharing multiple images", e)
            ShareResult(
                code = ShareResult.ERROR,
                message = e.message ?: "Error desconocido"
            )
        }
    }

    /**
     * Get list of images in Desoftinf folder
     */
    fun getDesoftinfImages(): List<String> {
        val imageList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
            val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/$DESOFTINF_FOLDER%")

            context.contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    imageList.add(cursor.getString(nameIndex))
                }
            }
        } else {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DESOFTINF_FOLDER
            )

            if (folder.exists() && folder.isDirectory) {
                folder.listFiles()?.forEach { file ->
                    if (file.isFile && file.extension in listOf("jpg", "jpeg", "png", "gif")) {
                        imageList.add(file.name)
                    }
                }
            }
        }

        return imageList
    }

    fun saveImageToDesoftinf(bitmap: Bitmap, filename: String): ShareResult {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageToMediaStore(bitmap, filename)
            } else {
                saveImageToFile(bitmap, filename)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            ShareResult(
                code = ShareResult.ERROR,
                message = e.message ?: "Error guardando imagen"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToMediaStore(bitmap: Bitmap, filename: String): ShareResult {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$DESOFTINF_FOLDER")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }
            return ShareResult(
                code = ShareResult.SUCCESS,
                message = "Imagen guardada: $filename"
            )
        }

        return ShareResult(
            code = ShareResult.ERROR,
            message = "No se pudo crear el archivo"
        )
    }

    private fun saveImageToFile(bitmap: Bitmap, filename: String): ShareResult {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DESOFTINF_FOLDER
        )

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val file = File(folder, filename)
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        return ShareResult(
            code = ShareResult.SUCCESS,
            message = "Imagen guardada: ${file.absolutePath}"
        )
    }
}