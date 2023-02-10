package com.medianet.android.adsdk.events

import com.app.analytics.AnalyticsSDK
import com.medianet.android.adsdk.LoggingEvents
import com.medianet.android.adsdk.MAdSize
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.events.Constants.EventName.AD_LOADED
import com.medianet.android.adsdk.events.Constants.EventName.AD_REQUEST_TO_GAM
import com.medianet.android.adsdk.events.Constants.EventName.BID_REQUEST
import com.medianet.android.adsdk.events.Constants.EventName.TIME_OUT
import com.medianet.android.adsdk.model.SdkConfiguration

object EventManager {

    fun init(config: SdkConfiguration) {
        EventFactory.updateConfiguration(config)
    }

    fun sendBidRequestEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = BID_REQUEST,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    fun sendTimeoutEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = TIME_OUT,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    fun sendAdRequestToGamEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = AD_REQUEST_TO_GAM,
            eventType = LoggingEvents.OPPORTUNITY,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    fun sendAdLoadedEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = AD_LOADED,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    private fun sendEvent(eventName: String, eventType: LoggingEvents, dfpDivId: String, sizes: List<MAdSize>?) {
        val event = EventFactory.getEvent(
            eventName = eventName,
            dfpDivId = dfpDivId,
            sizes = sizes,
            eventType = eventType
        )
        AnalyticsSDK.pushEvent(event)
    }

    fun clear() {
        EventFactory.clear()
    }
}
