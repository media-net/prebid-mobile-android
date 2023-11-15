package com.android.adsdk.base

import androidx.annotation.IntRange
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.android.adsdk.base.listeners.OnBidCompletionListener
import com.android.adsdk.events.EventManager
import com.android.adsdk.utils.MapperUtils.mapAdSizesToMAdSizes
import com.android.adsdk.utils.MapperUtils.mapResultCodeToError
import org.prebid.mobile.AdUnit
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.listeners.LoggingEventListener
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse

/**
 * base ad class for banner, interstitial and native ads
 */
abstract class Ad(val adUnit: AdUnit) {

    abstract val adType: com.android.adsdk.base.AdType
    companion object {
        private const val ADSIZE_ADJUSTMENT_ERROR_TAG = "AdSizeAdjustmentError"
    }

    private val loggingEventListener = object :
        LoggingEventListener {
        override fun onBidRequest() {
            EventManager.sendBidRequestEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = adUnit.configuration.sizes.mapAdSizesToMAdSizes(),
            )
        }

        override fun onBidRequestTimeout() {
            EventManager.sendTimeoutEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = adUnit.configuration.sizes.mapAdSizesToMAdSizes(),
            )
        }

        override fun onRequestSentToGam(bidResponse: BidResponse?, exception: AdException?) {
            EventManager.sendAdRequestToGamEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = adUnit.configuration.sizes.mapAdSizesToMAdSizes(),
                adType = adType,
                bidResponse = bidResponse,
                exception = exception,
            )
        }

        override fun onAdLoaded() {
            sendAdLoadedEvent()
        }
    }

    init {
        adUnit.setMediaEventListener(loggingEventListener)
    }

    fun getConfigId() = adUnit.configuration.configId

    /**
     * sets refresh interval for ad
     * @param seconds is the interval time in seconds to be passed and has a range from 30 to 120 seconds
     */
    fun setAutoRefreshIntervalInSeconds(
        @IntRange(
            from = (PrebidMobile.AUTO_REFRESH_DELAY_MIN / 1000).toLong(),
            to = (PrebidMobile.AUTO_REFRESH_DELAY_MAX / 1000).toLong(),
        ) seconds: Int,
    ) = apply {
        adUnit.setAutoRefreshInterval(seconds)
    }

    /**
     * cancels auto refresh of ad
     */
    fun stopAutoRefresh() = adUnit.stopAutoRefresh()

    /**
     * resumes auto refresh of ad
     */
    fun resumeAutoRefresh() = adUnit.resumeAutoRefresh()

    /**
     * obtains the context data keyword & value for adunit context targeting
     * if the key already exists the value will be appended to the list. No duplicates will be added
     * @param key is for the key of dictionary(hashmap)
     * @param values is the set of strings for the particular key
     */
    fun addContextData(key: String, values: Set<String>) = apply {
        adUnit.updateContextData(key, values)
    }

    /**
     * allows to remove specific context data keyword & values set from adunit context targeting
     * @param key is the key of dictionary(hashmap)
     */
    fun removeContextData(key: String) = apply { adUnit.removeContextData(key) }

    /**
     * allows to remove all context data set from adunit context targeting
     */
    fun clearContextData() = apply { adUnit.clearContextData() }

    fun getPrebidAdSlot() = adUnit.pbAdSlot
    fun setPrebidAdSlot(slot: String) = apply { adUnit.pbAdSlot = slot }

    /**
     * sends Event Of Ad Loaded to Analytics
     */
    fun sendAdLoadedEvent() {
        EventManager.sendAdLoadedEvent(
            dfpDivId = adUnit.configuration.configId,
            sizes = adUnit.configuration.sizes.mapAdSizesToMAdSizes(),
        )
    }

    /**
     * initiates the bid request call
     * @param adRequest is the ad request for ad manager
     * @param listener listens to bid request call result
     */
    protected fun fetchDemand(adRequest: AdManagerAdRequest, listener: OnBidCompletionListener) {
        adUnit.fetchDemand(adRequest) {
                resultCode ->
            when (resultCode) {
                ResultCode.SUCCESS -> {
                    listener.onSuccess(null)
                }
                else -> {
                    val error = resultCode.mapResultCodeToError()
                    listener.onError(error)
                }
            }
        }
    }

    /**
     * Method to log GAM error on publisher side, to track dropout due to GAM errors
     * This is a temporary method might not go in release
     */
    fun sendGAMErrorEvent(errorCode: Int, errorMessage: String) {
        EventManager.sendGAMErrorEvent(
            dfpDivId = adUnit.configuration.configId,
            errorMessage = errorMessage,
            errorCode = errorCode,
        )
    }
}
