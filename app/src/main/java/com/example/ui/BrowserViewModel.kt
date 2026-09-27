package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BookmarkEntity
import com.example.data.db.PrivacyStatsEntity
import com.example.data.model.BlockedTrackerItem
import com.example.data.model.PrivacySettings
import com.example.data.model.SearchEngine
import com.example.data.model.TabModel
import com.example.data.model.TrackerCategory
import com.example.data.repository.BrowserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BrowserRepository(application)

    // Tabs
    private val initialTab = TabModel(url = "")
    private val _tabs = MutableStateFlow<List<TabModel>>(listOf(initialTab))
    val tabs: StateFlow<List<TabModel>> = _tabs.asStateFlow()

    private val _selectedTabId = MutableStateFlow(initialTab.id)
    val selectedTabId: StateFlow<String> = _selectedTabId.asStateFlow()

    // Active tab
    val currentTab: StateFlow<TabModel> = combine(_tabs, _selectedTabId) { tabList, currentId ->
        tabList.find { it.id == currentId } ?: tabList.firstOrNull() ?: initialTab
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialTab)

    // Omnibox
    private val _omniboxText = MutableStateFlow("")
    val omniboxText: StateFlow<String> = _omniboxText.asStateFlow()

    private val _isOmniboxFocused = MutableStateFlow(false)
    val isOmniboxFocused: StateFlow<Boolean> = _isOmniboxFocused.asStateFlow()

    // Sheet / Dialog visibility
    val showPrivacyShield = MutableStateFlow(false)
    val showTabsSheet = MutableStateFlow(false)
    val showBurnDialog = MutableStateFlow(false)
    val showSettingsSheet = MutableStateFlow(false)
    val showBookmarksSheet = MutableStateFlow(false)

    // Burn state animation & Feedback
    val isBurningSession = MutableStateFlow(false)
    val snackbarMessage = MutableStateFlow<String?>(null)

    // Settings & Stats from repo
    val settings: StateFlow<PrivacySettings> = repository.settingsFlow
    val privacyStats: StateFlow<PrivacyStatsEntity?> = repository.privacyStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Webview navigation actions request (trigger reload, goBack, goForward)
    sealed class WebAction {
        object GoBack : WebAction()
        object GoForward : WebAction()
        object Reload : WebAction()
        object Stop : WebAction()
        data class LoadUrl(val url: String) : WebAction()
        object ClearCache : WebAction()
    }

    val pendingWebAction = MutableStateFlow<WebAction?>(null)

    // Current site shield disabled state
    val isCurrentSiteShieldDisabled = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            currentTab.collect { tab ->
                if (!_isOmniboxFocused.value) {
                    _omniboxText.value = if (tab.isBlankPage) "" else tab.url
                }
                checkCurrentSiteShield(tab.url)
            }
        }
    }

    private fun checkCurrentSiteShield(url: String) {
        viewModelScope.launch {
            val host = try {
                android.net.Uri.parse(url).host
            } catch (e: Exception) {
                null
            }
            if (host != null) {
                isCurrentSiteShieldDisabled.value = repository.isShieldDisabled(host)
            } else {
                isCurrentSiteShieldDisabled.value = false
            }
        }
    }

    fun setOmniboxText(text: String) {
        _omniboxText.value = text
    }

    fun setOmniboxFocused(focused: Boolean) {
        _isOmniboxFocused.value = focused
        if (!focused) {
            val tab = currentTab.value
            _omniboxText.value = if (tab.isBlankPage) "" else tab.url
        }
    }

    fun submitUrlOrSearch(input: String) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return

        val urlToLoad = settings.value.defaultSearchEngine.buildUrl(trimmed)
        loadUrl(urlToLoad)
    }

    fun loadUrl(url: String) {
        var finalUrl = url
        if (settings.value.forceHttps && finalUrl.startsWith("http://")) {
            finalUrl = "https://" + finalUrl.removePrefix("http://")
        }

        updateActiveTab { it.copy(url = finalUrl, isLoading = true, progress = 10, blockedTrackers = emptyList()) }
        _omniboxText.value = finalUrl
        _isOmniboxFocused.value = false
        pendingWebAction.value = WebAction.LoadUrl(finalUrl)
    }

    fun newTab(url: String = "") {
        val newTab = TabModel(
            url = url,
            isDesktopMode = settings.value.desktopModeDefault
        )
        _tabs.value = _tabs.value + newTab
        _selectedTabId.value = newTab.id
        showTabsSheet.value = false
        if (url.isNotBlank()) {
            loadUrl(url)
        } else {
            _omniboxText.value = ""
        }
    }

    fun selectTab(tabId: String) {
        _selectedTabId.value = tabId
        showTabsSheet.value = false
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value
        if (currentList.size <= 1) {
            // If last tab is closed, reset it to blank home tab
            val resetTab = TabModel(url = "")
            _tabs.value = listOf(resetTab)
            _selectedTabId.value = resetTab.id
            _omniboxText.value = ""
            return
        }

        val indexToClose = currentList.indexOfFirst { it.id == tabId }
        val updatedList = currentList.filter { it.id != tabId }
        _tabs.value = updatedList

        if (_selectedTabId.value == tabId) {
            val newIndex = (indexToClose - 1).coerceAtLeast(0)
            _selectedTabId.value = updatedList[newIndex].id
        }
    }

    fun goBack() {
        pendingWebAction.value = WebAction.GoBack
    }

    fun goForward() {
        pendingWebAction.value = WebAction.GoForward
    }

    fun reload() {
        pendingWebAction.value = WebAction.Reload
    }

    fun goHome() {
        updateActiveTab { it.copy(url = "", title = "新しいプライベートタブ", blockedTrackers = emptyList()) }
        _omniboxText.value = ""
        _isOmniboxFocused.value = false
    }

    fun toggleDesktopMode() {
        val active = currentTab.value
        val updatedMode = !active.isDesktopMode
        updateActiveTab { it.copy(isDesktopMode = updatedMode) }
        reload()
    }

    fun toggleCurrentSiteShield() {
        val active = currentTab.value
        val host = try {
            android.net.Uri.parse(active.url).host
        } catch (e: Exception) {
            null
        } ?: return

        viewModelScope.launch {
            val currentDisabled = isCurrentSiteShieldDisabled.value
            val newDisabled = !currentDisabled
            repository.toggleShieldForHost(host, newDisabled)
            isCurrentSiteShieldDisabled.value = newDisabled
            reload()
        }
    }

    fun toggleBookmark() {
        val tab = currentTab.value
        if (tab.isBlankPage) return

        viewModelScope.launch {
            val isAlready = bookmarks.value.any { it.url == tab.url }
            if (isAlready) {
                repository.removeBookmarkByUrl(tab.url)
                snackbarMessage.value = "ブックマークを解除しました"
            } else {
                val title = if (tab.title.isNotBlank()) tab.title else tab.displayDomain
                repository.addBookmark(title, tab.url)
                snackbarMessage.value = "プライベートブックマークに保存しました"
            }
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            repository.deleteBookmark(id)
        }
    }

    fun updateSettings(newSettings: PrivacySettings) {
        repository.updateSettings(newSettings)
    }

    /**
     * "Burn" / Fire feature:
     * Clears cookies, cache, web storage, and resets all tabs.
     */
    fun executeBurnSession() {
        viewModelScope.launch {
            showBurnDialog.value = false
            isBurningSession.value = true

            // Trigger webview cache clearance
            pendingWebAction.value = WebAction.ClearCache

            // Purge repository cookies & storage
            repository.burnSession()

            delay(1200) // Visual burn animation duration

            // Reset tabs to fresh private blank tab
            val freshTab = TabModel(url = "")
            _tabs.value = listOf(freshTab)
            _selectedTabId.value = freshTab.id
            _omniboxText.value = ""
            _isOmniboxFocused.value = false

            isBurningSession.value = false
            snackbarMessage.value = "🔥 セッション・Cookie・キャッシュを完全消去しました"
        }
    }

    // Callbacks from WebView
    fun onPageStarted(url: String) {
        val isHttps = url.startsWith("https://")
        updateActiveTab { it.copy(url = url, isLoading = true, isHttps = isHttps, progress = 10) }
        _omniboxText.value = url
        checkCurrentSiteShield(url)
    }

    fun onPageFinished(url: String, title: String?, canGoBack: Boolean, canGoForward: Boolean) {
        val isHttps = url.startsWith("https://")
        updateActiveTab {
            it.copy(
                url = url,
                title = title ?: it.displayDomain,
                isLoading = false,
                progress = 100,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                isHttps = isHttps
            )
        }
    }

    fun onProgressChanged(progress: Int) {
        updateActiveTab { it.copy(progress = progress, isLoading = progress < 100) }
    }

    fun onTrackerBlocked(item: BlockedTrackerItem) {
        val activeId = _selectedTabId.value
        updateActiveTab { tab ->
            // Avoid duplicate domain entries in the same page view
            if (tab.blockedTrackers.any { it.domain == item.domain }) {
                tab
            } else {
                tab.copy(blockedTrackers = tab.blockedTrackers + item)
            }
        }
        // Record to repository stats
        viewModelScope.launch {
            repository.recordBlockedTracker(item.category)
        }
    }

    private fun updateActiveTab(transform: (TabModel) -> TabModel) {
        val currentId = _selectedTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) transform(tab) else tab
        }
    }

    fun handleBackPress(): Boolean {
        val active = currentTab.value
        if (showPrivacyShield.value) {
            showPrivacyShield.value = false
            return true
        }
        if (showTabsSheet.value) {
            showTabsSheet.value = false
            return true
        }
        if (showBookmarksSheet.value) {
            showBookmarksSheet.value = false
            return true
        }
        if (showSettingsSheet.value) {
            showSettingsSheet.value = false
            return true
        }
        if (showBurnDialog.value) {
            showBurnDialog.value = false
            return true
        }
        if (_isOmniboxFocused.value) {
            _isOmniboxFocused.value = false
            return true
        }
        if (active.canGoBack) {
            goBack()
            return true
        }
        if (!active.isBlankPage) {
            goHome()
            return true
        }
        return false
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }
}
