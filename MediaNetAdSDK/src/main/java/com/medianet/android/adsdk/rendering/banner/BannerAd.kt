package com.medianet.android.adsdk.rendering.banner

import android.content.Context
import android.widget.FrameLayout
import com.google.android.gms.ads.AdSize
import com.medianet.android.adsdk.Util.getPrebidAdSizeFromGAMAdSize
import org.prebid.mobile.api.rendering.BannerView
import org.prebid.mobile.eventhandlers.GamBannerEventHandler

class BannerAd(context: Context, configId: String, adUnitId: String, adSize: AdSize) {

    constructor(context: Context, configId: String, adUnitId: String, width: Int, height: Int) : this(context, configId, adUnitId, AdSize(width, height))

    private val bannerEventHandler = GamBannerEventHandler(context, adUnitId, getPrebidAdSizeFromGAMAdSize(adSize))
    private val bannerView = BannerView(context, configId, bannerEventHandler)

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