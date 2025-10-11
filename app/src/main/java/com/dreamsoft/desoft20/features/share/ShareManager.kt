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
        const val DESOFTINF_FOLDER = "Desoftinf"
    }

    /**
     * Ensure the Desoftinf folder exists
     * Returns true if folder exists or was created successfully
     */
    private fun ensureDesoftinfFolderExists(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ensureDesoftinfFolderExistsMediaStore()
            } else {
                ensureDesoftinfFolderExistsLegacy()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ensuring Desoftinf folder exists", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun ensureDesoftinfFolderExistsMediaStore(): Boolean {
        val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/$DESOFTINF_FOLDER%")

        // Check if folder already has files
        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.count > 0) {
                Log.d(TAG, "Desoftinf folder already exists with files")
                return true
            }
        }

        // Folder doesn't exist, create it by saving a placeholder file
        Log.d(TAG, "Creating Desoftinf folder by adding placeholder file")
        return try {
            val placeholderName = ".desoftinf_folder"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, placeholderName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$DESOFTINF_FOLDER")
            }

            val uri = context.contentResolver.insert(contentUri, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write("Desoftinf folder placeholder".toByteArray())
                }
                Log.d(TAG, "Desoftinf folder created successfully via MediaStore")
                true
            } else {
                Log.e(TAG, "Failed to create Desoftinf folder - URI is null")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating Desoftinf folder via MediaStore", e)
            false
        }
    }

    private fun ensureDesoftinfFolderExistsLegacy(): Boolean {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DESOFTINF_FOLDER
        )

        if (folder.exists()) {
            Log.d(TAG, "Desoftinf folder already exists: ${folder.absolutePath}")
            return true
        }

        val created = folder.mkdirs()
        if (created) {
            Log.d(TAG, "Desoftinf folder created: ${folder.absolutePath}")
        } else {
            Log.e(TAG, "Failed to create Desoftinf folder: ${folder.absolutePath}")
        }

        return created || folder.exists()
    }

    fun shareImage(request: ShareRequest): ShareResult {
        return try {
            // Ensure folder exists before looking for images
            ensureDesoftinfFolderExists()

            val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getImageUriFromMediaStore(request.imageName)
            } else {
                getImageUriFromFile(request.imageName)
            }

            if (imageUri == null) {
                return ShareResult.error("Imagen ${request.imageName} no encontrada")
            }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                if (request.message.isNotEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, request.message)
                }

                putExtra(Intent.EXTRA_STREAM, imageUri)

                if (request.packageName.isNotEmpty()) {
                    setPackage(request.packageName)
                }
            }

            if (request.packageName.isNotEmpty() && !isAppInstalled(request.packageName)) {
                return ShareResult.error("La app solicitada no está instalado")
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Compartir imagen")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            ShareResult.success("Imagen compartida...")

        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Activity not found", e)
            ShareResult.error("No se encontró una actividad para compartir la imagen")
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception", e)
            ShareResult.error("Permisos insuficientes para compartir la imagen")
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing image", e)
            ShareResult.error(e.message ?: "Error desconocido")
        }
    }

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

            Log.w(TAG, "Image $imageName not found in MediaStore")
            return null
        }

        return null
    }

    private fun getImageUriFromFile(imageName: String): Uri? {
        var file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$DESOFTINF_FOLDER/$imageName"
        )

        if (!file.exists()) {
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

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareMultipleImages(imageNames: List<String>, message: String = "", packageName: String = ""): ShareResult {
        return try {
            // Ensure folder exists before looking for images
            ensureDesoftinfFolderExists()

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
                return ShareResult.error("No se encontraron imágenes para compartir")
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

            ShareResult.success("${uris.size} imágenes compartidas...")

        } catch (e: Exception) {
            Log.e(TAG, "Error sharing multiple images", e)
            ShareResult.error(e.message ?: "Error desconocido")
        }
    }

    fun getDesoftinfImages(): List<String> {
        // Ensure folder exists first
        ensureDesoftinfFolderExists()

        val imageList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
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
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)

                while (cursor.moveToNext()) {
                    val fileName = cursor.getString(nameIndex)
                    val mimeType = cursor.getString(mimeIndex)
                    val path = cursor.getString(pathIndex)

                    Log.d(TAG, "Found file: $fileName, mime: $mimeType, path: $path")

                    // Only include image files, exclude hidden files
                    if (!fileName.startsWith(".") && mimeType?.startsWith("image/") == true) {
                        imageList.add(fileName)
                    }
                }
            }
        } else {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DESOFTINF_FOLDER
            )

            if (folder.exists() && folder.isDirectory) {
                folder.listFiles()?.forEach { file ->
                    Log.d(TAG, "Found file: ${file.name}, extension: ${file.extension}")
                    if (file.isFile && file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif", "webp")) {
                        imageList.add(file.name)
                    }
                }
            }
        }

        Log.d(TAG, "Total images found: ${imageList.size}")
        return imageList
    }

    fun saveImageToDesoftinf(bitmap: Bitmap, filename: String): ShareResult {
        return try {
            // Ensure folder exists before saving
            if (!ensureDesoftinfFolderExists()) {
                return ShareResult.error("No se pudo crear la carpeta Desoftinf")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageToMediaStore(bitmap, filename)
            } else {
                saveImageToFile(bitmap, filename)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            ShareResult.error(e.message ?: "Error guardando imagen")
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
            Log.d(TAG, "Image saved to MediaStore: $filename")
            return ShareResult.success("Imagen guardada: $filename")
        }

        return ShareResult.error("No se pudo crear el archivo")
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

        Log.d(TAG, "Image saved to file: ${file.absolutePath}")
        return ShareResult.success("Imagen guardada: ${file.absolutePath}")
    }
}