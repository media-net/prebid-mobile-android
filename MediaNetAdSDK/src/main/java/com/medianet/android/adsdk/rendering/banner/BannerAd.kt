package com.medianet.android.adsdk.rendering.banner

import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.AdSize
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.rendering.AdEventListener
import com.medianet.android.adsdk.utils.Util
import com.medianet.android.adsdk.utils.Util.getPrebidAdSizeFromGAMAdSize
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.api.rendering.listeners.BannerViewListener
import org.prebid.mobile.api.rendering.listeners.MediaEventListener
import org.prebid.mobile.eventhandlers.GamBannerEventHandler

class BannerAd(context: Context, val adUnitId: String, adSize: AdSize) {

    constructor(context: Context, adUnitId: String, width: Int, height: Int) : this(context, adUnitId, AdSize(width, height))

    private val bannerEventHandler = GamBannerEventHandler(context, adUnitId, getPrebidAdSizeFromGAMAdSize(adSize))
    // TODO Pass adUnitId to BannerAdUnit once it is configured
    private val bannerView = BannerView(context, "imp-prebid-banner-320-50", bannerEventHandler, object : MediaEventListener{
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

    fun getView(): FrameLayout {
        return bannerView
    }

    fun setAutoRefreshInterval(delay: Int)  = apply {
        bannerView.setAutoRefreshDelay(delay)
    }

    fun loadAd() {
        bannerView.loadAd()
    }

    fun destroy() {
        bannerView.destroy()
    }

    fun stopRefresh() {
        bannerView.stopRefresh()
    }

    //TODO - we have not added method to add additional sizes??
}