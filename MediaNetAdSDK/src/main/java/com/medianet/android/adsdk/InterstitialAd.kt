package com.medianet.android.adsdk

import android.content.Context
import com.app.logger.CustomLogger
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.AdSize
import org.prebid.mobile.InterstitialAdUnit

/**
 * Interstitial Ad Class for original type
 */
class InterstitialAd(val adUnitId: String): Ad(InterstitialAdUnit("imp-prebid-display-interstitial-320-480")) {
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private var mInterstitialAdUnit: InterstitialAdUnit = adUnit as InterstitialAdUnit

    constructor(adUnitId: String, minWidthPerc: Int, minHeightPerc: Int) : this(adUnitId) {
        mInterstitialAdUnit.configuration.minSizePercentage = AdSize(minWidthPerc, minHeightPerc)
    }

    override val adType: AdType = AdType.INTERSTITIAL

    /**
     * starts the bid auction call
     * @param context specifies context of view on which ad loads
     * @param adRequest is the ad request for ad manager
     * @param listener listens to GAM events
     */
    fun fetchDemandAndLoad(
        context: Context,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {

        if(MediaNetAdSDK.isSdkOnVacation()){
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            return
        }
        fetchDemand(adRequest, object : OnBidCompletionListener{
            override fun onSuccess(keywordMap: Map<String, String>?) {
                listener.onSuccess()
                loadAd(context ,adUnitId, adRequest, listener)
            }

            override fun onError(error: Error) {
                listener.onAdFailedToLoad(error)
            }
        })
    }

    /**
     * this method loads the ad and provides the interstitial ad object for further use
     * @param context specifies context of view on which ad loads
     * @param adUnitId specifies Id of the adUnit containers where we show ads
     * @param adRequest is the ad request for ad manager
     * @param adLoadCallback listens to GAM events
     */
    private fun loadAd(context: Context, adUnitId: String, adRequest: AdManagerAdRequest, adLoadCallback: GamEventListener) {
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
                    adLoadCallback.onAdFailedToLoad(Util.mapGamLoadAdErrorToError(error))
                }
            })
    }
}