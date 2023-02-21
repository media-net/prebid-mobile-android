package com.medianet.android.adsdk.ad.rendering

import com.medianet.android.adsdk.base.Error

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