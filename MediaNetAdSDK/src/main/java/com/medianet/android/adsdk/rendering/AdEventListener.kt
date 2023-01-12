package com.medianet.android.adsdk.rendering

import com.medianet.android.adsdk.Error

interface AdEventListener {
    fun onAdClicked()
    fun onAdClosed()
    fun onAdDisplayed()
    fun onAdFailed(error: Error)
    fun onAdLoaded()
}