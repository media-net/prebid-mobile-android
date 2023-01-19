package com.app.analytics.providers.debug

import com.app.analytics.Event
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel
import com.app.analytics.utils.Constant
import com.app.logger.CustomLogger

class DebugAnalyticsProvider : AnalyticsProvider {

    companion object {
        private val TAG = DebugAnalyticsProvider::class.java.simpleName
    }

    override val defaultParams = mutableMapOf<String, Any>()

    override fun getName() = Constant.Providers.PROVIDER_DEBUG

    override suspend fun pushEvent(event: Event): Boolean {
        CustomLogger.debug(TAG, "Sending event: ${event.name}")
        event.params.forEach { (key, value) ->
            CustomLogger.debug(TAG, "    param: $key --> $value")
        }
        return true
    }

    override suspend fun pushEvents(events: List<Event>): Boolean {
        events.forEach {
            pushEvent(it)
        }
        return true
    }

    override suspend fun pushPixel(pixel: DefaultAnalyticsPixel): Boolean {
        CustomLogger.debug(TAG, "Sending pixel: $pixel")
        return true
    }

    override suspend fun pushPixels(pixels: List<DefaultAnalyticsPixel>): Boolean {
        pixels.forEach {
            pushPixel(it)
        }
        return true
    }

    override fun setDefaultParams(params: Map<String, Any>) {
        defaultParams.putAll(params)
    }

    override fun clean() {}
}
