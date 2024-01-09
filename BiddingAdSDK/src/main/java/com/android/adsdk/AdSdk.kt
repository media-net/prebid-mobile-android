package com.android.adsdk

import android.content.Context
import com.android.adsdk.base.LoggingLevel
import com.android.adsdk.base.listeners.MSdkInitListener

internal interface AdSdk {

    fun init(
        applicationContext: Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    )
    fun getAccountId(): String
    fun setTimeoutMillis(timeoutMillis: Long)
    fun getTimeOutMillis(): Int
    fun setStoredAuctionResponse(storedAuctionResponse: String?)
    fun addStoredBidResponse(bidder: String, responseId: String)
    fun getStoredBidResponses(): Map<String, String>
    fun setLogLevel(level: LoggingLevel)
    fun isCompatibleWithGoogleMobileAds(version: String)
    fun shareGeoLocation(share: Boolean)
    fun isSharingGeoLocation(): Boolean
    fun setSubjectToGDPR(enable: Boolean)
    fun isSubjectToGDPR(): Boolean
    fun setGDPRConsentString(consentString: String?)
    fun setStoreUrl(storeUrl: String)
    fun setDomain(domain: String)
    fun setDebug(enable: Boolean)
}