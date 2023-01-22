package com.medianet.android.adsdk.rendering.interstitial

import android.app.Activity
import android.content.Context
import com.medianet.android.adsdk.AdType
import com.medianet.android.adsdk.Util.mapAdExceptionToError
import com.medianet.android.adsdk.Util.mapInterstitialAdFormat
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.rendering.AdEventListener
import org.prebid.mobile.AdSize
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.InterstitialAdUnit
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener
import org.prebid.mobile.api.rendering.listeners.MediaEventListener
import org.prebid.mobile.eventhandlers.GamInterstitialEventHandler
import java.util.*

class InterstitialAd(context: Context, val adUnitId: String, adUnitFormats: EnumSet<AdType>) {

    constructor(context: Context, adUnitId: String): this(context, adUnitId, EnumSet.of(AdType.DISPLAY))

    private val gamInterstitialEventHandler = GamInterstitialEventHandler(context as Activity?, adUnitId)
    // TODO Pass adUnitId to InterstitialAdUnit once it is configured
    private val mInterstitialAdUnit: InterstitialAdUnit
    private var interstitialAdListener: AdEventListener? = null

    private val mediaEventListener = object : MediaEventListener {
        override fun onBidRequest() {
            EventManager.sendBidRequestEvent(
                dfpDivId = adUnitId,
                sizes = null
            )
        }

        override fun onBidRequestTimeout() {
            EventManager.sendTimeoutEvent(
                dfpDivId = adUnitId,
                sizes = null
            )
        }

        override fun onRequestSentToGam() {
            EventManager.sendAdRequestToGamEvent(
                dfpDivId = adUnitId,
                sizes = null
            )
        }

        override fun onAdLoaded() {
            EventManager.sendAdLoadedEvent(
                dfpDivId = adUnitId,
                sizes = null
            )
        }
    }

    init {
        mInterstitialAdUnit = InterstitialAdUnit(context, "imp-prebid-display-interstitial-320-480", mapInterstitialAdFormat(adUnitFormats), gamInterstitialEventHandler, mediaEventListener)
    }

    fun setInterstitialAdListener(listener: AdEventListener) {
        interstitialAdListener = listener
        mInterstitialAdUnit.setInterstitialAdUnitListener(object: InterstitialAdUnitListener {
            override fun onAdLoaded(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdLoaded()
                mediaEventListener.onAdLoaded()
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