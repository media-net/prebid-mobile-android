package com.android.adsdk.model.sdkconfig

import android.os.Build
import com.android.adsdk.AdSDKManager
import com.android.adsdk.BuildConfig
import org.prebid.mobile.PrebidMobile

/**
 *  mapper class for ConfigResponse.kt class
 */
internal data class SdkConfiguration(
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
    val sdkVersion: String = BuildConfig.VERSION_NAME,
    val osVersion: String = Build.VERSION.RELEASE,
    val prebidVersion: String = PrebidMobile.SDK_VERSION,
    val isSubjectToGDPR: Boolean = AdSDKManager.isSubjectToGDPR() ?: false,
) {
    fun getCrId(dfpAdId: String): String {
        return dpfToCrIdMap[dfpAdId] ?: dummyCCrId
    }

    fun isConfigExpired() = ((System.currentTimeMillis() - timeStamp) >= configExpiryMillis)
}
