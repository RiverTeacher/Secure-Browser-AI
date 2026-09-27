package com.example.data.model

enum class SearchEngine(
    val displayName: String,
    val searchUrlPrefix: String,
    val homeUrl: String,
    val isPrivacyFocused: Boolean,
    val descriptionJa: String
) {
    DUCKDUCKGO(
        displayName = "DuckDuckGo",
        searchUrlPrefix = "https://duckduckgo.com/?q=",
        homeUrl = "https://duckduckgo.com",
        isPrivacyFocused = true,
        descriptionJa = "履歴を追跡せずプライバシーを守る検索エンジン"
    ),
    BRAVE(
        displayName = "Brave Search",
        searchUrlPrefix = "https://search.brave.com/search?q=",
        homeUrl = "https://search.brave.com",
        isPrivacyFocused = true,
        descriptionJa = "独自の独立インデックスと追跡ゼロの検索"
    ),
    STARTPAGE(
        displayName = "Startpage",
        searchUrlPrefix = "https://www.startpage.com/sp/search?query=",
        homeUrl = "https://www.startpage.com",
        isPrivacyFocused = true,
        descriptionJa = "Googleの検索結果をプライバシー保護経由で取得"
    ),
    QWANT(
        displayName = "Qwant",
        searchUrlPrefix = "https://www.qwant.com/?q=",
        homeUrl = "https://www.qwant.com",
        isPrivacyFocused = true,
        descriptionJa = "欧州の厳格な個人情報保護法に準拠した検索"
    ),
    GOOGLE(
        displayName = "Google",
        searchUrlPrefix = "https://www.google.com/search?q=",
        homeUrl = "https://www.google.com",
        isPrivacyFocused = false,
        descriptionJa = "標準検索エンジン（追跡あり）"
    );

    fun buildUrl(query: String): String {
        val trimmed = query.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }
        // Check if looks like domain name without protocol (e.g. "example.com" or "wikipedia.org/wiki")
        if (trimmed.contains(".") && !trimmed.contains(" ") && !trimmed.startsWith(".")) {
            return "https://$trimmed"
        }
        return searchUrlPrefix + java.net.URLEncoder.encode(trimmed, "UTF-8")
    }
}
