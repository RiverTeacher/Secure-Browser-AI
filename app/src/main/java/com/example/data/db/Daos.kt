package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url LIMIT 1)")
    fun isBookmarked(url: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAllBookmarks()
}

@Dao
interface SiteShieldDao {
    @Query("SELECT * FROM shield_exceptions")
    fun getAllExceptions(): Flow<List<SiteShieldExceptionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM shield_exceptions WHERE host = :host AND shieldsDisabled = 1 LIMIT 1)")
    suspend fun isShieldDisabledForHost(host: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setShieldDisabled(exception: SiteShieldExceptionEntity)

    @Query("DELETE FROM shield_exceptions WHERE host = :host")
    suspend fun removeException(host: String)
}

@Dao
interface PrivacyStatsDao {
    @Query("SELECT * FROM privacy_stats WHERE id = 1 LIMIT 1")
    fun getPrivacyStats(): Flow<PrivacyStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: PrivacyStatsEntity)

    @Query("UPDATE privacy_stats SET totalTrackersBlocked = totalTrackersBlocked + 1, " +
            "adsBlocked = adsBlocked + :isAd, " +
            "analyticsBlocked = analyticsBlocked + :isAnalytics, " +
            "socialBlocked = socialBlocked + :isSocial, " +
            "fingerprintingBlocked = fingerprintingBlocked + :isFingerprinting " +
            "WHERE id = 1")
    suspend fun incrementTrackers(
        isAd: Int,
        isAnalytics: Int,
        isSocial: Int,
        isFingerprinting: Int
    )

    @Query("UPDATE privacy_stats SET sessionsBurned = sessionsBurned + 1 WHERE id = 1")
    suspend fun incrementBurnedSessions()
}
