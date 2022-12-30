package com.medianet.android.adsdk

import androidx.annotation.NonNull
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import org.prebid.mobile.AdUnit
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.ResultCode


/**
 *
 *
 */
class BannerAd(@NonNull val configId: String, val adSize: AdSize = AdSize.BANNER): Ad() {

    constructor(@NonNull configId: String, width: Int, height: Int) : this(configId, AdSize(width, height))

    private val bannerAdUnit: BannerAdUnit = BannerAdUnit(configId, adSize.width, adSize.height)

    override val adUnit: AdUnit = bannerAdUnit
    override val adType: AdType = AdType.BANNER


    fun addAdditionalSize(size: AdSize) = apply {
        bannerAdUnit.addAdditionalSize(size.width, size.height)
    }

    fun addAdditionalSize(width: Int, height: Int) = apply {
        bannerAdUnit.addAdditionalSize(width, height)
    }
}