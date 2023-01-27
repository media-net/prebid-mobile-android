package com.app.analytics.providers.cached.sync_strategy

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.app.analytics.AnalyticsSDK
import com.app.analytics.BuildConfig
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.cached.db.EventDBEntity
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository
import com.app.analytics.utils.AnalyticsUtil.toPixel
import com.app.analytics.utils.NetworkWatcher
import com.app.logger.CustomLogger
import com.app.network.Util
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImmediateSyncStrategy(
    var context: Context?,
    private val eventRepository: IAnalyticsEventRepository,
    pushService: PushEventToServerService)
    : EventSyncStrategy(pushService), NetworkWatcher.NetworkConnectionListener {

    companion object {
        private val TAG = ImmediateSyncStrategy::class.java.simpleName
        private const val SYNC_PAUSE_INTERVAL_MILLIS: Long = 15 * 60 * 1000L // 15 minutes.

    }

    private var topEventInDbLiveData: LiveData<EventDBEntity>? = null
    private var eventLifecycleOwner: EventsLifecycleOwner = EventsLifecycleOwner()
    private var isConnectedToInternet: Boolean = false
    private val observer = Observer<EventDBEntity> { dbEntry ->
        dbEntry?.let {
            syncEvent(it)
        }
    }

    override fun initialise() {
        CustomLogger.debug(TAG, "syncing strategy - $TAG")
        NetworkWatcher.startListening(this)
        topEventInDbLiveData = eventRepository.getFirstEvent()
        startListeningDbEvents()
    }

    private fun startListeningDbEvents() {
        eventLifecycleOwner.let { lifecycleOwner ->
            topEventInDbLiveData?.observe(lifecycleOwner, observer)

        }
        eventLifecycleOwner.startListening()
    }

    private fun syncEvent(dbEntry: EventDBEntity) {
        AnalyticsSDK.analyticsScope.launch {
            if (isConnectedToInternet.not()) {
                CustomLogger.debug(TAG, "Internet is not connected so skipping the sync for the event")
                return@launch
            }

            CustomLogger.debug(TAG, "syncing event - ${dbEntry.name}")
            val result = pushEventToServer(dbEntry.toPixel())
            if (result.isSuccess) {
                CustomLogger.debug(TAG, "event synced to server successfully so deleting entry from DB - ${dbEntry.name}")
                eventRepository.delete(dbEntry)
            } else  {
                val exception = result.errorValue()?.errorModel?.exception
                exception?.let {
                    if (Util.isClientSideHttpErrorError(it)) {
                        CustomLogger.error(TAG, "Client side error: ${it.message} while syncing, event: ${dbEntry.pixel}")
                        it.printStackTrace()
                        if (BuildConfig.DEBUG) throw it
                    } else {
                        pauseSyncing(SYNC_PAUSE_INTERVAL_MILLIS)
                    }
                }
            }
        }
    }

    override fun onNetworkChange(isConnected: Boolean) {
        isConnectedToInternet = isConnected
        if (isConnectedToInternet) {
            CustomLogger.debug(TAG, "internet available so start syncing")
            startListeningToLiveData()
        } else {
            CustomLogger.debug(TAG, "internet lost so stop syncing")
            stopListeningToLiveData()
        }
    }

    private suspend fun pauseSyncing(timeMillis: Long) {
        CustomLogger.debug(TAG, "pausing event syncing for $timeMillis milliseconds")
        stopListeningToLiveData()
        delay(timeMillis)
        startListeningToLiveData()
    }

    private fun startListeningToLiveData() {
        AnalyticsSDK.analyticsScope.launch {
            topEventInDbLiveData?.observe(eventLifecycleOwner, observer)
        }
    }

    private fun stopListeningToLiveData() {
        AnalyticsSDK.analyticsScope.launch {
            topEventInDbLiveData?.removeObserver(observer)
        }
    }

    override fun clean() {
        topEventInDbLiveData = null
        context = null
        NetworkWatcher.stopListening(this)
        eventLifecycleOwner.stopListening()
    }
}