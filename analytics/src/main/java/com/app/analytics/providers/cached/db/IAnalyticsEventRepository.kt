package com.app.analytics.providers.cached.db

import androidx.lifecycle.LiveData
import com.app.analytics.providers.cached.db.EventDBEntity

interface IAnalyticsEventRepository {
    fun getFirstEvent(): LiveData<EventDBEntity>
    suspend fun getAll(): List<EventDBEntity>?
    suspend fun insert(event: EventDBEntity)
    suspend fun insert(events: List<EventDBEntity>)
    suspend fun delete(event: EventDBEntity)
    suspend fun deleteUsingPixel(events: List<String>)
    suspend fun delete(events: List<EventDBEntity>)
    suspend fun getCount(): Long

    //TODO - decide return of each operation
}
