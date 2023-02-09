package com.medianet.android.adsdk

import com.medianet.android.adsdk.nativead.NativeInAppAd
import org.prebid.mobile.PrebidNativeAd
import org.prebid.mobile.PrebidNativeAdListener
import org.prebid.mobile.addendum.AdViewUtils


object MAdViewUtils {

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

}