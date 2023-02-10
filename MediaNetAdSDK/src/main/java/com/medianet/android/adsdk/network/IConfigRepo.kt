package com.medianet.android.adsdk.network

import android.content.Context
import com.medianet.android.adsdk.model.SdkConfiguration
import kotlinx.coroutines.flow.Flow

interface IConfigRepo {
    suspend fun getSDKConfig(cid: String): SdkConfiguration?
    fun getSDKConfigFlow(): Flow<SdkConfiguration?>
    suspend fun clearSdkConfig()
    suspend fun refreshSdkConfig(cid: String, context: Context)
}
