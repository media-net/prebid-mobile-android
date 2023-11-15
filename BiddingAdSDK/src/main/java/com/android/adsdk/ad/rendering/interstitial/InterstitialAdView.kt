package com.android.adsdk.ad.rendering.interstitial

import android.app.Activity
import android.content.Context
import com.app.logger.CustomLogger
import com.android.adsdk.ad.rendering.AdEventListener
import com.android.adsdk.base.AdViewSize
import com.android.adsdk.events.EventManager
import com.android.adsdk.utils.Constants.CONFIG_ERROR_TAG
import com.android.adsdk.utils.Constants.CONFIG_FAILURE_MSG
import com.android.adsdk.utils.Constants.INTERSTITIAL_MIN_HEIGHT_PERCENTAGE
import com.android.adsdk.utils.Constants.INTERSTITIAL_MIN_WIDTH_PERCENTAGE
import com.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.android.adsdk.utils.MapperUtils.getPrebidAdSizeFromMediaNetAdSize
import com.android.adsdk.utils.MapperUtils.mapAdExceptionToError
import com.android.adsdk.utils.MapperUtils.mapInterstitialAdFormat
import java.util.EnumSet
import org.prebid.mobile.api.exceptions.AdException
import org.prebid.mobile.api.rendering.InterstitialAdUnit
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener
import org.prebid.mobile.api.rendering.listeners.LoggingEventListener
import org.prebid.mobile.eventhandlers.GamInterstitialEventHandler
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse

/**
 * interstitial ad class for rendering type
 */
class InterstitialAdView(context: Context, val adUnitId: String, adUnitFormats: EnumSet<com.android.adsdk.base.AdType>) {

    constructor(context: Context, adUnitId: String) : this(
        context,
        adUnitId,
        EnumSet.of(
            com.android.adsdk.base.AdType.DISPLAY,
        ),
    )

    private val gamInterstitialEventHandler = GamInterstitialEventHandler(context as Activity?, adUnitId)

    private val mInterstitialAdUnit: InterstitialAdUnit
    private var interstitialAdListener: AdEventListener? = null

    private val loggingEventListener = object :
        LoggingEventListener {
        override fun onBidRequest() {
            EventManager.sendBidRequestEvent(
                dfpDivId = adUnitId,
                sizes = null,
            )
        }

        override fun onBidRequestTimeout() {
            EventManager.sendTimeoutEvent(
                dfpDivId = adUnitId,
                sizes = null,
            )
        }

        override fun onRequestSentToGam(bidResponse: BidResponse?, exception: AdException?) {
            EventManager.sendAdRequestToGamEvent(
                dfpDivId = adUnitId,
                sizes = null,
                adType = com.android.adsdk.base.AdType.INTERSTITIAL,
                bidResponse = bidResponse,
                exception = exception,
            )
        }

        override fun onAdLoaded() {
            EventManager.sendAdLoadedEvent(
                dfpDivId = adUnitId,
                sizes = null,
            )
        }
    }

    init {
        mInterstitialAdUnit = InterstitialAdUnit(context, adUnitId, adUnitFormats.mapInterstitialAdFormat(), gamInterstitialEventHandler, loggingEventListener)
        setMinSizePercentage(AdViewSize(INTERSTITIAL_MIN_WIDTH_PERCENTAGE, INTERSTITIAL_MIN_HEIGHT_PERCENTAGE))
    }

    /**
     * listens to the ad events once the bid request completes
     * @param listener
     */
    fun setInterstitialAdListener(listener: AdEventListener) {
        interstitialAdListener = listener
        mInterstitialAdUnit.setInterstitialAdUnitListener(object : InterstitialAdUnitListener {
            override fun onAdLoaded(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdLoaded()
                loggingEventListener.onAdLoaded()
            }

            override fun onAdDisplayed(interstitialAdUnit: InterstitialAdUnit?) {
                interstitialAdListener?.onAdDisplayed()
            }

            override fun onAdFailed(
                interstitialAdUnit: InterstitialAdUnit?,
                exception: AdException?,
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
    fun setMinSizePercentage(minSizePercentage: AdViewSize) {
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
        if (com.android.adsdk.AdSDKManager.isConfigEmpty()) {
            CustomLogger.error(CONFIG_ERROR_TAG, CONFIG_FAILURE_MSG)
            interstitialAdListener?.onAdFailed(com.android.adsdk.base.Error.CONFIG_ERROR_CONFIG_FAILURE)
            return
        } else if (com.android.adsdk.AdSDKManager.isSdkOnVacation()) {
            CustomLogger.error(CONFIG_ERROR_TAG, SDK_ON_VACATION_LOG_MSG)
            interstitialAdListener?.onAdFailed(com.android.adsdk.base.Error.CONFIG_ERROR_CONFIG_KILL_SWITCH)
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

    /**
     * Method to log GAM error on publisher side, to track dropout due to GAM errors
     * This is a temporary method might not go in release
     */
    fun sendGAMErrorEvent(adUnit: String, errorCode: Int, errorMessage: String, errorCause: String = "") {
        EventManager.sendGAMErrorEvent(
            dfpDivId = adUnit,
            errorMessage = errorMessage,
            errorCode = errorCode,
        )
    }
}
