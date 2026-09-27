package com.example.data.model

data class PrivacySettings(
    val blockTrackers: Boolean = true,
    val blockThirdPartyCookies: Boolean = true,
    val forceHttps: Boolean = true,
    val javascriptEnabled: Boolean = true,
    val sendDntAndGpcHeaders: Boolean = true,
    val defaultSearchEngine: SearchEngine = SearchEngine.DUCKDUCKGO,
    val clearDataOnAppExit: Boolean = false,
    val desktopModeDefault: Boolean = false,
    val safeBrowsingCheck: Boolean = true
)
