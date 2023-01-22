package com.medianet.android.adsdk

import org.prebid.mobile.AdSize
import org.prebid.mobile.InterstitialAdUnit

class InterstitialAd(adUnitId: String): Ad(InterstitialAdUnit("imp-prebid-display-interstitial-320-480")) {
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private var mInterstitialAdUnit: InterstitialAdUnit = adUnit as InterstitialAdUnit

    constructor(adUnitId: String, minWidthPerc: Int, minHeightPerc: Int) : this(adUnitId) {
        mInterstitialAdUnit.configuration.minSizePercentage = AdSize(minWidthPerc, minHeightPerc)
    }

    override val adType: AdType = AdType.INTERSTITIAL
}