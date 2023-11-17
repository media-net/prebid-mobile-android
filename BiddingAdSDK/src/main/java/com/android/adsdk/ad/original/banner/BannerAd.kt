package com.android.adsdk.ad.original.banner

import com.android.adsdk.AdSDKManager
import com.android.adsdk.base.Ad
import com.android.adsdk.base.AdSignal
import com.android.adsdk.base.AdType
import com.android.adsdk.base.Error
import com.android.adsdk.base.listeners.GamEventListener
import com.android.adsdk.base.listeners.OnBidCompletionListener
import com.android.adsdk.utils.Constants.CONFIG_ERROR_TAG
import com.android.adsdk.utils.Constants.CONFIG_FAILURE_MSG
import com.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.android.adsdk.utils.MapperUtils.mapGamLoadAdErrorToError
import com.android.adsdk.utils.MapperUtils.toSingnalApi
import com.app.logger.CustomLogger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.BannerBaseAdUnit
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError

/**
 * banner ad class for original type of loading where bid request call is made
 * and modified adRequest is returned to the user
 */
class BannerAd(adUnitId: String, private val adSize: AdSize = AdSize.BANNER) :
    Ad(BannerAdUnit(adUnitId, adSize.width, adSize.height)) {

    constructor(adUnitId: String, width: Int, height: Int) : this(adUnitId, AdSize(width, height))

    private val bannerAdUnit: BannerAdUnit = adUnit as BannerAdUnit
    override val adType: AdType = AdType.BANNER
    private var parameterApis = listOf<AdSignal.Api>()

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
     * starts the bid request call
     * @param adRequest is the ad request for ad manager
     * @param listener listens to request call events
     */
    fun fetchDemandForAd(
        adRequest: AdManagerAdRequest,
        listener: OnBidCompletionListener,
    ) {
        if (AdSDKManager.isConfigEmpty()) {
            CustomLogger.error(CONFIG_ERROR_TAG, CONFIG_FAILURE_MSG)
            listener.onError(Error.CONFIG_ERROR_CONFIG_FAILURE)
            return
        } else if (AdSDKManager.isSdkOnVacation()) {
            CustomLogger.error(CONFIG_ERROR_TAG, SDK_ON_VACATION_LOG_MSG)
            listener.onError(Error.CONFIG_ERROR_CONFIG_KILL_SWITCH)
            return
        }
        fetchDemand(adRequest, listener)
    }

    /**
     * starts the bid request call and loads the ad into the adView
     * @param adView is the view where ad loads
     * @param adRequest is the ad request for ad manager
     * @param listener listens to GAM events
     */
    private fun fetchDemandAndLoad(
        adView: AdManagerAdView,
        adRequest: AdManagerAdRequest,
        listener: GamEventListener,
    ) {
        bannerAdUnit.parameters
        if (AdSDKManager.isConfigEmpty()) {
            CustomLogger.error(CONFIG_ERROR_TAG, CONFIG_FAILURE_MSG)
            listener.onError(Error.CONFIG_ERROR_CONFIG_FAILURE)
            return
        } else if (AdSDKManager.isSdkOnVacation()) {
            CustomLogger.error(CONFIG_ERROR_TAG, SDK_ON_VACATION_LOG_MSG)
            listener.onError(Error.CONFIG_ERROR_CONFIG_KILL_SWITCH)
            return
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Update ad view
                AdViewUtils.findPrebidCreativeSize(
                    adView,
                    object : AdViewUtils.PbFindSizeListener {
                        override fun success(width: Int, height: Int) {
                            adView.setAdSizes(AdSize(width, height))
                        }

                        override fun failure(error: PbFindSizeError) {
                        }
                    },
                )
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

        fetchDemand(
            adRequest,
            object : OnBidCompletionListener {
                override fun onSuccess(keywordMap: Map<String, String>?) {
                    listener.onSuccess(keywordMap)
                    adView.loadAd(adRequest)
                }

                override fun onError(error: Error) {
                    listener.onError(error)
                    adView.loadAd(adRequest)
                }
            },
        )
    }

    fun setParameters(apis: List<AdSignal.Api>) {
        parameterApis = apis
        val params = BannerBaseAdUnit.Parameters()
        params.api = apis.toSingnalApi()
        bannerAdUnit.parameters = params
    }

    fun getParameters() = parameterApis
}