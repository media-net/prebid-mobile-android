package com.medianet.android.adsdk

import com.medianet.android.adsdk.nativead.NativeInAppAd

interface NativeAdListener {
    /**
     * A successful Native ad is returned
     *
     * @param ad use this instance for displaying
     */
    fun onNativeLoaded(ad: NativeInAppAd)

    /**
     * Native was not found in the server returned response,
     * Please display the ad as regular ways
     */
    fun onNativeNotFound()

    /**
     * Native ad was returned, however, the bid is not valid for displaying
     * Should be treated as on ad load failed
     */
    fun onNativeNotValid()
}
