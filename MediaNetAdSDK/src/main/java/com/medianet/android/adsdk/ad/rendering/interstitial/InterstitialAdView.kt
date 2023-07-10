package com.medianet.android.adsdk.ad.rendering.interstitial

import android.app.Activity
import android.content.Context
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.base.AdType
import com.medianet.android.adsdk.base.MAdSize
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.ad.rendering.AdEventListener
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidAdSizeFromMediaNetAdSize
import com.medianet.android.adsdk.utils.MapperUtils.mapAdExceptionToError
import com.medianet.android.adsdk.utils.MapperUtils.mapInterstitialAdFormat
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.InterstitialAdUnit
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener
import org.prebid.mobile.api.rendering.listeners.MediaEventListener
import org.prebid.mobile.eventhandlers.GamInterstitialEventHandler
import java.util.*

/**
 * interstitial ad class for rendering type
 */
class InterstitialAdView(context: Context, val adUnitId: String, adUnitFormats: EnumSet<AdType>) {

    constructor(context: Context, adUnitId: String): this(context, adUnitId, EnumSet.of(
        AdType.DISPLAY))

    private val gamInterstitialEventHandler = GamInterstitialEventHandler(context as Activity?, adUnitId)

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
        mInterstitialAdUnit = InterstitialAdUnit(context, adUnitId, adUnitFormats.mapInterstitialAdFormat(), gamInterstitialEventHandler, mediaEventListener)
    }

    /**
     * listens to the ad events once the bid request completes
     * @param listener
     */
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
                interstitialAdListener?.onAdFailed(exception.mapAdExceptionToError())
            }

            override fun onAdClicked(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdClicked()
            }

            override fun onAdClosed(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdClosed()
            }

        })
    }

    /**
     * sets minimum size percentage for the ad
     * that in turn will be sent in the request for bid request call
     * @param minSizePercentage
     */
    fun setMinSizePercentage(minSizePercentage: MAdSize) {
        mInterstitialAdUnit.setMinSizePercentage(minSizePercentage.getPrebidAdSizeFromMediaNetAdSize())
    }

    /**
     * displays interstitial ad on the screen
     */
    fun show() {
        mInterstitialAdUnit.show()
    }

    /**
     * initiates the ad loading by doing bid request call
     */
    fun loadAd() {

        if(MediaNetAdSDK.isSdkOnVacation() || MediaNetAdSDK.isConfigEmpty()){
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            interstitialAdListener?.onAdFailed(com.medianet.android.adsdk.base.Error.CONFIG_ERROR)
            return
        }
        mInterstitialAdUnit.loadAd()
    }

    /**
     * destroys the interstitial ad unit
     */
    fun destroy() {
        mInterstitialAdUnit.destroy()
    }
}