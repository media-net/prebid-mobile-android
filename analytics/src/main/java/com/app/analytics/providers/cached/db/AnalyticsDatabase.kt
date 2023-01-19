package com.app.analytics.providers.cached.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EventDBEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AnalyticsDatabase : RoomDatabase() {
    abstract fun analyticsEventDao(): AnalyticsEventDao
}
