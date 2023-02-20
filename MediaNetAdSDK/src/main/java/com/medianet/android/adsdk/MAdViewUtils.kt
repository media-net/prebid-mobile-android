package com.medianet.android.adsdk

import com.medianet.android.adsdk.nativead.NativeInAppAd
import org.prebid.mobile.PrebidNativeAd
import org.prebid.mobile.PrebidNativeAdListener
import org.prebid.mobile.addendum.AdViewUtils

/**
 * util class ad views
 */
object MAdViewUtils {

    /**
     * this api can be used to find if the passed object contains info to retrieve valid cached native response or not,
     * and notifies using the NativeAdListener
     *
     * @param obj instances of google native ads
     * @param listener to notify the validity of passed object via @onNativeLoaded, #onNativeNotFound, #onNativeNotValid
     */
    fun findNative(obj: Any, listener: NativeAdListener) {
        AdViewUtils.findNative(
            obj,
            object : PrebidNativeAdListener {
                override fun onPrebidNativeLoaded(ad: PrebidNativeAd) {
                    listener.onNativeLoaded(NativeInAppAd(ad))
                }

                override fun onPrebidNativeNotFound() {
                    listener.onNativeNotFound()
                }

                override fun onPrebidNativeNotValid() {
                    listener.onNativeNotValid()
                }
            }
        )
    }
}
