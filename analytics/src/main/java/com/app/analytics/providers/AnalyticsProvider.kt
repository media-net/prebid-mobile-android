package com.app.analytics.providers

import com.app.analytics.events.Event

interface AnalyticsProvider {
    val defaultParams: MutableMap<String, Any>
    fun getName(): String
    suspend fun pushEvent(event: Event): Boolean
    suspend fun pushEvents(events: List<Event>): Boolean
    fun setDefaultParams(params: Map<String, Any>)
}