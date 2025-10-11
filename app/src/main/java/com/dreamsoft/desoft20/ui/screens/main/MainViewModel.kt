package com.dreamsoft.desoft20.ui.screens.main

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamsoft.desoft20.data.models.AppConfiguration
import com.dreamsoft.desoft20.data.repositories.ConfigurationRepository
import com.dreamsoft.desoft20.features.barcode.BarcodeResult
import com.dreamsoft.desoft20.features.download.DownloadManager
import com.dreamsoft.desoft20.features.download.models.DownloadRequest
import com.dreamsoft.desoft20.features.download.models.DownloadResult
import com.dreamsoft.desoft20.features.location.LocationManager
import com.dreamsoft.desoft20.features.location.models.LocationData
import com.dreamsoft.desoft20.features.location.models.LocationResult
import com.dreamsoft.desoft20.features.printer.GenericPrinterManager
import com.dreamsoft.desoft20.features.printer.SunmiPrinterManager
import com.dreamsoft.desoft20.features.printer.models.PrintData
import com.dreamsoft.desoft20.features.printer.models.PrinterResult
import com.dreamsoft.desoft20.features.share.ShareManager
import com.dreamsoft.desoft20.features.share.models.ShareRequest
import com.dreamsoft.desoft20.features.share.models.ShareResult
import com.dreamsoft.desoft20.utils.extentions.UtilsHelper
import com.dreamsoft.desoft20.utils.network.NetworkUtils
import com.dreamsoft.desoft20.webview.WebViewCallbacks
import com.dreamsoft.desoft20.webview.WebViewJavaScriptInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.system.exitProcess

sealed class MainUiState {
    data class Initial(
        val currentUrl: String,
        val configuration: AppConfiguration,
        val canGoBack: Boolean,
        val javaScriptInterface: WebViewJavaScriptInterface
    ) : MainUiState()

    data class Loading(val message: String) : MainUiState()
    data class Error(val message: String, val onRetry: () -> Unit = {}) : MainUiState()
    data class Dialog(val onDismiss: () -> Unit = {}) : MainUiState()
}

sealed class WebViewEvent {
    object GoBack : WebViewEvent()
    data class ExecuteJavaScript(val script: String) : WebViewEvent()
    object ClearWebViewHistory : WebViewEvent()
}

// Event for print requests that might require permissions
sealed class PrintRequestEvent {
    data class RequestPermissionsAndPrint(val data: PrintData, val restoreState: MainUiState) :
        PrintRequestEvent()
}

sealed class LocationRequestEvent {
    data class RequestLocation(val restoreState: MainUiState) : LocationRequestEvent()
}

sealed class ScanRequestEvent {
    data class RequestScan(val restoreState: MainUiState,val fieldName: String) : ScanRequestEvent()
}

@Suppress("SpellCheckingInspection")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val configurationRepository: ConfigurationRepository,
    private val networkUtils: NetworkUtils,
    private val printerManager: GenericPrinterManager,
    private val locationManager: LocationManager,
    private val downloadManager: DownloadManager,
    private val shareManager: ShareManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading("Inicializando..."))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _webViewEvent = MutableSharedFlow<WebViewEvent>()
    val webViewEvent: SharedFlow<WebViewEvent> = _webViewEvent.asSharedFlow()

    // SharedFlow to signal UI about print requests needing permission checks
    private val _printRequestSharedEvent = MutableSharedFlow<PrintRequestEvent>()

    private val _locationRequestSharedEvent = MutableSharedFlow<LocationRequestEvent>()

    val printRequestSharedEvent: SharedFlow<PrintRequestEvent> =
        _printRequestSharedEvent.asSharedFlow()
    val locationRequestSharedEvent: SharedFlow<LocationRequestEvent> =
        _locationRequestSharedEvent.asSharedFlow()
    private val _scanRequestSharedEvent = MutableSharedFlow<ScanRequestEvent>()
    val scanRequestSharedEvent: SharedFlow<ScanRequestEvent> =
        _scanRequestSharedEvent.asSharedFlow()


    init {
        viewModelScope.launch {
            loadInitialConfiguration()
        }
    }

    private suspend fun loadInitialConfiguration() {
        _uiState.value = MainUiState.Loading("Cargando configuración...")
        try {
            var config = configurationRepository.getConfiguration()
            if (config.remoteUrl.isEmpty()) {
                config = config.copy(remoteUrl = "http://desoftinf.com/printersdk.php")
                if (networkUtils.hasNetworkConnection()) {
                    config = config.copy(useLocalUrl = false)
                }
                configurationRepository.saveConfiguration(config)
            }
            if (config.localUrl.isEmpty()) {
                config = config.copy(localUrl = "file:///android_asset/test/index.html")
                configurationRepository.saveConfiguration(config)
            }
            val url: String = if (config.useLocalUrl) {
                config.localUrl
            } else {
                config.remoteUrl
            }
            Log.d("WebViewLoad", "Attempting to load URL: $url")
            _uiState.value = MainUiState.Loading("Cargando URL: $url")
            val javaScriptInterface = WebViewJavaScriptInterface(
                onPrintGenericData = {
                    onPrintGenericDataTrigger(it)
                },
                onPrintSunmiData = {
                    onPrintSunmiDataTrigger(it)
                },
                onClearWebViewHistory = {
                    viewModelScope.launch {
                        _webViewEvent.emit(WebViewEvent.ClearWebViewHistory)
                    }
                },
                onGetDeviceId = { getDeviceId() },
                onHideKeyboard = {
                    //UtilsHelper.hideKeyboard(application)
                },
                onUpdateAppConfiguration = { remoteUrl, localUrl, useLocalUrl, printerName, enableWebNavigation ->
                    viewModelScope.launch {
                        config = configurationRepository.getConfiguration()
                        config = config.copy(
                            remoteUrl = remoteUrl,
                            localUrl = localUrl,
                            useLocalUrl = useLocalUrl,
                            printerName = printerName,
                            enableWebNavigation = enableWebNavigation
                        )
                        configurationRepository.saveConfiguration(config)
                    }
                },
                onOpenInBrowser = { url ->
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        url.toUri()
                    )
                    browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    application.startActivity(browserIntent)
                },
                onGetLastLocation = {
                    onGetLastLocationTrigger()
                },
                onReadBarcode = {
                    onRequestScan(it)
                },
                onDownloadFile = { onDownloadFileTrigger(it) },
                onOpenFile = { onOpenFileTrigger(it) },
                onShareImage = { onShareImageTrigger(it) },
                onShareMultipleImages = { images, message, pkg ->
                    onShareMultipleImagesTrigger(images, message, pkg)
                },
                onGetDesoftinfImages = { shareManager.getDesoftinfImages() }
            )
            _uiState.value = MainUiState.Initial(
                currentUrl = url,
                configuration = config,
                canGoBack = false,
                javaScriptInterface = javaScriptInterface
            )
        } catch (e: Exception) {
            _uiState.value =
                MainUiState.Error("Error al cargar configuración: ${e.message}", onRetry = {
                    exitApp()
                })
        }
    }

    fun refreshDataAndWebView() {
        viewModelScope.launch {
            loadInitialConfiguration()
        }
    }

    private fun onPrintGenericDataTrigger(data: PrintData) {
        viewModelScope.launch {
            val previousState = _uiState.value // Save previous state to potentially restore
            _uiState.value = MainUiState.Loading("Verificando permisos")
            if (printerManager.haveBluetoothPermission(application)) {
                proceedWithPrintingAfterPermission(data, previousState)
            } else {
                _uiState.value = MainUiState.Loading("Requiriendo permisos")
                _printRequestSharedEvent.emit(
                    PrintRequestEvent.RequestPermissionsAndPrint(
                        data,
                        previousState
                    )
                )
            }
        }
    }

    private fun onPrintSunmiDataTrigger(data: PrintData) {
        viewModelScope.launch {
            val previousState = _uiState.value // Save previous state to potentially restore
            _uiState.value = MainUiState.Loading("Verificando permisos")
            if (printerManager.haveBluetoothPermission(application)) {
                proceedWithPrintingSunmi(data, previousState)
            } else {
                _uiState.value = MainUiState.Loading("Requiriendo permisos")
                _printRequestSharedEvent.emit(
                    PrintRequestEvent.RequestPermissionsAndPrint(
                        data,
                        previousState
                    )
                )
            }
        }
    }

    // This function will be called by the UI after permissions are granted
    fun proceedWithPrintingAfterPermission(data: PrintData, restoreState: MainUiState) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading("Preparando impresora...")
            try {
                val config = configurationRepository.getConfiguration()
                val printerName = data.printerName.ifEmpty { config.printerName }

                if (printerName.isEmpty()) {
                    onPrintingError("Nombre de impresora no configurado.", restoreState)
                    return@launch
                }
                _uiState.value = MainUiState.Loading("Conectando a: $printerName...")
                var connected: Boolean
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    if (ActivityCompat.checkSelfPermission(
                            application,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            application,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        onPrintingError("Permisos de Bluetooth no concedidos.", restoreState)
                        return@launch
                    }
                    connected = printerManager.connect(deviceName = printerName)
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            application,
                            Manifest.permission.BLUETOOTH
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            application,
                            Manifest.permission.BLUETOOTH_ADMIN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        onPrintingError("Permisos de Bluetooth no concedidos.", restoreState)
                        return@launch
                    }
                    connected = printerManager.connectLegacy(deviceName = printerName)
                }
                if (!connected) {
                    onPrintingError(
                        "No se pudo conectar a la impresora: $printerName. Verifique la configuración, que esté encendida y los permisos de Bluetooth.",
                        restoreState
                    )
                    return@launch
                }
                printerManager.reset()
                printerManager.resetTextStyle()
                val lines = data.lines
                lines.forEach { line ->
                    printerManager.printLine(line)
                    delay(100)
                }
                printerManager.disconnect()
                val printResult = PrinterResult(code = 1, message = "Impresion Finalizada")
                executeJavaScript(
                    "${WebViewCallbacks.ON_PRINT_RESULT}('${
                        Json.encodeToString(
                            printResult
                        )
                    }')"
                )
                _uiState.value = restoreState

            } catch (e: Exception) {
                Log.e("PrintError", "Exception during printing: ${e.message}", e)
                onPrintingError(e.message!!, restoreState)
            }
        }
    }

    fun proceedWithPrintingSunmi(data: PrintData, restoreState: MainUiState) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading("Preparando impresora...")
            try {
                configurationRepository.getConfiguration()
                val printerManager = SunmiPrinterManager.getInstance()

                if (printerManager.sunmiPrinter != SunmiPrinterManager.FOUND_SUNMI_PRINTER) {
                    onPrintingError("No se encontro la impesora sunmi.", restoreState)
                    return@launch
                }
                printerManager.initPrinter()

                val lines = data.lines
                lines.forEach { line ->
                    printerManager.printLine(line)
                    delay(100)
                }
                val printResult = PrinterResult(code = 1, message = "Impresion Finalizada")
                executeJavaScript(
                    "${WebViewCallbacks.ON_PRINT_RESULT}('${
                        Json.encodeToString(
                            printResult
                        )
                    }')"
                )
                _uiState.value = restoreState
            } catch (e: Exception) {
                Log.e("PrintError", "Exception during printing: ${e.message}", e)
                onPrintingError(e.message!!, restoreState)
            }
        }
    }

    fun onPrintingError(message: String, restoreState: MainUiState) {
        val printResult = PrinterResult(code = 3, message = message)
        executeJavaScript("${WebViewCallbacks.ON_PRINT_RESULT}('${Json.encodeToString(printResult)}')")
        _uiState.value = MainUiState.Error(message, onRetry = {
            _uiState.value = restoreState
        })
    }

    fun onLocationError(message: String, restoreState: MainUiState) {
        val locationData = Json.encodeToString(LocationData.error(message))
        executeJavaScript("${WebViewCallbacks.ON_LOCATION_RESULT}('$locationData')")
        _uiState.value = MainUiState.Error(message, onRetry = {
            _uiState.value = restoreState
        })
    }

    private fun onGetLastLocationTrigger() {
        viewModelScope.launch {
            val previousState = _uiState.value // Save previous state to potentially restore
            _uiState.value = MainUiState.Loading("Verificando permisos")
            if (locationManager.hasLocationPermission(application)) {
                _uiState.value = MainUiState.Loading("Obteniendo ubicación...")
                proceedWithLastLocationRequest(previousState)
            } else {
                _uiState.value = MainUiState.Loading("Requiriendo permisos")
                _locationRequestSharedEvent.emit(LocationRequestEvent.RequestLocation(previousState))
            }
        }
    }

    fun proceedWithLastLocationRequest(restoreState: MainUiState) {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onLocationError("Permisos de ubicación no concedidos.", restoreState)
                return@launch
            }
            val lastLocation = locationManager.getLastLocation(application)
            when (lastLocation) {
                is LocationResult.Success -> {
                    val locationData = LocationData.success(
                        altitude = lastLocation.location.altitude,
                        accuracy = lastLocation.location.accuracy,
                        latitude = lastLocation.location.latitude,
                        longitude = lastLocation.location.longitude
                    )
                    val jsonstring = Json.encodeToString(locationData)
                    Log.d("LocationData", jsonstring)
                    executeJavaScript(
                        "${WebViewCallbacks.ON_LOCATION_RESULT}('$jsonstring')"
                    )
                    _uiState.value = restoreState
                }

                is LocationResult.Error -> {
                    onLocationError(lastLocation.message, restoreState)
                }

                is LocationResult.LocationNotAvailable -> {
                    onLocationError("La ubicación no está disponible.", restoreState)
                }

                is LocationResult.PermissionDenied -> {
                    onLocationError("Permisos de ubicación no concedidos.", restoreState)
                }
            }
        }
    }

    private fun onRequestScan(fieldName: String): String {
        val result = BarcodeResult.loading("Iniciando escaneo")
        viewModelScope.launch {
            val currentState = _uiState.value
            _scanRequestSharedEvent.emit(ScanRequestEvent.RequestScan(currentState,fieldName))
        }
        return Json.encodeToString(result)
    }

    fun onStateChanged(state: MainUiState) {
        _uiState.value = state
    }

    fun onBackPressed(state: MainUiState.Initial) {
        val config = state.configuration
        val canGoBackInWebView = state.canGoBack

        if (config.enableWebNavigation && canGoBackInWebView) {
            viewModelScope.launch {
                _webViewEvent.emit(WebViewEvent.GoBack)
            }
        } else {
            // Show exit dialog
            _uiState.value = MainUiState.Dialog(onDismiss = {
                _uiState.value = state
            })
        }
    }

    private fun executeJavaScript(script: String) {
        viewModelScope.launch {
            _webViewEvent.emit(WebViewEvent.ExecuteJavaScript(script))
        }
    }

    private fun getDeviceId(): String {
        return UtilsHelper.getDeviceId(application)
    }

    fun exitApp() {
        // Clean exit
        exitProcess(0)
    }

    fun onScanResult(result: BarcodeResult, restoreState: MainUiState) {
        executeJavaScript("${WebViewCallbacks.ON_BARCODE_RESULT}('${Json.encodeToString(result)}')")
        onStateChanged(restoreState)
    }

    private fun onDownloadFileTrigger(request: DownloadRequest) {
        val previousState = _uiState.value

        // Check network
        if (!networkUtils.hasNetworkConnection()) {
            executeJavaScript(
                "${WebViewCallbacks.ON_DOWNLOAD_ERROR}('${
                    Json.encodeToString(
                        DownloadResult(
                            code = DownloadResult.ERROR,
                            message = "Sin conexión a internet"
                        )
                    )
                }')"
            )
            return
        }

        // Use callback-based approach
        downloadManager.downloadFile(
            request = request,
            onProgress = { result ->
                Log.d("MainViewModel", "Download progress: ${result.message}")
                viewModelScope.launch {
                    _uiState.value = MainUiState.Loading(result.message)
                    executeJavaScript(
                        "${WebViewCallbacks.ON_DOWNLOAD_PROGRESS}('${Json.encodeToString(result)}')"
                    )
                }
            },
            onComplete = { result ->
                Log.d("MainViewModel", "Download complete: ${result.message}")
                viewModelScope.launch {
                    executeJavaScript(
                        "${WebViewCallbacks.ON_DOWNLOAD_COMPLETE}('${Json.encodeToString(result)}')"
                    )
                    _uiState.value = previousState
                }
            },
            onError = { result ->
                Log.d("MainViewModel", "Download error: ${result.message}")
                viewModelScope.launch {
                    executeJavaScript(
                        "${WebViewCallbacks.ON_DOWNLOAD_ERROR}('${Json.encodeToString(result)}')"
                    )
                    _uiState.value = MainUiState.Error(
                        result.message,
                        onRetry = { _uiState.value = previousState }
                    )
                }
            }
        )
    }

    private fun onOpenFileTrigger(filename: String) {
        viewModelScope.launch {
            try {
                val success = downloadManager.openDownloadedFile(application, filename)
                if (!success) {
                    executeJavaScript(
                        "${WebViewCallbacks.ON_DOWNLOAD_ERROR}('${
                            Json.encodeToString(
                                DownloadResult(
                                    code = DownloadResult.ERROR,
                                    message = "No se pudo abrir el archivo"
                                )
                            )
                        }')"
                    )
                }
            } catch (e: Exception) {
                Log.e("OpenFileError", "Error opening file", e)
                executeJavaScript(
                    "${WebViewCallbacks.ON_DOWNLOAD_ERROR}('${
                        Json.encodeToString(
                            DownloadResult(
                                code = DownloadResult.ERROR,
                                message = e.message ?: "Error abriendo archivo"
                            )
                        )
                    }')"
                )
            }
        }
    }

    private fun onShareImageTrigger(request: ShareRequest) {
        viewModelScope.launch {
            try {
                val result = shareManager.shareImage(request)
                executeJavaScript(
                    "${WebViewCallbacks.ON_SHARE_RESULT}('${Json.encodeToString(result)}')"
                )
            } catch (e: Exception) {
                Log.e("ShareError", "Error sharing image", e)
                val errorResult = ShareResult.error(e.message ?: "Error desconocido")
                executeJavaScript(
                    "${WebViewCallbacks.ON_SHARE_RESULT}('${Json.encodeToString(errorResult)}')"
                )
            }
        }
    }

    private fun onShareMultipleImagesTrigger(
        imageNames: List<String>,
        message: String,
        packageName: String
    ) {
        viewModelScope.launch {
            try {
                val result = shareManager.shareMultipleImages(imageNames, message, packageName)
                executeJavaScript(
                    "${WebViewCallbacks.ON_SHARE_RESULT}('${Json.encodeToString(result)}')"
                )
            } catch (e: Exception) {
                Log.e("ShareError", "Error sharing images", e)
                val errorResult = ShareResult.error(e.message ?: "Error desconocido")
                executeJavaScript(
                    "${WebViewCallbacks.ON_SHARE_RESULT}('${Json.encodeToString(errorResult)}')"
                )
            }
        }
    }
}
