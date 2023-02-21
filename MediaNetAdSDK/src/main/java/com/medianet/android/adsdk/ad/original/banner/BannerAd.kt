package com.medianet.android.adsdk.ad.original.banner

import com.app.logger.CustomLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.*
import com.medianet.android.adsdk.base.Ad
import com.medianet.android.adsdk.base.AdType
import com.medianet.android.adsdk.base.listeners.GamEventListener
import com.medianet.android.adsdk.base.listeners.OnBidCompletionListener
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.MapperUtils.mapGamLoadAdErrorToError
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError
import com.medianet.android.adsdk.base.Error


/**
 * banner ad class for original type of loading where bid request call is made
 * and modified adRequest is returned to the user
 */
class BannerAd(adUnitId: String, val adSize: AdSize = AdSize.BANNER) :
    Ad(BannerAdUnit(adUnitId, adSize.width, adSize.height)) {

    constructor(adUnitId: String, width: Int, height: Int) : this(adUnitId, AdSize(width, height))

    private val bannerAdUnit: BannerAdUnit = adUnit as BannerAdUnit
    override val adType: AdType = AdType.BANNER


    /**
     * allows us to add multiple sizes to the ad
     * @param size specifies the size for ad slot through AdSize object
     */
    fun addAdditionalSize(size: AdSize) = apply {
        bannerAdUnit.addAdditionalSize(size.width, size.height)
    }

    /**
     * allows us to add multiple sizes to the ad
     * @param width specifies the width for ad slot
     * @param height specifies the height for ad slot
     */
    fun addAdditionalSize(width: Int, height: Int) = apply {
        bannerAdUnit.addAdditionalSize(width, height)
    }

    /**
     * starts the bid request call and loads the ad into the adView
     * @param adView is the view where ad loads
     * @param adRequest is the ad request for ad manager
     * @param listener listens to GAM events
     */
    fun fetchDemandAndLoad(
        adView: AdManagerAdView,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {
        if (MediaNetAdSDK.isSdkOnVacation()) {
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

            override fun onAdFailedToLoad(error: LoadAdError) {
                listener.onAdFailedToLoad(error.mapGamLoadAdErrorToError())
            }

            override fun onAdOpened() {
                listener.onAdOpened()
            }

            override fun onAdImpression() {
                listener.onAdImpression()
            }
        }

        fetchDemand(adRequest, object : OnBidCompletionListener {
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