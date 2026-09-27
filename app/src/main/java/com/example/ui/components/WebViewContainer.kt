package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.BlockedTrackerItem
import com.example.data.model.PrivacySettings
import com.example.data.model.TabModel
import com.example.data.privacy.TrackerBlocker
import com.example.ui.BrowserViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    tab: TabModel,
    privacySettings: PrivacySettings,
    isShieldDisabled: Boolean,
    pendingAction: BrowserViewModel.WebAction?,
    onActionConsumed: () -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?, Boolean, Boolean) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onTrackerBlocked: (BlockedTrackerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Keep WebView instance remembered for this tab
    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Privacy Defaults on WebSettings
            this.settings.apply {
                javaScriptEnabled = privacySettings.javascriptEnabled
                domStorageEnabled = true
                databaseEnabled = false
                saveFormData = false
                allowFileAccess = false
                allowContentAccess = false
                setGeolocationEnabled(false)
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            // Third-Party Cookie Policy
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, !privacySettings.blockThirdPartyCookies)
        }
    }

    // Update settings dynamically if changed
    LaunchedEffect(privacySettings, tab.isDesktopMode) {
        webView.settings.javaScriptEnabled = privacySettings.javascriptEnabled
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, !privacySettings.blockThirdPartyCookies)

        // Desktop User Agent toggle
        if (tab.isDesktopMode) {
            webView.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        } else {
            webView.settings.userAgentString = null // Default mobile user agent
        }
    }

    // Set WebChromeClient
    DisposableEffect(tab.id) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                // Deny by default for strict privacy
                callback?.invoke(origin, false, false)
            }
        }

        // Set WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request == null) return null

                val reqUrl = request.url.toString()
                val currentHost = try {
                    Uri.parse(tab.url).host
                } catch (e: Exception) {
                    null
                }

                // If shields are active, check against TrackerBlocker
                if (privacySettings.blockTrackers && !isShieldDisabled) {
                    val category = TrackerBlocker.checkTracker(reqUrl, currentHost)
                    if (category != null) {
                        val trackerHost = try {
                            Uri.parse(reqUrl).host ?: reqUrl
                        } catch (e: Exception) {
                            reqUrl
                        }
                        onTrackerBlocked(
                            BlockedTrackerItem(
                                domain = trackerHost,
                                category = category,
                                url = reqUrl
                            )
                        )
                        return TrackerBlocker.createEmptyResponse()
                    }
                }

                return null
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request == null) return false
                val url = request.url.toString()

                // HTTPS auto-upgrade
                if (privacySettings.forceHttps && url.startsWith("http://")) {
                    val upgraded = "https://" + url.removePrefix("http://")
                    view?.loadUrl(upgraded)
                    return true
                }

                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false
                }

                // Handle external apps safely (e.g. mailto, tel)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Ignore unsupported scheme
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let { onPageStarted(it) }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    onPageFinished(
                        it,
                        view?.title,
                        view?.canGoBack() == true,
                        view?.canGoForward() == true
                    )
                }
            }
        }

        onDispose {
            webView.stopLoading()
        }
    }

    // Handle pending WebActions
    LaunchedEffect(pendingAction) {
        when (pendingAction) {
            is BrowserViewModel.WebAction.GoBack -> {
                if (webView.canGoBack()) webView.goBack()
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.GoForward -> {
                if (webView.canGoForward()) webView.goForward()
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.Reload -> {
                webView.reload()
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.Stop -> {
                webView.stopLoading()
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.LoadUrl -> {
                // Add DNT & GPC privacy headers to outgoing main request
                val extraHeaders = mutableMapOf<String, String>()
                if (privacySettings.sendDntAndGpcHeaders) {
                    extraHeaders["DNT"] = "1"
                    extraHeaders["Sec-GPC"] = "1"
                }
                webView.loadUrl(pendingAction.url, extraHeaders)
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.ClearCache -> {
                webView.clearCache(true)
                webView.clearHistory()
                webView.clearFormData()
                onActionConsumed()
            }
            null -> {}
        }
    }

    // Initial load if tab has URL but webview hasn't loaded it
    LaunchedEffect(tab.url) {
        if (tab.url.isNotBlank() && tab.url != "about:blank" && webView.url != tab.url) {
            val extraHeaders = mutableMapOf<String, String>()
            if (privacySettings.sendDntAndGpcHeaders) {
                extraHeaders["DNT"] = "1"
                extraHeaders["Sec-GPC"] = "1"
            }
            webView.loadUrl(tab.url, extraHeaders)
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
