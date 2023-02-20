package com.medianet.android.adsdk.nativead

import android.view.View
import com.medianet.android.adsdk.NativeAdEventListener
import org.prebid.mobile.PrebidNativeAd
import org.prebid.mobile.PrebidNativeAdEventListener

/**
 * native ad class that is provided for inflation on UI
 * once the native ad is loaded
 */
class NativeInAppAd(private val prebidNativeAd: PrebidNativeAd) {
    fun registerView(view: View, listener: NativeAdEventListener): Boolean {
        return prebidNativeAd.registerView(view, object : PrebidNativeAdEventListener{
            override fun onAdClicked() {
                listener.onAdClicked()
            }

            override fun onAdImpression() {
                listener.onAdImpression()
            }

            override fun onAdExpired() {
                listener.onAdExpired()
            }

        })
    }

    fun getIconUrl(): String = prebidNativeAd.iconUrl

    fun getTitle(): String = prebidNativeAd.title

    fun getImageUrl(): String = prebidNativeAd.imageUrl

    fun getDescription(): String = prebidNativeAd.description

    fun getCallToAction(): String = prebidNativeAd.callToAction
}