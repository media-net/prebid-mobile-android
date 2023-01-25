package com.medianet.android.adsdk

import androidx.annotation.IntRange
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.AdUnit
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode
import org.prebid.mobile.api.rendering.listeners.MediaEventListener

abstract class Ad(val adUnit: AdUnit) {

    abstract val adType: AdType
    companion object{
        private const val ADSIZE_ADJUSTMENT_ERROR_TAG = "AdSizeAdjustmentError"
    }

    private val mediaEventListener = object : MediaEventListener {
        override fun onBidRequest() {
            EventManager.sendBidRequestEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = Util.mapAdSizesToMAdSizes(adUnit.configuration.sizes)
            )
        }

        override fun onBidRequestTimeout() {
            EventManager.sendTimeoutEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = Util.mapAdSizesToMAdSizes(adUnit.configuration.sizes)
            )
        }

        override fun onRequestSentToGam() {
            EventManager.sendAdRequestToGamEvent(
                dfpDivId = adUnit.configuration.configId,
                sizes = Util.mapAdSizesToMAdSizes(adUnit.configuration.sizes)
            )
        }

        override fun onAdLoaded() {
            sendAdLoadedEvent()
        }
    }

    init {
        adUnit.setMediaEventListener(mediaEventListener)
    }

    fun getConfigId() = adUnit.configuration.configId

    fun setAutoRefreshIntervalInSeconds(
        @IntRange(
            from = (PrebidMobile.AUTO_REFRESH_DELAY_MIN / 1000).toLong(),
            to = (PrebidMobile.AUTO_REFRESH_DELAY_MAX / 1000).toLong()
        ) seconds: Int) = apply {
        adUnit.setAutoRefreshInterval(seconds)
    }

    fun stopAutoRefresh() = adUnit.stopAutoRefresh()
    fun resumeAutoRefresh() = adUnit.resumeAutoRefresh()

    fun addContextData(key: String, values: Set<String>) = apply {
        adUnit.updateContextData(key, values)
    }
    fun removeContextData(key: String) = apply { adUnit.removeContextData(key) }
    fun clearContextData() = apply { adUnit.clearContextData() }

    //TODO - prebid does not expose it, should we expose it?
    fun getContextData() = adUnit.configuration.contextDataDictionary

    fun getPrebidAdSlot() = adUnit.pbAdSlot
    fun setPrebidAdSlot(slot: String) = apply { adUnit.pbAdSlot =  slot }

    fun sendAdLoadedEvent() {
        EventManager.sendAdLoadedEvent(
            dfpDivId = adUnit.configuration.configId,
            sizes = Util.mapAdSizesToMAdSizes(adUnit.configuration.sizes)
        )
    }

    protected fun fetchDemand(adRequest: AdManagerAdRequest, listener: OnBidCompletionListener) {
        adUnit.fetchDemand(adRequest) {
        resultCode ->
                when(resultCode) {
                    ResultCode.SUCCESS -> {
                        listener.onSuccess(null)
                    }
                    else -> {
                        val error =  Util.mapResultCodeToError(resultCode)
                        listener.onError(error)
                    }
                }
        }
    }
}