package com.example.data.model

enum class TrackerCategory(
    val titleJa: String,
    val descriptionJa: String,
    val iconName: String
) {
    ADS("広告・アドネットワーク", "行動追跡型広告やバナー配信スクリプト", "Ad"),
    ANALYTICS("アクセス解析・テレメトリ", "ユーザー行動、クリック、スクロール計測ツール", "Analytics"),
    SOCIAL("SNS追跡・ピクセル", "SNS連携ボタンや行動追跡ピクセル", "Social"),
    FINGERPRINTING("指紋認証・データブローカー", "ブラウザ特性や端末識別情報収集", "Fingerprint"),
    OTHER("未分類トラッカー", "不審なリダイレクトや外部ビーコン", "Other")
}

data class BlockedTrackerItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val domain: String,
    val category: TrackerCategory,
    val url: String,
    val blockedAt: Long = System.currentTimeMillis()
)
