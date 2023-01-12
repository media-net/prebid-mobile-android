package com.medianet.android.adsdk

import org.prebid.mobile.AdUnit
import org.prebid.mobile.InterstitialAdUnit

class InterstitialAd(adUnitId: String): Ad() {
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private var mInterstitialAdUnit: InterstitialAdUnit = InterstitialAdUnit("imp-prebid-display-interstitial-320-480")

    constructor(adUnitId: String, minWidthPerc: Int, minHeightPerc: Int) : this(adUnitId) {
        mInterstitialAdUnit = InterstitialAdUnit("imp-prebid-display-interstitial-320-480", minWidthPerc, minHeightPerc)
    }

    override val adUnit: AdUnit = mInterstitialAdUnit
    override val adType: AdType = AdType.INTERSTITIAL
}