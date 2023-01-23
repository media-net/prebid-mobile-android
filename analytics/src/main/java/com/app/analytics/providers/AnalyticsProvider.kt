package com.app.analytics.providers

import com.app.analytics.events.Event
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel

interface AnalyticsProvider {
    val defaultParams: MutableMap<String, Any>
    fun getName(): String
    suspend fun pushEvent(event: Event): Boolean
    suspend fun pushEvents(events: List<Event>): Boolean
    suspend fun pushPixel(pixel: DefaultAnalyticsPixel): Boolean
    suspend fun pushPixels(pixels: List<DefaultAnalyticsPixel>): Boolean
    fun setDefaultParams(params: Map<String, Any>)
    fun clean()
}