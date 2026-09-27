package com.example.data.model

import java.util.UUID

data class TabModel(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "",
    val title: String = "新しいプライベートタブ",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isHttps: Boolean = false,
    val blockedTrackers: List<BlockedTrackerItem> = emptyList(),
    val isDesktopMode: Boolean = false
) {
    val isBlankPage: Boolean
        get() = url.isBlank() || url == "about:blank"

    val displayDomain: String
        get() {
            if (isBlankPage) return "新しいタブ"
            return try {
                val uri = android.net.Uri.parse(url)
                uri.host?.removePrefix("www.") ?: url
            } catch (e: Exception) {
                url
            }
        }

    val securityGrade: String
        get() {
            if (isBlankPage) return "A+"
            if (!isHttps) return "D"
            return when {
                blockedTrackers.isEmpty() -> "A+"
                blockedTrackers.size < 5 -> "A"
                blockedTrackers.size < 15 -> "B"
                else -> "C"
            }
        }
}
