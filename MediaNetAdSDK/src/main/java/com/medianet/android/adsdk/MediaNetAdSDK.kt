package com.medianet.android.adsdk

import android.content.Context
import android.util.Log
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener
import org.prebid.mobile.rendering.sdk.SdkInitializer

object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "0689a263-318d-448b-a3d4-b02e8a709d9d" //TODO - should store in preference ?
    private const val HOST_URL = "https://prebid-server-test-j.prebid.org/openrtb2/auction" //TODO - should store in preference ?

    private var logLevel: MLogLevel = MLogLevel.INFO
    // TODO - For some action we can check if test mode is on or not
    private var isTestMode: Boolean = false


    private var publisherSdkInitListener: MSdkInitListener? = null

    private var prebidSdkInitializationListener: SdkInitializationListener = object : SdkInitializationListener  {
        override fun onSdkInit() {
            Log.d(TAG, "SDK initialized successfully!")
            // If we need to send event for SDK initialisation we can do here
            publisherSdkInitListener?.onInitSuccess()
        }

        override fun onSdkFailedToInit(error: InitError?) {
            Log.e(TAG, "SDK initialization error: " + error?.error)
            val sdkInitError = Error.SDK_INIT_ERROR.apply {
                errorMessage = error?.error.toString()
            }
            publisherSdkInitListener?.onInitFailed(sdkInitError)
        }

    }

    fun initPrebidSDK(
        applicationContext : Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    ) {
        LogUtil.setBaseTag(TAG)
        PrebidMobile.setPrebidServerAccountId(accountId)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(HOST_URL))
        publisherSdkInitListener = sdkInitListener
        PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
    }


    // TODO - publisherAccountId is better name?
    fun getAccountId() = PrebidMobile.getPrebidServerAccountId()
    fun setAccountId(accountId: String) = apply { PrebidMobile.setPrebidServerAccountId(accountId) }

    fun getPrebidServerHost() = HOST_URL

    fun setTimeoutMillis(timeoutMillis: Long) = apply { PrebidMobile.setTimeoutMillis(timeoutMillis.toInt()) }
    fun getTimeOutMillis() = PrebidMobile.getTimeoutMillis()

    //TODO - should expose PrebidMobile.setCustomHeaders()

    //TODO - should expose PrebidMobile.getApplicationContext()

    fun enableTestMode() = apply {
        isTestMode = true
        PrebidMobile.setPbsDebug(true)
    }
    fun disableTestMode() = apply {
        isTestMode = false
        PrebidMobile.setPbsDebug(false)
    }
    fun isDebugMode(): Boolean {
        //return isTestMode
        return PrebidMobile.getPbsDebug()
    }

    fun setStoredAuctionResponse(storedAuctionResponse: String? = null) = apply {
        storedAuctionResponse?.let { storedResponse ->
            PrebidMobile.setStoredAuctionResponse(storedResponse)
        }
    }

    //TODO - should expose PrebidMobile.addStoredBidResponse()

    fun setLogLevel(level: MLogLevel) = apply {
        logLevel = level
        PrebidMobile.setLogLevel(Util.mapLogLevelToPrebidLogLevel(level))
    }

    fun getLoLevel() = logLevel

    fun isCompatibleWithGoogleMobileAds(version: String) = PrebidMobile.checkGoogleMobileAdsCompatibility(version)

    fun shouldShareGeoLocation(share: Boolean) = apply { PrebidMobile.setShareGeoLocation(share) }
    fun isSharingGeoLocation() = PrebidMobile.isShareGeoLocation()

    fun setSubjectToGDPR(enable: Boolean) = apply { TargetingParams.setSubjectToGDPR(enable) }

    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }
}
