package com.medianet.android.adsdk

import org.prebid.mobile.AdUnit
import org.prebid.mobile.InterstitialAdUnit

class InterstitialAd(configId: String): Ad() {
    private var mInterstitialAdUnit: InterstitialAdUnit = InterstitialAdUnit(configId)

    constructor(configId: String, minWidthPerc: Int, minHeightPerc: Int) : this(configId) {
        mInterstitialAdUnit = InterstitialAdUnit(configId, minWidthPerc, minHeightPerc)
    }

    override val adUnit: AdUnit = mInterstitialAdUnit
    override val adType: AdType = AdType.INTERSTITIAL
}