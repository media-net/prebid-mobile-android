package com.medianet.android.adsdk

import android.content.Context
import android.util.Log
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.PrebidMobile.LogLevel
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

// This will replace PrebidMobileClass with better patterns
object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "0689a263-318d-448b-a3d4-b02e8a709d9d" //TODO - should store in preference ?
    private const val HOST_URL = "https://prebid-server-test-j.prebid.org/openrtb2/auction" //TODO - should store in preference ?

    private var publisherSdkInitializationListener: SdkInitializationListener? = null

    private var prebidSdkInitializationListener: SdkInitializationListener = object : SdkInitializationListener  {
        override fun onSdkInit() {
            Log.d(TAG, "SDK initialized successfully!")
            // If we need to send event for SDK initialisation we can do here
            publisherSdkInitializationListener?.onSdkInit()
        }

        override fun onSdkFailedToInit(error: InitError?) {
            Log.e(TAG, "SDK initialization error: " + error?.error)
            publisherSdkInitializationListener?.onSdkInit()
        }

    }

    fun initPrebidSDK(
        applicationContext : Context,
        accountId: String,
        sdkInitializationListener: SdkInitializationListener? = null
    ) {
        PrebidMobile.setPrebidServerAccountId(TEMP_ACCOUNT_ID)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(HOST_URL))
        publisherSdkInitializationListener = sdkInitializationListener
        PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
        PrebidMobile.setShareGeoLocation(true)

        //TODO - config call?
    }

    fun isCompatibleWithGoogleMobileAds(version: String) = PrebidMobile.checkGoogleMobileAdsCompatibility(version)

    fun setLogLevel() {
    }

}