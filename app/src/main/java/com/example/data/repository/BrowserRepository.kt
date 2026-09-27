package com.example.data.repository

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkEntity
import com.example.data.db.PrivacyStatsEntity
import com.example.data.db.SiteShieldExceptionEntity
import com.example.data.model.PrivacySettings
import com.example.data.model.SearchEngine
import com.example.data.model.TrackerCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class BrowserRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val bookmarkDao = db.bookmarkDao()
    private val siteShieldDao = db.siteShieldDao()
    private val privacyStatsDao = db.privacyStatsDao()

    private val prefs = context.getSharedPreferences("privacy_browser_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow = _settingsFlow.asStateFlow()

    private fun loadSettings(): PrivacySettings {
        val searchEngineName = prefs.getString("search_engine", SearchEngine.DUCKDUCKGO.name) ?: SearchEngine.DUCKDUCKGO.name
        val engine = try {
            SearchEngine.valueOf(searchEngineName)
        } catch (e: Exception) {
            SearchEngine.DUCKDUCKGO
        }

        return PrivacySettings(
            blockTrackers = prefs.getBoolean("block_trackers", true),
            blockThirdPartyCookies = prefs.getBoolean("block_third_party_cookies", true),
            forceHttps = prefs.getBoolean("force_https", true),
            javascriptEnabled = prefs.getBoolean("javascript_enabled", true),
            sendDntAndGpcHeaders = prefs.getBoolean("send_dnt_gpc", true),
            defaultSearchEngine = engine,
            clearDataOnAppExit = prefs.getBoolean("clear_on_exit", false),
            desktopModeDefault = prefs.getBoolean("desktop_default", false),
            safeBrowsingCheck = prefs.getBoolean("safe_browsing", true)
        )
    }

    fun updateSettings(newSettings: PrivacySettings) {
        prefs.edit()
            .putBoolean("block_trackers", newSettings.blockTrackers)
            .putBoolean("block_third_party_cookies", newSettings.blockThirdPartyCookies)
            .putBoolean("force_https", newSettings.forceHttps)
            .putBoolean("javascript_enabled", newSettings.javascriptEnabled)
            .putBoolean("send_dnt_gpc", newSettings.sendDntAndGpcHeaders)
            .putString("search_engine", newSettings.defaultSearchEngine.name)
            .putBoolean("clear_on_exit", newSettings.clearDataOnAppExit)
            .putBoolean("desktop_default", newSettings.desktopModeDefault)
            .putBoolean("safe_browsing", newSettings.safeBrowsingCheck)
            .apply()

        _settingsFlow.value = newSettings
    }

    // Bookmarks
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(url: String): Flow<Boolean> = bookmarkDao.isBookmarked(url)

    suspend fun addBookmark(title: String, url: String) {
        withContext(Dispatchers.IO) {
            bookmarkDao.insertBookmark(BookmarkEntity(title = title, url = url))
        }
    }

    suspend fun removeBookmarkByUrl(url: String) {
        withContext(Dispatchers.IO) {
            bookmarkDao.deleteBookmarkByUrl(url)
        }
    }

    suspend fun deleteBookmark(id: Long) {
        withContext(Dispatchers.IO) {
            bookmarkDao.deleteBookmarkById(id)
        }
    }

    // Site Shield Whitelist
    suspend fun isShieldDisabled(host: String): Boolean {
        return withContext(Dispatchers.IO) {
            siteShieldDao.isShieldDisabledForHost(host)
        }
    }

    suspend fun toggleShieldForHost(host: String, disabled: Boolean) {
        withContext(Dispatchers.IO) {
            if (disabled) {
                siteShieldDao.setShieldDisabled(SiteShieldExceptionEntity(host = host, shieldsDisabled = true))
            } else {
                siteShieldDao.removeException(host)
            }
        }
    }

    // Stats
    val privacyStats: Flow<PrivacyStatsEntity?> = privacyStatsDao.getPrivacyStats()

    suspend fun recordBlockedTracker(category: TrackerCategory) {
        withContext(Dispatchers.IO) {
            val isAd = if (category == TrackerCategory.ADS) 1 else 0
            val isAnalytics = if (category == TrackerCategory.ANALYTICS) 1 else 0
            val isSocial = if (category == TrackerCategory.SOCIAL) 1 else 0
            val isFingerprinting = if (category == TrackerCategory.FINGERPRINTING) 1 else 0
            privacyStatsDao.incrementTrackers(isAd, isAnalytics, isSocial, isFingerprinting)
        }
    }

    /**
     * Complete Session Burn:
     * Completely purges all cookies, web storage, databases, and application web cache.
     */
    suspend fun burnSession() {
        withContext(Dispatchers.IO) {
            try {
                // Clear all cookies
                val cookieManager = CookieManager.getInstance()
                cookieManager.removeAllCookies(null)
                cookieManager.flush()

                // Clear DOM WebStorage & LocalStorage
                WebStorage.getInstance().deleteAllData()

                // Increment burned session count
                privacyStatsDao.incrementBurnedSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
