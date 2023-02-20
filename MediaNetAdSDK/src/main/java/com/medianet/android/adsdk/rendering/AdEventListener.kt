package com.medianet.android.adsdk.rendering

import com.medianet.android.adsdk.Error

/**
 * listener interface to listen ad events
 */
interface AdEventListener {
    fun onAdClicked()
    fun onAdClosed()
    fun onAdDisplayed()
    fun onAdFailed(error: Error)
    fun onAdLoaded()
}