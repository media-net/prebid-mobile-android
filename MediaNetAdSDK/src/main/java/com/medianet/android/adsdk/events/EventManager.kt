package com.medianet.android.adsdk.events

import com.app.analytics.AnalyticsSDK

object EventManager {

    // Take all custom params from user here
    fun sendTimeoutEvent(browserId: String) {
        val event = EventFactory.getTimeoutEvent(browserId)
        event?.let {
            AnalyticsSDK.pushEvent(event)
        }
    }
}