package com.medianet.android.adsdk

import androidx.annotation.WorkerThread

/**
 * Listener interface to listen to native ad events
 */
interface NativeAdEventListener {
    /**
     * Callback method for Ad's click event
     */
    fun onAdClicked()

    /**
     * Callback method for Ad's click event
     */
    @WorkerThread
    fun onAdImpression()

    /**
     * Callback method for Ad's click event
     */
    fun onAdExpired()
}