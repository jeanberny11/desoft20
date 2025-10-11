package com.dreamsoft.desoft20.ui.screens.main

import android.Manifest
import android.os.Build
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dreamsoft.desoft20.R
import com.dreamsoft.desoft20.features.barcode.BarcodeResult
import com.dreamsoft.desoft20.ui.components.FullScreenWebView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // Define permissions based on Android version
    val bluetoothPermissions = remember {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }

    val locationPermissions = remember {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    var printEvent by remember { mutableStateOf<PrintRequestEvent.RequestPermissionsAndPrint?>(null) }
    var locationEvent by remember { mutableStateOf<LocationRequestEvent.RequestLocation?>(null) }
    var scanEvent by remember { mutableStateOf<ScanRequestEvent.RequestScan?>(null) }
    var webView: WebView? by remember { mutableStateOf(null) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allGranted = permissionsResult.values.all { it }
        if (printEvent != null) {
            if (allGranted) {
                viewModel.proceedWithPrintingAfterPermission(
                    printEvent!!.data, printEvent!!.restoreState
                )
                printEvent = null
            } else {
                viewModel.onPrintingError(
                    "Permisos de Bluetooth no concedidos.", printEvent!!.restoreState
                )
            }
        }
    }

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allGranted = permissionsResult.values.all { it }
        if (locationEvent != null) {
            if (allGranted) {
                viewModel.proceedWithLastLocationRequest(locationEvent!!.restoreState)
                locationEvent = null
            } else {
                viewModel.onPrintingError(
                    "Permisos de ubicación no concedidos.", locationEvent!!.restoreState
                )
            }
        }
    }

    var hasResumedAtleastOne by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasResumedAtleastOne) {
                    viewModel.refreshDataAndWebView()
                }
            } else {
                hasResumedAtleastOne = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.printRequestSharedEvent.collect { event ->
            when (event) {
                is PrintRequestEvent.RequestPermissionsAndPrint -> {
                    printEvent = event
                    permissionsLauncher.launch(bluetoothPermissions.toTypedArray())
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.locationRequestSharedEvent.collect { event ->
            when (event) {
                is LocationRequestEvent.RequestLocation -> {
                    locationEvent = event
                    locationPermissionsLauncher.launch(locationPermissions.toTypedArray())
                }
            }
        }
    }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (scanEvent != null) {
            if (result.contents == null) {
                viewModel.onScanResult(
                    BarcodeResult.error("No se pudo realizar el escaneo"),
                    scanEvent!!.restoreState
                )
            } else {
                result.contents?.let { barcode ->
                    viewModel.onScanResult(
                        BarcodeResult.success(barcode = barcode, fieldName = scanEvent!!.fieldName),
                        scanEvent!!.restoreState
                    )
                }
            }
            scanEvent = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.scanRequestSharedEvent.collect { event ->
            when (event) {
                is ScanRequestEvent.RequestScan -> {
                    scanEvent = event
                    val options = ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                        setPrompt("Scan a barcode")
                        setCameraId(0)
                        setBeepEnabled(true)
                        setBarcodeImageEnabled(false)
                        setOrientationLocked(true)
                    }
                    scanLauncher.launch(options)
                }
            }
        }
    }

    LaunchedEffect(webView, uiState) {
        if (webView != null) {
            viewModel.webViewEvent.collectLatest { event ->
                when (event) {
                    is WebViewEvent.GoBack -> {
                        if (uiState is MainUiState.Initial) {
                            if (webView!!.canGoBack() && (uiState as MainUiState.Initial).configuration.enableWebNavigation) {
                                webView!!.goBack()
                            }
                        }
                    }

                    is WebViewEvent.ExecuteJavaScript -> {
                        webView!!.post {
                            webView!!.evaluateJavascript(event.script, null)
                        }
                    }

                    WebViewEvent.ClearWebViewHistory -> {
                        webView!!.clearFormData()
                        webView!!.clearHistory()
                    }
                }
            }
        }
    }

    BackHandler {
        when (val currentState = uiState) {
            is MainUiState.Initial -> viewModel.onBackPressed(currentState)
            is MainUiState.Error -> currentState.onRetry()
            is MainUiState.Dialog -> currentState.onDismiss()
            is MainUiState.Loading -> {}
        }
    }

    // CRITICAL: Box layout with WebView always visible underneath
    Box(modifier = Modifier.fillMaxSize()) {
        // WebView is ALWAYS rendered - it never gets removed from composition
        FullScreenWebView(
            uiState = uiState,
            onUistateChanged = { viewModel.onStateChanged(it) },
            javaScriptInterface = if (uiState is MainUiState.Initial) {
                (uiState as MainUiState.Initial).javaScriptInterface
            } else null,
            onWebViewIsReady = { wv -> webView = wv }
        )

        // Overlays appear ON TOP of WebView without removing it
        when (val currentState = uiState) {
            is MainUiState.Dialog -> {
                AlertDialog(
                    onDismissRequest = currentState.onDismiss,
                    title = { Text(text = stringResource(R.string.app_name)) },
                    text = { Text(text = "¿Desea salir de la aplicación?") },
                    confirmButton = {
                        Button(onClick = { viewModel.exitApp() }) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = currentState.onDismiss) {
                            Text(stringResource(R.string.no))
                        }
                    }
                )
            }

            is MainUiState.Error -> {
                ErrorOverlay(
                    onRetry = currentState.onRetry,
                    errorMessage = currentState.message
                )
            }

            is MainUiState.Loading -> {
                LoadingOverlay(message = currentState.message)
            }

            else -> { /* WebView is visible, no overlay */
            }
        }
    }
}

@Composable
private fun LoadingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ErrorOverlay(onRetry: () -> Unit, errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.webview_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}