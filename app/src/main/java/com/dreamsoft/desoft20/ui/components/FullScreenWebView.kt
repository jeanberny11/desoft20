package com.dreamsoft.desoft20.ui.components

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dreamsoft.desoft20.R
import com.dreamsoft.desoft20.ui.screens.main.MainUiState
import com.dreamsoft.desoft20.webview.WebViewJavaScriptInterface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullScreenWebView(
    uiState: MainUiState,
    onUistateChanged: (MainUiState) -> Unit,
    javaScriptInterface: WebViewJavaScriptInterface?,
    onWebViewIsReady: (WebView) -> Unit,
) {
    val context = LocalContext.current

    // CRITICAL: Create WebView instance ONCE and remember it
    // This prevents recreation during recomposition
    val webViewHolder = remember {
        WebViewHolder()
    }

    // Track the last loaded URL to prevent unnecessary reloads
    val lastLoadedUrl = remember { mutableStateOf("") }

    // Expose WebView to parent when ready
    LaunchedEffect(webViewHolder.webView) {
        webViewHolder.webView?.let { onWebViewIsReady(it) }
    }

    // Handle JavaScript interface changes
    LaunchedEffect(javaScriptInterface) {
        webViewHolder.webView?.let { webView ->
            // Remove old interface if exists
            webView.removeJavascriptInterface("AndroidInterfaces")

            // Add new interface if not null
            if (javaScriptInterface != null) {
                webView.addJavascriptInterface(javaScriptInterface, "AndroidInterfaces")
            }
        }
    }

    // Handle URL changes without recreating WebView
    LaunchedEffect(uiState) {
        if (uiState is MainUiState.Initial) {
            webViewHolder.webView?.let { webView ->
                val newUrl = uiState.currentUrl

                // Load URL if it's different from the last one we loaded
                if (newUrl.isNotEmpty() && newUrl != lastLoadedUrl.value) {
                    webView.loadUrl(newUrl)
                    lastLoadedUrl.value = newUrl
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Cleanup when composable is permanently disposed
            webViewHolder.webView?.apply {
                stopLoading()
                removeJavascriptInterface("AndroidInterfaces")
                destroy()
            }
            webViewHolder.swipeRefreshLayout?.removeAllViews()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { factoryContext ->
            // Create SwipeRefreshLayout
            SwipeRefreshLayout(factoryContext).apply {
                val swrl = this

                val swipeRefreshEnabled = if (uiState is MainUiState.Initial) {
                    uiState.configuration.enableSwipeRefresh
                } else {
                    false
                }

                isEnabled = swipeRefreshEnabled

                // Create WebView ONCE
                val webView = WebView(factoryContext).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Configure WebView settings
                    val enabledZoom = if (uiState is MainUiState.Initial) {
                        uiState.configuration.enableZoom
                    } else {
                        false
                    }

                    settings.apply {
                        loadsImagesAutomatically = true
                        javaScriptEnabled = true
                        setSupportZoom(enabledZoom)
                        builtInZoomControls = enabledZoom
                        displayZoomControls = enabledZoom
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        setGeolocationEnabled(true)
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        databaseEnabled = true
                    }

                    // WebView client
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?
                        ) {
                            swrl.isRefreshing = true
                            if (uiState is MainUiState.Initial) {
                                onUistateChanged(uiState.copy(canGoBack = view?.canGoBack() == true))
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            if (swrl.isRefreshing) {
                                swrl.isRefreshing = false
                            }
                            if (uiState is MainUiState.Initial) {
                                onUistateChanged(uiState.copy(canGoBack = view?.canGoBack() == true))
                            }
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            if (errorCode == ERROR_HOST_LOOKUP ||
                                errorCode == ERROR_CONNECT ||
                                errorCode == ERROR_TIMEOUT
                            ) {
                                onUistateChanged(
                                    MainUiState.Error(
                                        description ?: "Network error",
                                        onRetry = {
                                            view?.reload()
                                        })
                                )
                            }
                        }

                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            handler?.proceed()
                        }
                    }

                    // WebChrome client
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            // Progress handling if needed
                        }

                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String?,
                            callback: GeolocationPermissions.Callback?
                        ) {
                            callback?.invoke(origin, true, true)
                        }

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            consoleMessage?.let {
                                println("${it.messageLevel()}: ${it.message()}")
                            }
                            return super.onConsoleMessage(consoleMessage)
                        }
                    }

                    // Don't load URL here - let LaunchedEffect handle it
                    // This prevents issues with state synchronization
                }

                // Store references
                webViewHolder.webView = webView
                webViewHolder.swipeRefreshLayout = this

                // Add WebView to SwipeRefreshLayout
                addView(webView)

                // Configure swipe refresh
                setOnRefreshListener {
                    webView.reload()
                }

                setColorSchemeResources(
                    R.color.primary,
                    R.color.secondary,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light
                )
            }
        },
        update = { swipeRefreshLayout ->
            // Update is called on recomposition but WebView persists
            // Only update refresh state, don't recreate anything
            swipeRefreshLayout.isRefreshing = false
        }
    )
}

// Helper class to hold WebView reference across recompositions
private class WebViewHolder {
    var webView: WebView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
}