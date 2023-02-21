package com.medianet.android.adsdk.base.listeners

import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.medianet.android.adsdk.base.Error

/**
 * listener interface for ad events through GAM
 */
interface GamEventListener: OnBidCompletionListener {
    fun onAdLoaded(){}
    fun onInterstitialAdLoaded(ad: AdManagerInterstitialAd){}
    fun onAdClicked(){}
    fun onAdClosed(){}
    fun onAdFailedToLoad(error: Error){}
    fun onAdOpened(){}
    fun onAdImpression(){}
    fun onEvent(key: String, value: String){}
}