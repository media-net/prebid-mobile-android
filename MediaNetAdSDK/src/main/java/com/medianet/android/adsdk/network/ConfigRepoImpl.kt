package com.medianet.android.adsdk.network

import com.app.network.Either
import com.app.network.IFailure
import com.app.network.RetryPolicy
import com.app.network.wrapper.safeApiCall
import com.medianet.android.adsdk.BuildConfig
import com.medianet.android.adsdk.model.ConfigResponse
import com.medianet.android.adsdk.utils.Constants.KEY_CC
import com.medianet.android.adsdk.utils.Constants.KEY_DN
import com.medianet.android.adsdk.utils.Constants.KEY_UGD
import com.medianet.android.adsdk.utils.Constants.VALUE_MOBILE
import com.medianet.android.adsdk.utils.Constants.VALUE_US
import com.medianet.android.adsdk.utils.Util

class ConfigRepoImpl(private val serverApiService: ServerApiService?) : IConfigRepo {
    override suspend fun getSDKConfig(
        cid: String
    ): Either<IFailure, ConfigResponse?> {
        val configParams = mapOf(
            KEY_CC to VALUE_US,
            KEY_DN to BuildConfig.LIBRARY_PACKAGE_NAME,
            KEY_UGD to VALUE_MOBILE
        )
        return safeApiCall(
            apiCall = {
                serverApiService?.getSdkConfig(cid, configParams)
            },
            successTransform = {
                val configExpiry = Util.calculateConfigExpiryTime(it?.code(), it?.headers()?.get("Cache-Control"))
                it?.body()?.apply {
                    globalConfig.configExpiryInSec = configExpiry
                }
            },
            retryPolicy = RetryPolicy(maxTries = 0)
        )
    }
}
