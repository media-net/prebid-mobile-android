package com.medianet.android.adsdk

import android.content.Context
import android.util.Log
import com.app.analytics.AnalyticsSDK
import com.app.analytics.SamplingMap
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "8CU46ENS7" //TODO - should store in preference ?
    private const val HOST_URL = "https://mobile-sdk.media.net/rtb/pb/mobile-sdk" //TODO - should store in preference ?

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

        //Initialising Aanalytics
        initAnalytics(applicationContext)
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

    fun getLogLevel() = logLevel

    fun isCompatibleWithGoogleMobileAds(version: String) = PrebidMobile.checkGoogleMobileAdsCompatibility(version)

    fun shouldShareGeoLocation(share: Boolean) = apply { PrebidMobile.setShareGeoLocation(share) }
    fun isSharingGeoLocation() = PrebidMobile.isShareGeoLocation()

    fun setSubjectToGDPR(enable: Boolean) = apply { TargetingParams.setSubjectToGDPR(enable) }

    private fun initAnalytics(applicationContext: Context) {
        val samplingMap = SamplingMap()
        samplingMap.put(LoggingEvents.PROJECT.type, 100) //TODO get this in config call
        samplingMap.put(LoggingEvents.SLOT_OPPORTUNITY.type, 100) //TODO get this in config call

        val configuration = AnalyticsSDK.Configuration.Builder()
            .setDebugMode(false)
            .setAnalyticsUrl("https://logstash-gcpi.net") //TODO get this in config call
            .enableEventCaching(true)
            .enableEventSampling(false, samplingMap)
            .build()

        AnalyticsSDK.init(applicationContext, configuration)
        //AnalyticsSDK.pushEvent(Event(name = "Config_call_success", type = LoggingEvents.PROJECT.type))
    }


    //TODO - when to call this
    fun clear() {
        AnalyticsSDK.clear()
    }

    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }
}
