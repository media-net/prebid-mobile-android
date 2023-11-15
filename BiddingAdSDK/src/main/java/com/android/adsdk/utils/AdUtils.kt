package com.android.adsdk.utils

import android.view.View
import com.android.adsdk.ad.nativead.NativeInAppAd
import com.android.adsdk.ad.nativead.listeners.NativeAdListener
import com.android.adsdk.base.FindSizeError
import com.android.adsdk.base.listeners.FindSizeListener
import org.prebid.mobile.PrebidNativeAd
import org.prebid.mobile.PrebidNativeAdListener
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.AdViewUtils.PbFindSizeListener
import org.prebid.mobile.addendum.PbFindSizeError

/**
 * util class ad views
 */
object AdUtils {

    /**
     * this api can be used to find if the passed object contains info to retrieve valid cached native response or not,
     * and notifies using the NativeAdListener
     *
     * @param obj instances of google native ads
     * @param listener to notify the validity of passed object via @onNativeLoaded, #onNativeNotFound, #onNativeNotValid
     */
    fun findNative(obj: Any, listener: NativeAdListener) {
        AdViewUtils.findNative(obj, object : PrebidNativeAdListener {
            override fun onPrebidNativeLoaded(ad: PrebidNativeAd) {
                listener.onNativeLoaded(NativeInAppAd(ad))
            }

            override fun onPrebidNativeNotFound() {
                listener.onNativeNotFound()
            }

            override fun onPrebidNativeNotValid() {
                listener.onNativeNotValid()
            }
        })
    }

    /**
     * verifies whether it’s AdSdk’s ad and resize the ad slot respectively to the creative’s properties.
     * @param adView is the GAM Ad View
     * @param handler gives size according to creative's properties through callbacks
     */
    fun findCreativeSize(adView: View?, handler: FindSizeListener?) {
        AdViewUtils.findPrebidCreativeSize(adView, object : PbFindSizeListener{
            override fun success(width: Int, height: Int) {
                handler?.success(width, height)
            }

            override fun failure(error: PbFindSizeError) {
                handler?.failure(FindSizeError(error))
            }

        })
    }

}