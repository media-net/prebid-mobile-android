package com.medianet.android.adsdk

import com.google.android.gms.ads.AdSize
import org.prebid.mobile.AdUnit
import org.prebid.mobile.BannerAdUnit


class BannerAd(adUnitId: String, val adSize: AdSize = AdSize.BANNER): Ad() {

    constructor(adUnitId: String, width: Int, height: Int) : this(adUnitId, AdSize(width, height))

    // TODO Pass adUnitId to BannerAdUnit once it is configured
    private val bannerAdUnit: BannerAdUnit = BannerAdUnit("imp-prebid-banner-300-250", adSize.width, adSize.height)

    override val adUnit: AdUnit = bannerAdUnit
    override val adType: AdType = AdType.BANNER


    fun addAdditionalSize(size: AdSize) = apply {
        bannerAdUnit.addAdditionalSize(size.width, size.height)
    }

    fun addAdditionalSize(width: Int, height: Int) = apply {
        bannerAdUnit.addAdditionalSize(width, height)
    }
}