package com.medianet.android.adsdk

import android.util.Log
import androidx.annotation.IntRange
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.utils.Constants
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.AdUnit
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError
import org.prebid.mobile.api.rendering.listeners.MediaEventListener

abstract class Ad(val adUnit: AdUnit) {

    abstract val adType: AdType

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
            adLoaded()
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

    //We assume that publisher will fire this method when ad loaded on his side
    fun adLoaded() {
        EventManager.sendAdLoadedEvent(
            dfpDivId = adUnit.configuration.configId,
            sizes = Util.mapAdSizesToMAdSizes(adUnit.configuration.sizes)
        )
    }

    // TODO - need to expose this?
    fun fetchDemand(listener: OnBidCompletionListener) {
        adUnit.fetchDemand { resultCode, unmodifiableMap ->
            when(resultCode) {
                ResultCode.SUCCESS -> listener.onSuccess(unmodifiableMap)
                else -> Util.mapResultCodeToError(resultCode)
            }
        }
    }

    fun fetchDemand(adRequest: AdManagerAdRequest, listener: OnBidCompletionListener) {
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

    fun fetchDemand(
        adView: AdManagerAdView,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {
        adView.setAppEventListener { key, value ->
            if (key == Constants.KEY_AD_RENDERED) {
                // Mark our ad win
            }
            listener.onEvent(key, value)
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Update ad view
                AdViewUtils.findPrebidCreativeSize(adView, object : AdViewUtils.PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        adView.setAdSizes(AdSize(width, height))
                    }

                    override fun failure(error: PbFindSizeError) {
                        Log.e("Nikhil", "error in adjusting ad view")
                    }
                })
                adLoaded()
                listener.onAdLoaded()
            }

            override fun onAdClicked() {
                listener.onAdClicked()
            }

            override fun onAdClosed() {
                listener.onAdClosed()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                listener.onAdFailedToLoad(Util.mapGamLoadAdErrorToError(p0))
            }

            override fun onAdOpened() {
                listener.onAdOpened()
            }

            override fun onAdImpression() {
                listener.onAdImpression()
            }
        }

        fetchDemand(adRequest, object : OnBidCompletionListener{
            override fun onSuccess(keywordMap: Map<String, String>?) {
                listener.onSuccess()
                adView.loadAd(adRequest)
            }

            override fun onError(error: Error) {
                listener.onAdFailedToLoad(error)
            }
        })
    }
}