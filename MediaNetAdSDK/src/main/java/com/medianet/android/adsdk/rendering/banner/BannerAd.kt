package com.medianet.android.adsdk.rendering.banner

import android.content.Context
import android.widget.FrameLayout
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.MAdSize
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.rendering.AdEventListener
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.Util
import com.medianet.android.adsdk.utils.Util.getPrebidAdSizeFromMediaNetAdSize
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.api.rendering.listeners.BannerViewListener
import org.prebid.mobile.api.rendering.listeners.MediaEventListener
import org.prebid.mobile.eventhandlers.GamBannerEventHandler

/**
 * banner ad class for rendering type
 */
class BannerAd(context: Context, val adUnitId: String, adSize: MAdSize) {

    constructor(context: Context, adUnitId: String, width: Int, height: Int) : this(context, adUnitId, MAdSize(width, height))

    private val bannerEventHandler = GamBannerEventHandler(context, adUnitId, getPrebidAdSizeFromMediaNetAdSize(adSize))

    private val bannerView = BannerView(context, adUnitId, bannerEventHandler, object : MediaEventListener{
        override fun onBidRequest() {
            EventManager.sendBidRequestEvent(
                dfpDivId = adUnitId,
                sizes = Util.mapAdSizesToMAdSizes(bannerEventHandler.adSizeArray.toHashSet())
            )
        }

        override fun onBidRequestTimeout() {
            EventManager.sendTimeoutEvent(
                dfpDivId = adUnitId,
                sizes = Util.mapAdSizesToMAdSizes(bannerEventHandler.adSizeArray.toHashSet())
            )
        }

        override fun onRequestSentToGam() {
            EventManager.sendAdRequestToGamEvent(
                dfpDivId = adUnitId,
                sizes = Util.mapAdSizesToMAdSizes(bannerEventHandler.adSizeArray.toHashSet())
            )
        }

        override fun onAdLoaded() {
            EventManager.sendAdLoadedEvent(
                dfpDivId = adUnitId,
                sizes = Util.mapAdSizesToMAdSizes(bannerEventHandler.adSizeArray.toHashSet())
            )
        }
    })

    private var bannerAdListener: AdEventListener? = null

    /**
     * listens to the ad events once the bid request is completed
     * @param listener
     */
    fun setBannerAdListener(listener: AdEventListener) = apply {
        bannerAdListener = listener
        bannerView.setBannerListener(object: BannerViewListener {
            override fun onAdLoaded(view: BannerView?) {
                bannerAdListener?.onAdLoaded()
                bannerView.mediaEventListener?.onAdLoaded()
            }

            override fun onAdDisplayed(bannerView: BannerView?) {
                bannerAdListener?.onAdDisplayed()
            }

            override fun onAdFailed(bannerView: BannerView?, exception: AdException?) {
                bannerAdListener?.onAdFailed(Util.mapAdExceptionToError(exception))
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
    fun setAutoRefreshInterval(delay: Int)  = apply {
        bannerView.setAutoRefreshDelay(delay)
    }

    /**
     * initiates the ad loading by doing bid request call
     */
    fun loadAd() {
        if(MediaNetAdSDK.isSdkOnVacation()){
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            return
        }

        bannerView.loadAd()
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
    fun addAdditionalSize(size: MAdSize) = apply {
        bannerView.addAdditionalSizes(getPrebidAdSizeFromMediaNetAdSize(size))
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
    fun getAdditionalAdSizes(): List<MAdSize> {
        return Util.mapAdSizesToMAdSizes(bannerView.additionalSizes.toHashSet())
    }
}