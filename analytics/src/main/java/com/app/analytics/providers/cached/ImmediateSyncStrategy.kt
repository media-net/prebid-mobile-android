package com.app.analytics.providers.cached

import androidx.lifecycle.LiveData
import com.app.analytics.AnalyticsSDK
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.cached.db.EventDBEntity
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository
import com.app.analytics.utils.AnalyticsUtil.toPixel
import com.app.logger.CustomLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImmediateSyncStrategy(
    private val eventRepository: IAnalyticsEventRepository,
    pushService: PushEventToServerService)
    : EventSyncStrategy(pushService) {

    companion object {
        private val TAG = ImmediateSyncStrategy::class.java.simpleName
    }

    private var topEventInDbLiveData: LiveData<EventDBEntity>? = null
    private var eventLifecycleOwner: EventsLifecycleOwner = EventsLifecycleOwner()

    override fun initialise() {
        CustomLogger.debug(TAG, "syncing strategy - $TAG")
        topEventInDbLiveData = eventRepository.getFirstEvent()
        startListeningDbEvents()
    }

    private fun startListeningDbEvents() {
        eventLifecycleOwner.let { lifecycleOwner ->
            topEventInDbLiveData?.observe(lifecycleOwner) { dbEntry ->
                dbEntry?.let {
                    syncEvent(it)
                }
            }
        }
        eventLifecycleOwner.startListening()
    }

    private fun syncEvent(dbEntry: EventDBEntity) {
        CustomLogger.debug(TAG, "syncing event - ${dbEntry.name}")
        AnalyticsSDK.analyticsScope.launch {
            val result = pushEventToServer(dbEntry.toPixel())
            if (result.isSuccess) {
                CustomLogger.debug(TAG, "event synced to server successfully so deleting entry from DB - ${dbEntry.name}")
                eventRepository.delete(dbEntry)
            }
        }
    }

    override fun clean() {
        topEventInDbLiveData = null
    }
}