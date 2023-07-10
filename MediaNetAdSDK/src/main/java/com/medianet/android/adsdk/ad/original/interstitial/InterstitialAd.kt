package com.medianet.android.adsdk.ad.original.interstitial

import android.content.Context
import com.app.logger.CustomLogger
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.medianet.android.adsdk.*
import com.medianet.android.adsdk.base.Ad
import com.medianet.android.adsdk.base.AdType
import com.medianet.android.adsdk.base.listeners.GamEventListener
import com.medianet.android.adsdk.base.listeners.OnBidCompletionListener
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.MapperUtils.mapGamLoadAdErrorToError
import org.prebid.mobile.AdSize
import org.prebid.mobile.InterstitialAdUnit
import com.medianet.android.adsdk.base.Error

/**
 * interstitial ad class for original type
 */
class InterstitialAd(val adUnitId: String) : Ad(InterstitialAdUnit(adUnitId)) {

    private var mInterstitialAdUnit: InterstitialAdUnit = adUnit as InterstitialAdUnit

    constructor(adUnitId: String, minWidthPerc: Int, minHeightPerc: Int) : this(adUnitId) {
        mInterstitialAdUnit.configuration.minSizePercentage = AdSize(minWidthPerc, minHeightPerc)
    }

    override val adType: AdType = AdType.INTERSTITIAL

    /**
     * starts the bid request call
     * @param adRequest is the ad request for ad manager
     * @param listener listens to request call events
     */
    fun fetchDemandForAd(
        adRequest: AdManagerAdRequest,
        listener: OnBidCompletionListener
    ) {

        if (MediaNetAdSDK.isSdkOnVacation() || MediaNetAdSDK.isConfigEmpty()) {
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            listener.onError(Error.CONFIG_ERROR)
            return
        }
        fetchDemand(adRequest, listener)
    }


    /**
     * starts the bid request call and loads the
     * @param context specifies context of view on which ad loads
     * @param adRequest is the ad request for ad manager
     * @param listener listens to GAM events
     */
    private fun fetchDemandAndLoad(
        context: Context,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {

        if (MediaNetAdSDK.isSdkOnVacation() || MediaNetAdSDK.isConfigEmpty()) {
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            listener.onError(Error.CONFIG_ERROR)
            return
        }
        fetchDemand(adRequest, object : OnBidCompletionListener {
            override fun onSuccess(keywordMap: Map<String, String>?) {
                listener.onSuccess(keywordMap)
                loadAd(context, adUnitId, adRequest, listener)
            }

            override fun onError(error: Error) {
                listener.onError(error)
            }
        })
    }

    /**
     * loads the ad and provides the interstitial ad object for further use
     * @param context specifies context of view on which ad loads
     * @param adUnitId specifies Id of the adUnit containers where we show ads
     * @param adRequest is the ad request for ad manager
     * @param adLoadCallback listens to GAM events
     */
    private fun loadAd(
        context: Context,
        adUnitId: String,
        adRequest: AdManagerAdRequest,
        adLoadCallback: GamEventListener
    ) {
        AdManagerInterstitialAd.load(
            context,
            adUnitId,
            adRequest, object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(adManagerInterstitialAd: AdManagerInterstitialAd) {
                    super.onAdLoaded(adManagerInterstitialAd)
                    sendAdLoadedEvent()
                    adLoadCallback.onInterstitialAdLoaded(adManagerInterstitialAd)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    adLoadCallback.onAdFailedToLoad(error.mapGamLoadAdErrorToError())
                }
            })
    }
}