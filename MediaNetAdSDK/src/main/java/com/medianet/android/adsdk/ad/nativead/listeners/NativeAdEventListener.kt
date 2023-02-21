package com.medianet.android.adsdk.ad.nativead.listeners

import androidx.annotation.WorkerThread

/**
 * listener interface to listen to native ad events
 */
interface NativeAdEventListener {
    /**
     * callback method for ad's click event
     */
    fun onAdClicked()

    /**
     * callback method for ad's click event
     */
    @WorkerThread
    fun onAdImpression()

    /**
     * callback method for ad's click event
     */
    fun onAdExpired()
}