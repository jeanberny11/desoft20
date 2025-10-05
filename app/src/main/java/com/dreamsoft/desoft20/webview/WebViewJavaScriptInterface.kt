package com.dreamsoft.desoft20.webview

import android.webkit.JavascriptInterface
import com.dreamsoft.desoft20.features.location.models.LocationData
import com.dreamsoft.desoft20.features.printer.models.PrintData
import com.dreamsoft.desoft20.features.printer.models.PrinterResult
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

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
    val onReadBarcode: () -> Unit,
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
    fun readBarcode() {
        onReadBarcode()
    }
}