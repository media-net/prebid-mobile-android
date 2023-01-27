package com.medianet.android.adsdk

import android.content.Context
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.AdSize
import org.prebid.mobile.InterstitialAdUnit

class InterstitialAd(val adUnitId: String): Ad(InterstitialAdUnit("imp-prebid-display-interstitial-320-480")) {
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private var mInterstitialAdUnit: InterstitialAdUnit = adUnit as InterstitialAdUnit

    constructor(adUnitId: String, minWidthPerc: Int, minHeightPerc: Int) : this(adUnitId) {
        mInterstitialAdUnit.configuration.minSizePercentage = AdSize(minWidthPerc, minHeightPerc)
    }

    override val adType: AdType = AdType.INTERSTITIAL

    fun fetchDemandAndLoad(
        context: Context,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {

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