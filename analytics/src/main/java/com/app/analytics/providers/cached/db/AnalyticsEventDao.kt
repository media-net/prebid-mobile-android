package com.app.analytics.providers.cached.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnalyticsEventDao {

    @Query("SELECT * FROM EventDBEntity LIMIT 1")
    fun getTopEvent(): LiveData<EventDBEntity>

    @Insert(entity = EventDBEntity::class)
    suspend fun insertEvent(event: EventDBEntity)

    @Insert(entity = EventDBEntity::class)
    suspend fun insertEvents(event: List<EventDBEntity>)

    @Query("SELECT * FROM EventDBEntity")
    suspend fun getAllEvents(): List<EventDBEntity>

    @Query("DELETE FROM EventDBEntity WHERE pixel IN (:eventsPixels)")
    suspend fun deleteEventsUsingPixel(eventsPixels: List<String>)

    @Delete(entity = EventDBEntity::class)
    suspend fun deleteEvent(event: EventDBEntity)

    @Delete(entity = EventDBEntity::class)
    suspend fun deleteEvents(events: List<EventDBEntity>)

    @Query("SELECT COUNT(*) FROM EventDBEntity")
    suspend fun getEventCount(): Long

    // TODO - decide return of each operation
}
