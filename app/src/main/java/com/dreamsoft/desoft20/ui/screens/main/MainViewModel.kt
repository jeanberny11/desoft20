package com.dreamsoft.desoft20.ui.screens.main

import android.Manifest
import android.app.Application
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
import com.dreamsoft.desoft20.features.location.LocationManager
import com.dreamsoft.desoft20.features.location.models.LocationData
import com.dreamsoft.desoft20.features.location.models.LocationResult
import com.dreamsoft.desoft20.features.printer.GenericPrinterManager
import com.dreamsoft.desoft20.features.printer.SunmiPrinterManager
import com.dreamsoft.desoft20.features.printer.models.PrintData
import com.dreamsoft.desoft20.features.printer.models.PrinterResult
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
    data class RequestScan(val restoreState: MainUiState) : ScanRequestEvent()
}

@Suppress("SpellCheckingInspection")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val configurationRepository: ConfigurationRepository,
    private val networkUtils: NetworkUtils,
    private val printerManager: GenericPrinterManager,
    private val locationManager: LocationManager,
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
                config = config.copy(localUrl = "file:///android_asset/prestamosoff/index.html")
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
                    val browserIntent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        url.toUri()
                    )
                    browserIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    application.startActivity(browserIntent)
                },
                onGetLastLocation = {
                    onGetLastLocationTrigger()
                },
                onReadBarcode = {

                }
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
                var connected = false
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
                    "${WebViewCallbacks.ON_PRINT_RESULT}(${
                        Json.encodeToString(
                            printResult
                        )
                    })"
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
                    "${WebViewCallbacks.ON_PRINT_RESULT}(${
                        Json.encodeToString(
                            printResult
                        )
                    })"
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
        executeJavaScript("${WebViewCallbacks.ON_PRINT_RESULT}(${Json.encodeToString(printResult)})")
        _uiState.value = MainUiState.Error(message, onRetry = {
            _uiState.value = restoreState
        })
    }

    fun onLocationError(message: String, restoreState: MainUiState) {
        val locationData = LocationData(resultCode = 3, message = message)
        executeJavaScript("${WebViewCallbacks.ON_LOCATION_RESULT}(${Json.encodeToString(locationData)})")
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
                    val locationData = LocationData(
                        resultCode = 3,
                        message = "Ubicacion obtenida",
                        altitude = lastLocation.location.altitude,
                        accuracy = lastLocation.location.accuracy,
                        latitude = lastLocation.location.latitude,
                        longitude = lastLocation.location.longitude
                    )
                    executeJavaScript(
                        "${WebViewCallbacks.ON_LOCATION_RESULT}(${
                            Json.encodeToString(
                                locationData
                            )
                        })"
                    )
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

    private fun onRequestScan() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _scanRequestSharedEvent.emit(ScanRequestEvent.RequestScan(currentState))
        }
    }

    fun onStateChanged(state: MainUiState) {
        _uiState.value = state
    }

    fun onRefresh(state: MainUiState.Initial) {
        viewModelScope.launch {
            val url: String = if (state.configuration.useLocalUrl) {
                "file:///android_asset/prestamosoff/index.html"
            } else {
                state.configuration.remoteUrl.ifEmpty {
                    "file:///android_asset/www/index.html" // Fallback
                }
            }
            _uiState.value = state.copy(currentUrl = url)
        }
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

    fun onWebViewIsReady(state: MainUiState.Initial, webView: WebView) {
        // This function seems to collect events, should likely be called once.
        // Or manage collection lifecycle carefully if called multiple times.
        viewModelScope.launch {
            webViewEvent.collectLatest { event -> // Use webViewEvent (the SharedFlow)
                when (event) {
                    is WebViewEvent.GoBack -> {
                        if (webView.canGoBack() && state.configuration.enableWebNavigation) {
                            webView.goBack()
                        }
                    }

                    is WebViewEvent.ExecuteJavaScript -> {
                        webView.post { // Ensure it runs on the UI thread
                            webView.evaluateJavascript(event.script, null)
                        }
                    }

                    WebViewEvent.ClearWebViewHistory -> {
                        webView.clearFormData()
                        webView.clearHistory()
                    }
                }
            }
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

    fun onScanResult(barcode: String,restoreState: MainUiState) {
        val result = BarcodeResult(resultCode = 3, message = "Exito", barcode = barcode)
        executeJavaScript("${WebViewCallbacks.ON_LOCATION_RESULT}(${Json.encodeToString(result)})")
        onStateChanged(restoreState)
    }
}
