package com.medianet.android.adsdk.network.repository

import android.content.Context
import com.medianet.android.adsdk.model.sdkconfig.SdkConfiguration
import kotlinx.coroutines.flow.Flow

/**
 * repository interface for sdk config
 */
internal interface IConfigRepo {
    suspend fun getSDKConfig(cid: String): SdkConfiguration?
    fun getSDKConfigFlow(): Flow<SdkConfiguration?>
    suspend fun clearSdkConfig()
    suspend fun refreshSdkConfig(cid: String, context: Context)
}