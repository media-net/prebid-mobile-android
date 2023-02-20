package com.medianet.android.adsdk.model

import com.medianet.android.adsdk.BuildConfig

/**
 *  mapper class for ConfigResponse.kt class
 */
data class SdkConfiguration(
    val customerId: String,
    val partnerId: String,
    val domainName: String,
    val countryCode: String,
    val auctionTimeOutMillis: Long,
    val dpfToCrIdMap: MutableMap<String, String>,
    val dummyCCrId: String,
    val projectEventPercentage: Int,
    val opportunityEventPercentage: Int,
    val shouldKillSDK: Boolean,
    val bidRequestUrl: String,
    val projectEventUrl: String,
    val opportunityEventUrl: String,
    val configExpiryMillis: Long = -1,
    val timeStamp: Long = System.currentTimeMillis(),
    val sdkVersion: String = BuildConfig.VERSION_NAME
) {
    fun getCrId(dfpAdId: String): String {
        return dpfToCrIdMap[dfpAdId] ?: dummyCCrId
    }

    fun isConfigExpired() = ((System.currentTimeMillis() - timeStamp) >= configExpiryMillis)

}