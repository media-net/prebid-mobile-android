package com.android.adsdk.ad.rendering.banner

import android.content.Context
import android.widget.FrameLayout
import com.android.adsdk.AdSDKManager
import com.android.adsdk.ad.rendering.AdEventListener
import com.android.adsdk.base.AdViewSize
import com.android.adsdk.base.Error
import com.android.adsdk.events.EventManager
import com.android.adsdk.model.banner.ContentModel
import com.android.adsdk.utils.Constants.CONFIG_ERROR_TAG
import com.android.adsdk.utils.Constants.CONFIG_FAILURE_MSG
import com.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.android.adsdk.utils.MapperUtils.getPrebidAdSizeFromMediaNetAdSize
import com.android.adsdk.utils.MapperUtils.mapAdExceptionToError
import com.android.adsdk.utils.MapperUtils.mapAdSizesToMAdSizes
import com.android.adsdk.utils.MapperUtils.mapContentModelToContentObject
import com.app.logger.CustomLogger
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.api.rendering.listeners.BannerViewListener
import org.prebid.mobile.api.rendering.listeners.LoggingEventListener
import org.prebid.mobile.eventhandlers.GamBannerEventHandler
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse

/**
 * banner ad class for rendering type
 */
class BannerAdView(context: Context, val adUnitId: String, adSize: AdViewSize) {

    constructor(context: Context, adUnitId: String, width: Int, height: Int) : this(context, adUnitId, AdViewSize(width, height))

    private val bannerEventHandler = GamBannerEventHandler(context, adUnitId, adSize.getPrebidAdSizeFromMediaNetAdSize())

    private val bannerView = BannerView(
        context,
        adUnitId,
        bannerEventHandler,
        object : LoggingEventListener {
            override fun onBidRequest() {
                EventManager.sendBidRequestEvent(
                    dfpDivId = adUnitId,
                    sizes = bannerEventHandler.adSizeArray.toHashSet().mapAdSizesToMAdSizes(),
                )
            }

            override fun onBidRequestTimeout() {
                EventManager.sendTimeoutEvent(
                    dfpDivId = adUnitId,
                    sizes = bannerEventHandler.adSizeArray.toHashSet().mapAdSizesToMAdSizes(),
                )
            }

            override fun onRequestSentToGam(bidResponse: BidResponse?, exception: AdException) {
                EventManager.sendAdRequestToGamEvent(
                    dfpDivId = adUnitId,
                    sizes = bannerEventHandler.adSizeArray.toHashSet().mapAdSizesToMAdSizes(),
                    adType = com.android.adsdk.base.AdType.BANNER,
                    bidResponse = bidResponse,
                    exception = exception,
                )
            }

            override fun onAdLoaded() {
                EventManager.sendAdLoadedEvent(
                    dfpDivId = adUnitId,
                    sizes = bannerEventHandler.adSizeArray.toHashSet().mapAdSizesToMAdSizes(),
                )
            }
        },
    )

    private var bannerAdListener: AdEventListener? = null

    /**
     * listens to the ad events once the bid request is completed
     * @param listener
     */
    fun setBannerAdListener(listener: AdEventListener) = apply {
        bannerAdListener = listener
        bannerView.setBannerListener(object : BannerViewListener {
            override fun onAdLoaded(view: BannerView?) {
                bannerAdListener?.onAdLoaded()
                bannerView.loggingEventListener?.onAdLoaded()
            }

            override fun onAdDisplayed(bannerView: BannerView?) {
                bannerAdListener?.onAdDisplayed()
            }

            override fun onAdFailed(bannerView: BannerView?, exception: AdException?) {
                bannerAdListener?.onAdFailed(exception.mapAdExceptionToError())
            }

            override fun onAdClicked(bannerView: BannerView?) {
                bannerAdListener?.onAdClicked()
            }

            override fun onAdClosed(bannerView: BannerView?) {
                bannerAdListener?.onAdClosed()
            }
        })
    }

    /**
     * returns the banner view UI
     */
    fun getView(): FrameLayout {
        return bannerView
    }

    /**
     * sets the interval in which the ad needs to be refreshed
     * @param delay is the interval time in seconds
     */
    fun setAutoRefreshInterval(delay: Int) = apply {
        bannerView.setAutoRefreshDelay(delay)
    }

    /**
     * initiates the ad loading by doing bid request call
     */
    fun loadAd() {
        if (AdSDKManager.isConfigEmpty()) {
            CustomLogger.error(CONFIG_ERROR_TAG, CONFIG_FAILURE_MSG)
            bannerAdListener?.onAdFailed(Error.CONFIG_ERROR_CONFIG_FAILURE)
            return
        } else if (AdSDKManager.isSdkOnVacation()) {
            CustomLogger.error(CONFIG_ERROR_TAG, SDK_ON_VACATION_LOG_MSG)
            bannerAdListener?.onAdFailed(Error.CONFIG_ERROR_CONFIG_KILL_SWITCH)
            return
        }

        bannerView.loadAd()
    }

    private fun addContent(mContent: ContentModel) {
        bannerView.addContent(mContent.mapContentModelToContentObject())
    }

    /**
     * destroys the banner view created
     */
    fun destroy() {
        bannerView.destroy()
    }

    /**
     * stops the banner view ad refresh
     */
    fun stopRefresh() {
        bannerView.stopRefresh()
    }

    /**
     * allows us to add multiple sizes to the ad
     * @param size specifies the size for ad slot through AdSize object
     */
    fun addAdditionalSize(size: AdViewSize) = apply {
        bannerView.addAdditionalSizes(size.getPrebidAdSizeFromMediaNetAdSize())
    }

    /**
     * allows us to add multiple sizes to the ad
     * @param width specifies the width for ad slot
     * @param height specifies the height for ad slot
     */
    fun addAdditionalSize(width: Int, height: Int) = apply {
        bannerView.addAdditionalSizes(org.prebid.mobile.AdSize(width, height))
    }

    /**
     * Returns a list all the additional ad sizes
     */
    fun getAdditionalAdSizes(): List<AdViewSize> {
        return bannerView.additionalSizes.toHashSet().mapAdSizesToMAdSizes()
    }

    /**
     * Method to log GAM error on publisher side, to track dropout due to GAM errors
     * This is a temporary method might not go in release
     */
    fun sendGAMErrorEvent(adUnit: String, errorCode: Int, errorMessage: String, errorCause: String = "") {
        EventManager.sendGAMErrorEvent(
            dfpDivId = adUnit,
            errorMessage = errorMessage,
            errorCode = errorCode
        )
    }
}
