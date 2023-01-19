package com.medianet.android.adsdk.rendering.banner

import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.AdSize
import com.medianet.android.adsdk.Util
import com.medianet.android.adsdk.Util.getPrebidAdSizeFromGAMAdSize
import com.medianet.android.adsdk.rendering.AdEventListener
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.api.rendering.listeners.BannerViewListener
import org.prebid.mobile.eventhandlers.GamBannerEventHandler

class BannerAd(context: Context, adUnitId: String, adSize: AdSize) {

    constructor(context: Context, adUnitId: String, width: Int, height: Int) : this(context, adUnitId, AdSize(width, height))

    private val bannerEventHandler = GamBannerEventHandler(context, adUnitId, getPrebidAdSizeFromGAMAdSize(adSize))
    // TODO Pass adUnitId to BannerAdUnit once it is configured
    private val bannerView = BannerView(context, "imp-prebid-banner-320-50", bannerEventHandler)
    private var bannerAdListener: AdEventListener? = null

    fun setBannerAdListener(listener: AdEventListener) = apply {
        bannerAdListener = listener
        bannerView.setBannerListener(object: BannerViewListener {
            override fun onAdLoaded(bannerView: BannerView?) {
                bannerAdListener?.onAdLoaded()
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
}