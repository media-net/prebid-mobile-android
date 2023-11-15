package com.android.adsdk.utils

import com.android.adsdk.model.StoredConfigs.StoredSdkConfig
import com.android.adsdk.model.sdkconfig.SdkConfiguration

internal object ConfigUtils {

    private fun parseConfigExpiryTime(headerValue: String?): Long? {
        return headerValue?.split(",")?.find { it.contains("max-age") }?.split("=")?.get(1)?.trim()?.toLongOrNull()
    }


    fun calculateConfigExpiryTime(statusCode: Int?, headerValue: String?): Long {
        return parseConfigExpiryTime(headerValue) ?: when (statusCode) {
            500 -> 300L //300 sec = 5 min
            else -> 120L // 120 sec = 2 min (For error codes 502, 503, 504)
        }
    }

    fun storedConfigToSdkConfig(storedConfig: StoredSdkConfig): SdkConfiguration? {
        // Data store will return default value of StoredSdkConfig initially n which configId is wmpty
        if (storedConfig.customerId.isNullOrBlank()) return null
        return SdkConfiguration(
            customerId = storedConfig.customerId,
            partnerId = storedConfig.partnerId,
            domainName = storedConfig.domainName,
            countryCode = storedConfig.countryCode,
            auctionTimeOutMillis = storedConfig.auctionTimeOutMillis,
            dummyCCrId = storedConfig.dummyCrId,
            projectEventPercentage = storedConfig.projectEventPercentage,
            opportunityEventPercentage = storedConfig.opportunityEventPercentage,
            shouldKillSDK = storedConfig.shouldKillSDK,
            bidRequestUrl = storedConfig.bidRequestUrl,
            projectEventUrl = storedConfig.projectEventUrl,
            opportunityEventUrl = storedConfig.opportunityEventUrl,
            dpfToCrIdMap = storedConfig.dpfToCrIdMapMap,
            configExpiryMillis = storedConfig.configExpiryMillis
        )
    }

    fun getLoggingPercentage(logValue: Double): Double {
        return if (logValue > 0) {
            100 / logValue
        } else {
            0.0
        }
    }
}