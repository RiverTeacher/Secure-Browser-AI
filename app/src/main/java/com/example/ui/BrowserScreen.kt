package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.components.BookmarksSheet
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BurnAnimationOverlay
import com.example.ui.components.BurnDialog
import com.example.ui.components.HomePrivacyView
import com.example.ui.components.Omnibox
import com.example.ui.components.PrivacyShieldSheet
import com.example.ui.components.SettingsSheet
import com.example.ui.components.TabsSheet
import com.example.ui.components.WebViewContainer

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val omniboxText by viewModel.omniboxText.collectAsStateWithLifecycle()
    val isOmniboxFocused by viewModel.isOmniboxFocused.collectAsStateWithLifecycle()

    val showShield by viewModel.showPrivacyShield.collectAsStateWithLifecycle()
    val showTabs by viewModel.showTabsSheet.collectAsStateWithLifecycle()
    val showBurn by viewModel.showBurnDialog.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showBookmarks by viewModel.showBookmarksSheet.collectAsStateWithLifecycle()

    val isBurningSession by viewModel.isBurningSession.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val stats by viewModel.privacyStats.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val isShieldDisabled by viewModel.isCurrentSiteShieldDisabled.collectAsStateWithLifecycle()
    val pendingAction by viewModel.pendingWebAction.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept back button to navigate back within WebView or close open sheets
    BackHandler {
        viewModel.handleBackPress()
    }

    // Snackbar notifications
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val isBookmarked = remember(bookmarks, currentTab.url) {
        bookmarks.any { it.url == currentTab.url && currentTab.url.isNotBlank() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ElegantDarkBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column(
                    modifier = Modifier
                        .background(ElegantDarkBg)
                        .statusBarsPadding()
                ) {
                    Omnibox(
                        tab = currentTab,
                        text = omniboxText,
                        isFocused = isOmniboxFocused,
                        tabCount = tabs.size,
                        isShieldDisabled = isShieldDisabled,
                        onTextChange = { viewModel.setOmniboxText(it) },
                        onFocusChange = { viewModel.setOmniboxFocused(it) },
                        onSubmit = { viewModel.submitUrlOrSearch(it) },
                        onOpenShield = { viewModel.showPrivacyShield.value = true },
                        onOpenTabs = { viewModel.showTabsSheet.value = true },
                        onOpenBurn = { viewModel.showBurnDialog.value = true },
                        onReload = { viewModel.reload() }
                    )
                }
            },
            bottomBar = {
                BrowserBottomBar(
                    tab = currentTab,
                    isBookmarked = isBookmarked,
                    onBack = { viewModel.goBack() },
                    onForward = { viewModel.goForward() },
                    onHome = { viewModel.goHome() },
                    onToggleBookmark = { viewModel.toggleBookmark() },
                    onOpenBookmarks = { viewModel.showBookmarksSheet.value = true },
                    onOpenSettings = { viewModel.showSettingsSheet.value = true },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (currentTab.isBlankPage) {
                    HomePrivacyView(
                        settings = settings,
                        stats = stats,
                        bookmarks = bookmarks,
                        onSearchOrUrl = { viewModel.loadUrl(settings.defaultSearchEngine.buildUrl(it)) },
                        onOpenBurnDialog = { viewModel.showBurnDialog.value = true },
                        onOpenShieldSheet = { viewModel.showPrivacyShield.value = true }
                    )
                } else {
                    WebViewContainer(
                        tab = currentTab,
                        privacySettings = settings,
                        isShieldDisabled = isShieldDisabled,
                        pendingAction = pendingAction,
                        onActionConsumed = { viewModel.pendingWebAction.value = null },
                        onPageStarted = { viewModel.onPageStarted(it) },
                        onPageFinished = { url, title, canBack, canFwd ->
                            viewModel.onPageFinished(url, title, canBack, canFwd)
                        },
                        onProgressChanged = { viewModel.onProgressChanged(it) },
                        onTrackerBlocked = { viewModel.onTrackerBlocked(it) }
                    )
                }
            }
        }

        // Sheets & Dialogs
        if (showShield) {
            PrivacyShieldSheet(
                tab = currentTab,
                isShieldDisabled = isShieldDisabled,
                onToggleShield = { viewModel.toggleCurrentSiteShield() },
                onDismiss = { viewModel.showPrivacyShield.value = false }
            )
        }

        if (showTabs) {
            TabsSheet(
                tabs = tabs,
                selectedTabId = currentTab.id,
                onSelectTab = { viewModel.selectTab(it) },
                onCloseTab = { viewModel.closeTab(it) },
                onNewTab = { viewModel.newTab() },
                onBurnAll = { viewModel.showBurnDialog.value = true },
                onDismiss = { viewModel.showTabsSheet.value = false }
            )
        }

        if (showBurn) {
            BurnDialog(
                onConfirmBurn = { viewModel.executeBurnSession() },
                onDismiss = { viewModel.showBurnDialog.value = false }
            )
        }

        if (showBookmarks) {
            BookmarksSheet(
                bookmarks = bookmarks,
                onSelectBookmark = {
                    viewModel.showBookmarksSheet.value = false
                    viewModel.loadUrl(it)
                },
                onDeleteBookmark = { viewModel.deleteBookmark(it) },
                onDismiss = { viewModel.showBookmarksSheet.value = false }
            )
        }

        if (showSettings) {
            SettingsSheet(
                settings = settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onDismiss = { viewModel.showSettingsSheet.value = false }
            )
        }

        // Fullscreen Burn Animation Overlay
        BurnAnimationOverlay(isVisible = isBurningSession)
    }
}
