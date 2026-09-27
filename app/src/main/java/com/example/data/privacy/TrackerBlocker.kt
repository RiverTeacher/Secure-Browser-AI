package com.example.data.privacy

import android.net.Uri
import com.example.data.model.BlockedTrackerItem
import com.example.data.model.TrackerCategory
import java.util.Locale

object TrackerBlocker {

    // Common Ad domains
    private val adDomains = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "adcolony.com",
        "amazon-adsystem.com",
        "criteo.com",
        "criteo.net",
        "outbrain.com",
        "taboola.com",
        "openx.net",
        "pubmatic.com",
        "rubiconproject.com",
        "popads.net",
        "adnxs.com",
        "adroll.com",
        "casalemedia.com",
        "moatads.com",
        "serving-sys.com",
        "smartadserver.com",
        "advertising.com",
        "zemanta.com",
        "revcontent.com",
        "sharethrough.com",
        "inmobi.com",
        "applovin.com",
        "media.net",
        "sovrn.com",
        "bidswitch.net",
        "yieldmo.com",
        "triplelift.com",
        "teads.tv",
        "admob.com"
    )

    // Analytics & Telemetry domains
    private val analyticsDomains = setOf(
        "google-analytics.com",
        "analytics.google.com",
        "hotjar.com",
        "segment.io",
        "segment.com",
        "mixpanel.com",
        "amplitude.com",
        "clarity.ms",
        "mouseflow.com",
        "crazyegg.com",
        "heapanalytics.com",
        "fullstory.com",
        "chartbeat.com",
        "statcounter.com",
        "quantserve.com",
        "scorecardresearch.com",
        "appsflyer.com",
        "adjust.com",
        "branch.io",
        "kochava.com",
        "singular.net",
        "mc.yandex.ru",
        "analytics.tiktok.com",
        "umeng.com",
        "logx.optimizely.com",
        "speedcurve.com",
        "inspectlet.com",
        "luckyorange.com"
    )

    // Social trackers
    private val socialDomains = setOf(
        "connect.facebook.net",
        "pixel.facebook.com",
        "graph.facebook.com",
        "platform.twitter.com",
        "static.ads-twitter.com",
        "ads-api.twitter.com",
        "widgets.pinterest.com",
        "tr.snapchat.com",
        "px.ads.linkedin.com",
        "snap.licdn.com",
        "analytics.twitter.com"
    )

    // Fingerprinting / Data broker domains
    private val fingerprintDomains = setOf(
        "fingerprintjs.com",
        "fpjs.io",
        "threatmetrix.com",
        "iovation.com",
        "bluekai.com",
        "krxd.net",
        "demdex.net",
        "agkn.com",
        "rlcdn.com",
        "tapad.com",
        "id5-sync.com",
        "adsystem.com",
        "scorecardresearch.com",
        "addthis.com",
        "sharethis.com"
    )

    /**
     * Checks if the given URL is a tracker.
     * Returns the TrackerCategory if blocked, or null if allowed.
     */
    fun checkTracker(url: String, currentSiteHost: String? = null): TrackerCategory? {
        val uri = try {
            Uri.parse(url)
        } catch (e: Exception) {
            return null
        }

        val host = uri.host?.lowercase(Locale.ROOT) ?: return null

        // Allow first-party requests unless explicitly identified as tracking path
        if (currentSiteHost != null && (host == currentSiteHost || host.endsWith(".$currentSiteHost"))) {
            // First-party tracking scripts check
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""
            if (path.contains("/gtag/js") || path.contains("/analytics.js") || path.contains("/fbevents.js")) {
                return TrackerCategory.ANALYTICS
            }
            return null
        }

        // Check against known category lists
        for (domain in adDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return TrackerCategory.ADS
            }
        }

        for (domain in analyticsDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return TrackerCategory.ANALYTICS
            }
        }

        for (domain in socialDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return TrackerCategory.SOCIAL
            }
        }

        for (domain in fingerprintDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return TrackerCategory.FINGERPRINTING
            }
        }

        // Generic pattern inspection
        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        if (path.endsWith("/fbevents.js") || host.contains("analytics") || host.contains("telemetry")) {
            return TrackerCategory.ANALYTICS
        }
        if (path.contains("doubleclick") || host.startsWith("ads.") || host.startsWith("adserver.")) {
            return TrackerCategory.ADS
        }

        return null
    }

    /**
     * Creates a dummy 0-byte response for blocked resources.
     */
    fun createEmptyResponse(): android.webkit.WebResourceResponse {
        return android.webkit.WebResourceResponse(
            "text/plain",
            "UTF-8",
            200,
            "OK",
            mapOf("X-Blocked-By" to "PrivacyBrowser-Shield"),
            java.io.ByteArrayInputStream(ByteArray(0))
        )
    }
}
