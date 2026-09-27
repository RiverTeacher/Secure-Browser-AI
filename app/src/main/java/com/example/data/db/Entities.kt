package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shield_exceptions")
data class SiteShieldExceptionEntity(
    @PrimaryKey val host: String,
    val shieldsDisabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "privacy_stats")
data class PrivacyStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalTrackersBlocked: Long = 0,
    val adsBlocked: Long = 0,
    val analyticsBlocked: Long = 0,
    val socialBlocked: Long = 0,
    val fingerprintingBlocked: Long = 0,
    val sessionsBurned: Long = 0
)
