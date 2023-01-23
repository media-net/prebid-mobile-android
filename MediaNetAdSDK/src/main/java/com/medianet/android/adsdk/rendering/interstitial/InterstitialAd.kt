package com.medianet.android.adsdk.rendering.interstitial

import android.app.Activity
import android.content.Context
import com.medianet.android.adsdk.AdType
import com.medianet.android.adsdk.utils.Util.mapAdExceptionToError
import com.medianet.android.adsdk.utils.Util.mapInterstitialAdFormat
import com.medianet.android.adsdk.rendering.AdEventListener
import org.prebid.mobile.AdSize
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.InterstitialAdUnit
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener
import org.prebid.mobile.eventhandlers.GamInterstitialEventHandler
import java.util.*

class InterstitialAd(context: Context, adUnitId: String, adUnitFormats: EnumSet<AdType>) {

    constructor(context: Context, adUnitId: String): this(context, adUnitId, EnumSet.of(AdType.DISPLAY))

    private val gamInterstitialEventHandler = GamInterstitialEventHandler(context as Activity?, adUnitId)
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private val mInterstitialAdUnit = InterstitialAdUnit(context, "imp-prebid-display-interstitial-320-480", mapInterstitialAdFormat(adUnitFormats), gamInterstitialEventHandler)
    private var interstitialAdListener: AdEventListener? = null

    fun setInterstitialAdListener(listener: AdEventListener) {
        interstitialAdListener = listener
        mInterstitialAdUnit.setInterstitialAdUnitListener(object: InterstitialAdUnitListener {
            override fun onAdLoaded(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdLoaded()
            }

            override fun onAdDisplayed(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdDisplayed()
            }

            override fun onAdFailed(
                interstitialAdUnit: InterstitialAdUnit?,
                exception: AdException?
            ) {
                interstitialAdListener?.onAdFailed(mapAdExceptionToError(exception))
            }

            override fun onAdClicked(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdClicked()
            }

            override fun onAdClosed(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdClosed()
            }

        })
    }

    fun setMinSizePercentage(minSizePercentage: AdSize?) {
        mInterstitialAdUnit.setMinSizePercentage(minSizePercentage)
    }

    fun show() {
        mInterstitialAdUnit.show()
    }

    fun loadAd() {
        mInterstitialAdUnit.loadAd()
    }

    fun destroy() {
        mInterstitialAdUnit.destroy()
    }
}