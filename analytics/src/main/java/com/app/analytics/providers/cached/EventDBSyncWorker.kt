package com.app.analytics.providers.cached

import android.content.Context
import androidx.work.*
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.cached.db.EventDBEntity
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository
import com.app.logger.CustomLogger
import com.app.network.wrapper.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class EventDBSyncWorker (
    context: Context,
    params: WorkerParameters,
    private val repository: IAnalyticsEventRepository,
    private val syncService: PushEventToServerService
) : CoroutineWorker(context, params) {

    companion object {
        private const val WORKER_TAG = "ANALYTICS_SYNC_WORK"
        private const val INITIAL_DELAY_IN_MIN = 5L
        private val TAG = EventDBSyncWorker::class.java.name

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun scheduleAnalyticsSync(context: Context, syncIntervalInMinutes: Long = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
            var interval = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
            CustomLogger.debug(TAG, "scheduling worker for event sync")
            if (syncIntervalInMinutes < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
                CustomLogger.debug(TAG, "given sync interval is too small, minimal sync interval should be 15 min")
            } else  {
                interval = syncIntervalInMinutes
            }

            val request = PeriodicWorkRequestBuilder<EventDBSyncWorker>(
                repeatInterval = interval,
                TimeUnit.MILLISECONDS
            )
                .setConstraints(constraints)
                .setInitialDelay(INITIAL_DELAY_IN_MIN, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                .addTag(WORKER_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancelAnalyticsSync(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {

        val events = repository.getAll()
        val eventSyncedSuccessFully = mutableListOf<EventDBEntity>()

        // Sync events with network
        events?.forEach { event ->
            CustomLogger.debug(TAG, "sending event to server ${event.name}")
            safeApiCall(
                apiCall = { syncService.pushAnalyticsEvent(event.pixel) },
                successTransform = {
                    eventSyncedSuccessFully.add(event)
                }
            )
        }

        // delete synced events from DB
        repository.delete(eventSyncedSuccessFully)

        Result.success()
    }
}
