package com.medianet.android.adsdk

import android.util.Log
import androidx.annotation.IntRange
import com.app.analytics.AnalyticsSDK
import com.app.analytics.events.Event
import com.app.logger.CustomLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.utils.Constants
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.AdUnit
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError

abstract class Ad {

    abstract val adUnit: AdUnit
    abstract val adType: AdType
    companion object{
        private const val ADSIZE_ADJUSTMENT_ERROR_TAG = "AdSizeAdjustmentError"
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
                    ResultCode.SUCCESS -> listener.onSuccess(null)
                    else -> Util.mapResultCodeToError(resultCode)
                }
        }
    }

    fun fetchDemand(
        adView: AdManagerAdView,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {

        AnalyticsSDK.pushEvent(Event(name = "fetching_prebid", type = LoggingEvents.PROJECT.type))
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
                        CustomLogger.error(ADSIZE_ADJUSTMENT_ERROR_TAG, "error in adjusting ad view")
                    }
                })
                AnalyticsSDK.pushEvent(Event(name = "ad_loaded", type = LoggingEvents.OPPORTUNITY.type))
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

            /*override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
            }*/
        }

        adUnit.fetchDemand(adRequest) { code ->
            when(code) {
                ResultCode.SUCCESS -> {
                    listener.onSuccess()
                    AnalyticsSDK.pushEvent(Event(name = "prebid_auction_success", type = LoggingEvents.PROJECT.type))
                    adView.loadAd(adRequest)
                }

                else ->  {
                    Util.mapResultCodeToError(code)
                    AnalyticsSDK.pushEvent(Event(name = "prebid_auction_failure", type = LoggingEvents.PROJECT.type))
                }
            }
        }
    }
}