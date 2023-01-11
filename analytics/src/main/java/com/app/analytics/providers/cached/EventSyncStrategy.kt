package com.app.analytics.providers.cached

import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel
import com.app.logger.CustomLogger
import com.app.network.wrapper.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

abstract class EventSyncStrategy(private val pushService: PushEventToServerService) {

    companion object {
        private val TAG = EventSyncStrategy::class.java.simpleName
    }

    abstract fun initialise()
    abstract fun clean()

    suspend fun pushEventToServer(pixel: DefaultAnalyticsPixel) = withContext(Dispatchers.IO) {
        CustomLogger.debug(TAG, "pushing event to server - $pixel")
        val result = safeApiCall(
            apiCall = {
                //pushService.pushAnalyticsEvent(pixel.pixel)
                delay(200)
            },
            successTransform = {}
        )
        return@withContext result
    }


}