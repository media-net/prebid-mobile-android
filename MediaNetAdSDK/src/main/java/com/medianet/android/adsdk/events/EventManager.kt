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

/**
 * manager class that handles events transmission to analytics sdk
 */
object EventManager {

    fun init(config: SdkConfiguration) {
        EventFactory.updateConfiguration(config)
    }

    /**
     * sends bid request event to analytics when bid request call is made
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
    fun sendBidRequestEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = BID_REQUEST,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    /**
     * sends timeout event to analytics when bid request call times out
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
    fun sendTimeoutEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = TIME_OUT,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    /**
     * sends event to analytics when adRequest is sent to GAM after the bid request
     * @param dfpDivId is the adUnit's configuration config id
     * @param sizes are the sizes set for the ad slot
     */
    fun sendAdRequestToGamEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = AD_REQUEST_TO_GAM,
            eventType = LoggingEvents.OPPORTUNITY,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    /**
     * sends event to analytics when ad is successfully loaded
     * @param dfpDivId is the adUnit's configuration config id
     * @param sizes are the sizes set for the ad slot
     */
    fun sendAdLoadedEvent(dfpDivId: String, sizes: List<MAdSize>?) {
        sendEvent(
            eventName = AD_LOADED,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes
        )
    }

    /**
     * base method to get created event and send to analytics sdk
     * @param eventName is the unique name for the event
     * @param eventType is type of event like PROJECT(PE) or OPPORTUNITY(AP)
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
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