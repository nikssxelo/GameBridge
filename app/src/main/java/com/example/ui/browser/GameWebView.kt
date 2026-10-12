package com.example.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentRed
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.web.GameBridgeScript

class WebViewCommander {
    var webViewInstance: WebView? = null

    fun evaluateJs(script: String) {
        webViewInstance?.post {
            webViewInstance?.evaluateJavascript(script, null)
        }
    }

    fun loadUrl(url: String) {
        webViewInstance?.post {
            val formatted = formatGameUrl(url)
            webViewInstance?.loadUrl(formatted)
        }
    }

    fun goBack() {
        webViewInstance?.post {
            if (webViewInstance?.canGoBack() == true) {
                webViewInstance?.goBack()
            }
        }
    }

    fun goForward() {
        webViewInstance?.post {
            if (webViewInstance?.canGoForward() == true) {
                webViewInstance?.goForward()
            }
        }
    }

    fun reload() {
        webViewInstance?.post {
            webViewInstance?.reload()
        }
    }
}

fun formatGameUrl(input: String): String {
    val trimmed = input.trim()
    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
        else -> "https://duckduckgo.com/?q=" + java.net.URLEncoder.encode(trimmed, "UTF-8")
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GameWebView(
    initialUrl: String,
    commander: WebViewCommander,
    onUrlChanged: (url: String) -> Unit,
    onTitleChanged: (title: String) -> Unit,
    onLoadingStateChanged: (isLoading: Boolean, progress: Int) -> Unit,
    onNavStateChanged: (canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var currentFailedUrl by remember { mutableStateOf("") }

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Hardware acceleration & performance settings for HTML5 / WebGL games
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(android.graphics.Color.parseColor("#0B101D"))

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false

                // Security restrictions: no local files or content schemes from internet
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val uri = request?.url ?: return true
                    val scheme = uri.scheme?.lowercase()
                    // Strict security requirement: Only allow HTTP and HTTPS navigation
                    return if (scheme == "http" || scheme == "https") {
                        false
                    } else {
                        // Block any custom intent, tel, mailto, javascript, or file schemes
                        true
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    hasError = false
                    val safeUrl = url ?: ""
                    onUrlChanged(safeUrl)
                    onLoadingStateChanged(true, 10)
                    onNavStateChanged(canGoBack(), canGoForward())

                    // Early injection of the GameBridge synthetic bridge
                    view?.evaluateJavascript(GameBridgeScript.INJECTION_RUNTIME, null)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val safeUrl = url ?: ""
                    onUrlChanged(safeUrl)
                    onLoadingStateChanged(false, 100)
                    onNavStateChanged(canGoBack(), canGoForward())

                    // Final injection and auto-focus canvas
                    view?.evaluateJavascript(GameBridgeScript.INJECTION_RUNTIME, null)
                    view?.evaluateJavascript(GameBridgeScript.buildFocusGameJs(), null)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        hasError = true
                        currentFailedUrl = request.url?.toString() ?: ""
                        errorMessage = error?.description?.toString() ?: "Failed to load page"
                        onLoadingStateChanged(false, 100)
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onLoadingStateChanged(newProgress < 100, newProgress)
                    onNavStateChanged(canGoBack(), canGoForward())
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    onTitleChanged(title ?: "")
                }
            }

            loadUrl(formatGameUrl(initialUrl))
        }
    }

    DisposableEffect(webView) {
        commander.webViewInstance = webView
        webView.onResume()
        onDispose {
            commander.webViewInstance = null
            webView.onPause()
            webView.stopLoading()
            webView.destroy()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )

        // Error Screen overlay when website fails to load or offline
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GamingNavyDark.copy(alpha = 0.96f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingNavySurface),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Game Loading Error",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )
                        if (currentFailedUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentFailedUrl,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                hasError = false
                                webView.reload()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text("Retry Connection", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
