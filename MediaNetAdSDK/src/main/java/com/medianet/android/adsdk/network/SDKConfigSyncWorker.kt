package com.medianet.android.adsdk.network

import android.content.Context
import androidx.work.*
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.MediaNetAdSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SDKConfigSyncWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    companion object {
        private const val WORKER_TAG = "CONFIG_SYNC_WORKER_TAG"
        private const val LOG_TAG = "ConfigSyncRefresh"

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

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

        fun cancelSDKConfigSync(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            CustomLogger.debug(LOG_TAG, "refreshing config by fetching it from server")
            MediaNetAdSDK.initialiseSdkConfig(applicationContext)
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }
}