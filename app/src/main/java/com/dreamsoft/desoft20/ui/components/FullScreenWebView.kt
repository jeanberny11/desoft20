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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dreamsoft.desoft20.R
import com.dreamsoft.desoft20.ui.screens.main.MainUiState
import com.dreamsoft.desoft20.webview.WebViewJavaScriptInterface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullScreenWebView(
    uiState: MainUiState.Initial,
    onUistateChanged: (MainUiState) -> Unit,
    onRefresh: () -> Unit,
    javaScriptInterface: WebViewJavaScriptInterface,
    onWebViewIsReady: (WebView) -> Unit,
) {
    var webView: WebView? by remember { mutableStateOf(null) }

    // Expose WebView back functionality to parent
    LaunchedEffect(webView, uiState) {
        webView?.let { wv ->
            onWebViewIsReady(wv)
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            SwipeRefreshLayout(context).apply {
                val swrl = this
                val webViewInstance = WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    addJavascriptInterface(javaScriptInterface,"AndroidInterfaces")
                    // WebView settings
                    settings.apply {
                        loadsImagesAutomatically = true
                        javaScriptEnabled = true
                        setSupportZoom(uiState.configuration.enableZoom)
                        builtInZoomControls = uiState.configuration.enableZoom
                        displayZoomControls = uiState.configuration.enableZoom
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        setGeolocationEnabled(true)
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    // WebView client
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            swrl.isRefreshing=true
                            onUistateChanged(uiState.copy(canGoBack = view?.canGoBack() == true))
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            if(swrl.isRefreshing){
                                swrl.isRefreshing = false
                            }
                            onUistateChanged(uiState.copy(canGoBack = view?.canGoBack() == true))
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
                                onUistateChanged(MainUiState.Error(description?:"Network error", onRetry = {
                                    onUistateChanged(uiState)
                                }))
                            }
                        }

                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            // For development - in production, handle SSL errors properly
                            handler?.proceed()
                        }
                    }

                    // WebChrome client for progress and other features
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            // Progress handling if needed
                        }

                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String?,
                            callback: GeolocationPermissions.Callback?
                        ) {
                            // Grant geolocation permission
                            callback?.invoke(origin, true, true)
                        }

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            // Log web console messages for debugging
                            consoleMessage?.let {
                                println("${it.messageLevel()}: ${it.message()}")
                                //android.util.Log.d("WebView", "${it.messageLevel()}: ${it.message()}")
                            }
                            return super.onConsoleMessage(consoleMessage)
                        }
                    }
                }

                webView = webViewInstance
                addView(webViewInstance)

                // Swipe refresh listener
                setOnRefreshListener {
                    onRefresh()
                    webViewInstance.reload()
                }

                // Set colors
                setColorSchemeResources(
                    R.color.primary,
                    R.color.secondary,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light
                )
            }
        },
        update = { swipeRefreshLayout ->
            swipeRefreshLayout.isRefreshing = false

            // Load URL when it changes
            webView?.let { webView ->
                if (uiState.currentUrl.isNotEmpty() && webView.url != uiState.currentUrl) {
                    webView.loadUrl(uiState.currentUrl)
                }
            }
        }
    )
}
