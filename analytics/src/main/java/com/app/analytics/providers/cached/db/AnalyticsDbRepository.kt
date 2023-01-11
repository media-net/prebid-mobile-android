package com.app.analytics.providers.cached.db

import androidx.lifecycle.LiveData

class AnalyticsDbRepository(private val  eventDao: AnalyticsEventDao): IAnalyticsEventRepository {
    override fun getFirstEvent(): LiveData<EventDBEntity> {
        return eventDao.getTopEvent()
    }

    override suspend fun getAll(): List<EventDBEntity> {
       return eventDao.getAllEvents()
    }

    override suspend fun insert(event: EventDBEntity) {
        return eventDao.insertEvent(event)
    }

    override suspend fun insert(events: List<EventDBEntity>) {
        return eventDao.insertEvents(events)
    }

    override suspend fun delete(event: EventDBEntity) {
       return eventDao.deleteEvent(event)
    }

    override suspend fun delete(events: List<EventDBEntity>) {
        return eventDao.deleteEvents(events)
    }

    override suspend fun deleteUsingPixel(events: List<String>) {
        return eventDao.deleteEventsUsingPixel(events)
    }

    override suspend fun getCount(): Long {
        return eventDao.getEventCount()
    }
}