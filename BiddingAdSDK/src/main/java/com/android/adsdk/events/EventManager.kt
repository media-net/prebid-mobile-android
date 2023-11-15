package com.android.adsdk.events

import com.android.adsdk.base.AdType
import com.android.adsdk.base.AdViewSize
import com.android.adsdk.base.Error
import com.android.adsdk.events.Constants.EventName.AD_LOADED
import com.android.adsdk.events.Constants.EventName.AD_REQUEST_TO_GAM
import com.android.adsdk.events.Constants.EventName.BID_REQUEST
import com.android.adsdk.events.Constants.EventName.GAM_ERROR
import com.android.adsdk.events.Constants.EventName.PREBID_ERROR
import com.android.adsdk.events.Constants.EventName.TIME_OUT
import com.android.adsdk.events.Constants.Keys.AD_TYPES
import com.android.adsdk.events.Constants.Keys.ERROR_CODE
import com.android.adsdk.events.Constants.Keys.ERROR_MSG
import com.android.adsdk.events.Constants.Keys.REQUEST_ID
import com.android.adsdk.events.Constants.Keys.REQ_MTYPE
import com.android.adsdk.events.Constants.Keys.RESPONSE_AD_SIZES
import com.android.adsdk.events.Constants.Keys.SNM
import com.android.adsdk.events.Constants.SNM_ERROR_VALUE
import com.android.adsdk.events.Constants.SNM_NO_BIDS_VALUE
import com.android.adsdk.events.Constants.SNM_SUCCESS_VALUE
import com.android.adsdk.model.sdkconfig.SdkConfiguration
import com.android.adsdk.utils.MapperUtils.getSizeString
import com.android.adsdk.utils.MapperUtils.mapAdExceptionToError
import com.android.adsdk.utils.MapperUtils.toEventParamValue
import com.app.analytics.AnalyticsSDK
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse

/**
 * manager class that handles events transmission to analytics sdk
 */
internal object EventManager {

    fun init(config: SdkConfiguration) {
        EventFactory.updateConfiguration(config)
    }

    /**
     * sends bid request event to analytics when bid request call is made
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
    fun sendBidRequestEvent(dfpDivId: String, sizes: List<AdViewSize>?) {
        sendEvent(
            eventName = BID_REQUEST,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes,
        )
    }

    /**
     * sends timeout event to analytics when bid request call times out
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
    fun sendTimeoutEvent(dfpDivId: String, sizes: List<AdViewSize>?) {
        sendEvent(
            eventName = TIME_OUT,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes,
        )
    }

    /**
     * sends event to analytics when adRequest is sent to GAM after the bid request
     * @param dfpDivId is the adUnit's configuration config id
     * @param sizes are the sizes set for the ad slot
     */
    fun sendAdRequestToGamEvent(dfpDivId: String, sizes: List<AdViewSize>?, adType: AdType, bidResponse: BidResponse?, exception: AdException?) {
        val type = adType.toEventParamValue().toString()
        val params = mutableMapOf(
            REQ_MTYPE to type,
            AD_TYPES to type,
        )

        bidResponse?.id?.let {
            params[REQUEST_ID] = it
        }
        bidResponse?.winningBid?.let {
            params[RESPONSE_AD_SIZES] = getSizeString(listOf(AdViewSize(width = it.width, height = it.height)))
        }

        params[SNM] = if (bidResponse != null) {
            SNM_SUCCESS_VALUE
        } else {
            if (exception.mapAdExceptionToError() == Error.NO_BIDS) {
                SNM_NO_BIDS_VALUE
            } else {
                SNM_ERROR_VALUE
            }
        }

        // Error event to track server errors
        if (exception != null) {
            sendErrorEvent(
                dfpDivId = dfpDivId,
                exception = exception,
            )
        }

        sendEvent(
            eventName = AD_REQUEST_TO_GAM,
            eventType = LoggingEvents.OPPORTUNITY,
            dfpDivId = dfpDivId,
            sizes = sizes,
            params = params,
        )
    }

    /**
     * sends event to analytics when ad is successfully loaded
     * @param dfpDivId is the adUnit's configuration config id
     * @param sizes are the sizes set for the ad slot
     */
    fun sendAdLoadedEvent(dfpDivId: String, sizes: List<AdViewSize>?) {
        sendEvent(
            eventName = AD_LOADED,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = sizes,
        )
    }

    /**
     * sends event to analytics when GAM error occurs during ad load
     * @param errorCode
     * @param errorMessage
     */
    fun sendGAMErrorEvent(dfpDivId: String, errorCode: Int, errorMessage: String) {
        val params = mutableMapOf(
            ERROR_MSG to errorMessage,
            ERROR_CODE to errorCode.toString(),
        )
        sendEvent(
            eventName = GAM_ERROR,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = null,
            params = params,
        )
    }

    /**
     * sends event to analytics when we receive error in bid request
     * @param errorMessage
     */
    fun sendErrorEvent(dfpDivId: String, exception: AdException?) {
        val params = mutableMapOf(
            ERROR_MSG to (exception?.message ?: ""),
        )
        sendEvent(
            eventName = PREBID_ERROR,
            eventType = LoggingEvents.PROJECT,
            dfpDivId = dfpDivId,
            sizes = null,
            params = params,
        )
    }

    /**
     * base method to get created event and send to analytics sdk
     * @param eventName is the unique name for the event
     * @param eventType is type of event like PROJECT(PE) or OPPORTUNITY(AP)
     * @param dfpDivId is the adUnit's configuration config ID
     * @param sizes are the sizes set for the ad slot
     */
    private fun sendEvent(eventName: String, eventType: LoggingEvents, dfpDivId: String, sizes: List<AdViewSize>?, params: MutableMap<String, String> = mutableMapOf()) {
        val event = EventFactory.getEvent(
            eventName = eventName,
            dfpDivId = dfpDivId,
            sizes = sizes,
            eventType = eventType,
            eventParams = params,
        )
        AnalyticsSDK.pushEvent(event)
    }

    fun clear() {
        EventFactory.clear()
    }
}
