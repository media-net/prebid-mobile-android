package com.medianet.android.adsdk.ad.nativead.listeners

import com.medianet.android.adsdk.ad.nativead.NativeInAppAd

/**
 * listener interface to listen to native ad events
 */
interface NativeAdListener{
    /**
     * a successful native ad is returned
     *
     * @param ad use this instance for displaying
     */
    fun onNativeLoaded(ad: NativeInAppAd)

    /**
     * native was not found in the server returned response,
     * please display the ad as regular ways
     */
    fun onNativeNotFound()

    /**
     * native ad was returned, however, the bid is not valid for displaying
     * should be treated as on ad load failed
     */
    fun onNativeNotValid()
}