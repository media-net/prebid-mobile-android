package com.medianet.android.adsdk

import com.app.logger.CustomLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.Util
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError


class BannerAd(adUnitId: String, val adSize: AdSize = AdSize.BANNER): Ad(BannerAdUnit(adUnitId, adSize.width, adSize.height)) {

    constructor(adUnitId: String, width: Int, height: Int) : this(adUnitId, AdSize(width, height))

    private val bannerAdUnit: BannerAdUnit = adUnit as BannerAdUnit
    override val adType: AdType = AdType.BANNER


    fun addAdditionalSize(size: AdSize) = apply {
        bannerAdUnit.addAdditionalSize(size.width, size.height)
    }

    fun addAdditionalSize(width: Int, height: Int) = apply {
        bannerAdUnit.addAdditionalSize(width, height)
    }

    fun fetchDemandAndLoad(
        adView: AdManagerAdView,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {
        if(MediaNetAdSDK.isSdkOnVacation()){
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            return
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Update ad view
                AdViewUtils.findPrebidCreativeSize(adView, object : AdViewUtils.PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        adView.setAdSizes(AdSize(width, height))
                    }

                    override fun failure(error: PbFindSizeError) {
                    }
                })
                sendAdLoadedEvent()
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