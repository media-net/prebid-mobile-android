package com.medianet.android.adsdk

import com.google.android.gms.ads.admanager.AdManagerInterstitialAd

interface GamEventListener : OnBidCompletionListener {
    fun onAdLoaded() {}
    fun onInterstitialAdLoaded(ad: AdManagerInterstitialAd) {}
    fun onAdClicked() {}
    fun onAdClosed() {}
    fun onAdFailedToLoad(error: Error) {}
    fun onAdOpened() {}
    fun onAdImpression() {}
    fun onEvent(key: String, value: String) {}
}
