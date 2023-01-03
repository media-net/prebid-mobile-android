package com.medianet.android.adsdk

interface GamEventListener: OnBidCompletionListener {
    fun onAdLoaded(){}
    fun onAdClicked(){}
    fun onAdClosed(){}
    fun onAdFailedToLoad(error: Error){}
    fun onAdOpened(){}
    fun onAdImpression(){}
    fun onEvent(key: String, value: String){}
}