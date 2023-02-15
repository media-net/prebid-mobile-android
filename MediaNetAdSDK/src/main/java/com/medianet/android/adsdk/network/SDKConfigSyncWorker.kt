package com.medianet.android.adsdk.network

import android.content.Context
import androidx.work.*
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.MediaNetAdSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker Class to sync SDK Config from server to datastore
 * as per config cache expiry
 */
class SDKConfigSyncWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    companion object {
        private const val WORKER_TAG = "CONFIG_SYNC_WORKER_TAG"
        private const val LOG_TAG = "ConfigSyncRefresh"

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /**
         * schedules config fetch from server as per expiry time which is in seconds
         * @param context specifies the context of application where MediaNetAdSdk has been integrated
         * @param expiry is the time in seconds after which config fetch from server will be scheduled
         */
        fun scheduleConfigFetch(context: Context, expiry: Long){
            val sdkConfigSyncWorker = OneTimeWorkRequestBuilder<SDKConfigSyncWorker>()
                .setConstraints(constraints)
                .setInitialDelay(expiry, TimeUnit.SECONDS)
                .addTag(WORKER_TAG)
                .build()

            CustomLogger.debug(LOG_TAG, "scheduling config refresh")
            WorkManager.getInstance(context).enqueueUniqueWork(
                "sdkConfigSyncWorker",
                ExistingWorkPolicy.REPLACE,
                sdkConfigSyncWorker
            )
        }

        /**
         * cancels all works scheduled by the worker
         * @param context specifies the context of application where MediaNetAdSdk has been integrated
         */
        fun cancelSDKConfigSync(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            CustomLogger.debug(LOG_TAG, "refreshing config by fetching it from server")
            MediaNetAdSDK.fetchConfigFromServer(applicationContext)
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }
}