package com.app.analytics

import android.content.Context
import androidx.room.Room
import com.app.analytics.providers.cached.db.AnalyticsDatabase
import com.app.analytics.providers.cached.db.AnalyticsDbRepository
import com.app.analytics.providers.cached.db.AnalyticsEventDao
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository

object DbComponentFactory {

    private const val ANALYTICS_DB_NAME = "analytics_db"
    private lateinit var analyticsDB: AnalyticsDatabase

    private fun getAnalyticsDb(applicationContext: Context): AnalyticsDatabase {
        if (this::analyticsDB.isInitialized.not()) {
            analyticsDB = Room.databaseBuilder(
                applicationContext,
                AnalyticsDatabase::class.java, ANALYTICS_DB_NAME
            ).build()
        }
        return analyticsDB
    }

    private fun getEventDao(applicationContext: Context): AnalyticsEventDao {
        return getAnalyticsDb(applicationContext).analyticsEventDao()
    }

    fun getEventDbRepository(applicationContext: Context): IAnalyticsEventRepository {
        return AnalyticsDbRepository(getEventDao(applicationContext))
    }
}
