package com.dreamsoft.desoft20.webview

import android.R
import android.webkit.JavascriptInterface
import com.dreamsoft.desoft20.features.download.models.DownloadRequest
import com.dreamsoft.desoft20.features.download.models.DownloadResult
import com.dreamsoft.desoft20.features.location.models.LocationData
import com.dreamsoft.desoft20.features.printer.models.PrintData
import com.dreamsoft.desoft20.features.printer.models.PrinterResult
import com.dreamsoft.desoft20.features.share.models.ShareRequest
import com.dreamsoft.desoft20.features.share.models.ShareResult
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.collections.emptyList

@Suppress("unused")
class WebViewJavaScriptInterface(
    val onPrintGenericData: (PrintData) -> Unit,
    val onPrintSunmiData: (PrintData) -> Unit,
    val onClearWebViewHistory: () -> Unit,
    val onGetDeviceId: () -> String,
    val onHideKeyboard: () -> Unit,
    val onUpdateAppConfiguration: (String, String, Boolean, String, Boolean) -> Unit,
    val onOpenInBrowser: (String) -> Unit,
    val onGetLastLocation: () -> Unit,
    val onReadBarcode: (String) -> String,
    val onDownloadFile: (DownloadRequest) -> Unit,  // ← Add this
    val onOpenFile: (String) -> Unit,  // ← Add this
    val onShareImage: (ShareRequest) -> Unit,  // ← Add this
    val onShareMultipleImages: (List<String>, String, String) -> Unit,
    val onGetDesoftinfImages:()-> List<String>,
) {

    @JavascriptInterface
    fun printGenericData(data: String): String {
        try {
            val printData = Json.decodeFromString<PrintData>(data)
            onPrintGenericData(printData)
            val result = PrinterResult(code = 2, message = "Imprimiendo...")
            return Json.encodeToString(result)
        } catch (e: SerializationException) {
            val result = PrinterResult(
                code = 3,
                message = if (e.message == null) "Ha ocurrido un error decodificando el json" else e.message!!
            )
            return Json.encodeToString(result)
        } catch (e: Exception) {
            val result = PrinterResult(
                code = 3,
                message = if (e.message == null) "Ha ocurrido un error!!" else e.message!!
            )
            return Json.encodeToString(result)
        }
    }

    @JavascriptInterface
    fun printSunmiData(data: String): String {
        try {
            val printData = Json.decodeFromString<PrintData>(data)
            onPrintSunmiData(printData)
            val result = PrinterResult(code = 2, message = "Imprimiendo...")
            return Json.encodeToString(result)
        } catch (e: SerializationException) {
            val result = PrinterResult(
                code = 3,
                message = if (e.message == null) "Ha ocurrido un error decodificando el json" else e.message!!
            )
            return Json.encodeToString(result)
        } catch (e: Exception) {
            val result = PrinterResult(
                code = 3,
                message = if (e.message == null) "Ha ocurrido un error!!" else e.message!!
            )
            return Json.encodeToString(result)
        }
    }

    @JavascriptInterface
    fun clearWebViewHistory() {
        onClearWebViewHistory()
    }

    @JavascriptInterface
    fun getDeviceId(): String {
        return onGetDeviceId()
    }

    @JavascriptInterface
    fun hideKeyboard() {
        onHideKeyboard()
    }

    @JavascriptInterface
    fun updateAppConfiguration(
        remoteurl: String,
        localurl: String,
        uselocalurl: Boolean,
        printername: String,
        enablewebnavigation: Boolean
    ) {
        onUpdateAppConfiguration(remoteurl, localurl, uselocalurl, printername, enablewebnavigation)
    }

    @JavascriptInterface
    fun openInBrowser(url: String) {
        onOpenInBrowser(url)
    }

    @JavascriptInterface
    fun getLastLocation(): String {
        try {
            onGetLastLocation()
            val locationData = LocationData(resultCode = 2, message = "Obteniendo ubicación...")
            return Json.encodeToString(locationData)
        } catch (e: SerializationException) {
            val result = LocationData(
                resultCode = 3,
                message = if (e.message == null) "Ha ocurrido un error decodificando el json" else e.message!!
            )
            return Json.encodeToString(result)
        } catch (e: Exception) {
            val result = LocationData(
                resultCode = 3,
                message = if (e.message == null) "Ha ocurrido un error!!" else e.message!!
            )
            return Json.encodeToString(result)
        }
    }

    @JavascriptInterface
    fun readBarcode(fieldname: String): String {
        return onReadBarcode(fieldname)
    }

    @JavascriptInterface
    fun downloadFile(data: String): String {
        return try {
            val downloadRequest = Json.decodeFromString<DownloadRequest>(data)
            onDownloadFile(downloadRequest)

            val result = DownloadResult(
                code = DownloadResult.IN_PROGRESS,
                message = "Iniciando descarga..."
            )
            Json.encodeToString(result)
        } catch (e: SerializationException) {
            val result = DownloadResult(
                code = DownloadResult.ERROR,
                message = e.message ?: "Error decodificando JSON"
            )
            Json.encodeToString(result)
        } catch (e: Exception) {
            val result = DownloadResult(
                code = DownloadResult.ERROR,
                message = e.message ?: "Error desconocido"
            )
            Json.encodeToString(result)
        }
    }

    @JavascriptInterface
    fun openDownloadedFile(filename: String): String {
        return try {
            onOpenFile(filename)
            Json.encodeToString(DownloadResult(
                code = DownloadResult.SUCCESS,
                message = "Abriendo archivo..."
            ))
        } catch (e: Exception) {
            Json.encodeToString(DownloadResult(
                code = DownloadResult.ERROR,
                message = e.message ?: "Error abriendo archivo"
            ))
        }
    }

    @JavascriptInterface
    fun sendWhatsAppImage(imagename: String, message: String): String {
        return try {
            val shareRequest = ShareRequest(
                imageName = imagename,
                message = message,
                packageName = "com.whatsapp"
            )
            onShareImage(shareRequest)

            Json.encodeToString(ShareResult.loading("Compartiendo imagen..."))
        } catch (e: Exception) {
            Json.encodeToString(ShareResult.error(e.message ?: "Error desconocido"))
        }
    }

    @JavascriptInterface
    fun shareImage(data: String): String {
        return try {
            val shareRequest = Json.decodeFromString<ShareRequest>(data)
            onShareImage(shareRequest)

            Json.encodeToString(ShareResult.loading("Compartiendo imagen..."))
        } catch (e: Exception) {
            Json.encodeToString(ShareResult.error(e.message ?: "Error al compartir"))
        }
    }

    @JavascriptInterface
    fun shareMultipleImages(imageNames: String, message: String, packageName: String): String {
        return try {
            val images = Json.decodeFromString<List<String>>(imageNames)
            onShareMultipleImages(images, message, packageName)

            Json.encodeToString(ShareResult.loading("Compartiendo ${images.size} imágenes..."))
        } catch (e: Exception) {
            Json.encodeToString(ShareResult.error( e.message ?: "Error al compartir"))
        }
    }

    @JavascriptInterface
    fun getDesoftinfImages(): String {
        return try {
            val images = onGetDesoftinfImages.invoke()
            Json.encodeToString(images)
        } catch (e: Exception) {
            Json.encodeToString(emptyList<String>())
        }
    }
}