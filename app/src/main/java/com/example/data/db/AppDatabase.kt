package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookmarkEntity::class,
        SiteShieldExceptionEntity::class,
        PrivacyStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun siteShieldDao(): SiteShieldDao
    abstract fun privacyStatsDao(): PrivacyStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "privacy_browser.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initialize default stats row and initial privacy bookmarks
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.privacyStatsDao().insertOrUpdate(
                                PrivacyStatsEntity(
                                    id = 1,
                                    totalTrackersBlocked = 28,
                                    adsBlocked = 14,
                                    analyticsBlocked = 10,
                                    socialBlocked = 3,
                                    fingerprintingBlocked = 1,
                                    sessionsBurned = 0
                                )
                            )
                            database.bookmarkDao().insertBookmark(
                                BookmarkEntity(
                                    title = "DuckDuckGo",
                                    url = "https://duckduckgo.com"
                                )
                            )
                            database.bookmarkDao().insertBookmark(
                                BookmarkEntity(
                                    title = "Wikipedia",
                                    url = "https://ja.wikipedia.org"
                                )
                            )
                            database.bookmarkDao().insertBookmark(
                                BookmarkEntity(
                                    title = "Electronic Frontier Foundation (EFF)",
                                    url = "https://www.eff.org"
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
