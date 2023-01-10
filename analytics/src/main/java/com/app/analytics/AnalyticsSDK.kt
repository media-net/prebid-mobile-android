package com.app.analytics

import com.app.analytics.events.Event
import com.app.analytics.providers.AnalyticsProvider

object AnalyticsSDK {

    private val TAG = AnalyticsSDK::class.java.name
    private val analyticsProviders: MutableList<AnalyticsProvider> = mutableListOf()
    private var isInitialised = false
    private val pendingEvents = mutableListOf<Event>()

    suspend fun init(providers: List<AnalyticsProvider>) {
        if (isInitialised.not()) {
            analyticsProviders.addAll(providers)
            isInitialised = true
            sendPendingEventIfAny()
            //CustomLogger.debug(TAG, "Analytics SDK initialised")
        }
    }

    private suspend fun sendPendingEventIfAny() {
        if (pendingEvents.isNotEmpty()) {
            //CustomLogger.debug(TAG, "Analytics SDK initialised so pushing events of pending queue")
            pushEvents()
            pendingEvents.clear()
        }
    }

    suspend fun pushEvent(event: Event): Boolean {
        return if (isInitialised) {
            analyticsProviders.forEach { provider ->
                provider.pushEvent(event)
            }
            true
        } else {
            //CustomLogger.debug(TAG, "Analytics SDK not initialised yet so saving event in Queue: ${event.name}")
            pendingEvents.add(event)
            true
        }
    }

    private suspend fun pushEvents(): Boolean {
        return if (isInitialised) {
            analyticsProviders.forEach { provider ->
                provider.pushEvents(pendingEvents)
            }
            true
        } else false
    }
}