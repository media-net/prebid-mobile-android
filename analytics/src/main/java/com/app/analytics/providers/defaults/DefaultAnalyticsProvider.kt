package com.app.analytics.providers.defaults

import com.app.analytics.Event
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.utils.AnalyticsUtil.getDefaultAnalyticsPixel
import com.app.analytics.utils.Constant
import com.app.logger.CustomLogger
import com.app.network.wrapper.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultAnalyticsProvider(
    private val analyticsBaseUrl: String,
    private val pushService: PushEventToServerService
) : AnalyticsProvider {

    override val defaultParams = mutableMapOf<String, Any>()

    override fun getName() = Constant.Providers.PROVIDER_DEFAULT

    override suspend fun pushEvent(event: Event): Boolean {
        CustomLogger.debug(TAG, "pushing event to default analytics: ${event.name}")
        if (analyticsBaseUrl.isBlank() && event.baseUrl.isBlank()) {
            CustomLogger.error(TAG, "Invalid host url: Empty base url to push event")
        }
        val url = event.baseUrl.ifBlank { analyticsBaseUrl }
        val pixel = getDefaultAnalyticsPixel(event, url)
        return pushEventToServer(pixel)
    }

    private suspend fun pushEventToServer(pixel: DefaultAnalyticsPixel) = withContext(Dispatchers.IO) {
        val result = safeApiCall(
            apiCall = {
                pushService.pushAnalyticsEvent(pixel.pixel)
                CustomLogger.debug(TAG, "pushing pixel to server: $pixel")
            },
            successTransform = {
                it
            }
        )
        return@withContext (result.isSuccess)
    }

    override suspend fun pushEvents(events: List<Event>): Boolean {
        var allEventsPushed = true
        events.forEach {
            allEventsPushed = allEventsPushed && pushEvent(it)
        }
        return allEventsPushed
    }

    override suspend fun pushPixel(pixel: DefaultAnalyticsPixel): Boolean {
        return pushEventToServer(pixel)
    }

    override suspend fun pushPixels(pixels: List<DefaultAnalyticsPixel>): Boolean {
        var allEventsPushed = true
        pixels.forEach {
            allEventsPushed = allEventsPushed && pushPixel(it)
        }
        return allEventsPushed
    }

    override fun setDefaultParams(params: Map<String, Any>) {
        defaultParams.putAll(params)
    }

    override fun clean() {}

    companion object {
        private val TAG = DefaultAnalyticsProvider::class.java.simpleName
    }
}
