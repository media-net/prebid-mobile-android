package com.medianet.android.adsdk

import android.content.Context
import android.util.Log
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

object MediaNetAdSDK {
    val TAG = "MediaNetAdSDK"


     fun initPrebidSDK( applicationContext : Context) {
        PrebidMobile.setPrebidServerAccountId("0689a263-318d-448b-a3d4-b02e8a709d9d")
        PrebidMobile.setPrebidServerHost(Host.createCustomHost("https://prebid-server-test-j.prebid.org/openrtb2/auction"))
        PrebidMobile.initializeSdk(applicationContext, object : SdkInitializationListener {
            override fun onSdkInit() {
                Log.d(TAG, "SDK initialized successfully!")
            }

            override fun onSdkFailedToInit(error: InitError?) {
                Log.e(TAG, "SDK initialization error: " + error?.error)
            }
        })
        PrebidMobile.setShareGeoLocation(true)
    }

}