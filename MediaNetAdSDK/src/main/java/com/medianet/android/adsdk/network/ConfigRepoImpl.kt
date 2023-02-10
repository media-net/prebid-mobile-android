package com.medianet.android.adsdk.network

import android.content.Context
import androidx.datastore.core.DataStore
import com.app.logger.CustomLogger
import com.app.network.RetryPolicy
import com.app.network.wrapper.safeApiCall
import com.medianet.android.adsdk.BuildConfig
import com.medianet.android.adsdk.model.ConfigResponse
import com.medianet.android.adsdk.model.SdkConfiguration
import com.medianet.android.adsdk.model.StoredConfigs
import com.medianet.android.adsdk.utils.Constants.KEY_CC
import com.medianet.android.adsdk.utils.Constants.KEY_DN
import com.medianet.android.adsdk.utils.Constants.KEY_UGD
import com.medianet.android.adsdk.utils.Constants.VALUE_MOBILE
import com.medianet.android.adsdk.utils.Constants.VALUE_US
import com.medianet.android.adsdk.utils.Util
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import java.io.IOException

class ConfigRepoImpl(private val serverApiService: ServerApiService?, private val configDataStore: DataStore<StoredConfigs.StoredSdkConfig>) : IConfigRepo {

    companion object {
        private val TAG = ConfigRepoImpl::class.java.name
    }

    private val sdkConfigFlow: Flow<SdkConfiguration?> = configDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                CustomLogger.error(TAG, "Error reading sdk config from data store.", exception)
                emit(StoredConfigs.StoredSdkConfig.getDefaultInstance())
            } else {
                throw exception
            }
        }
        .map {
            Util.storedConfigToSdkConfig(it)
        }

    override suspend fun getSDKConfig(
        cid: String
    ): SdkConfiguration? {
        return sdkConfigFlow.last()
    }

    override fun getSDKConfigFlow(): Flow<SdkConfiguration?> {
        return sdkConfigFlow
    }

    override suspend fun clearSdkConfig() {
        configDataStore.updateData { config ->
            config.toBuilder().apply {
                clear()
            }.build()
        }
    }

    override suspend fun refreshSdkConfig(cid: String, context: Context) {
        val configParams = mapOf(
            KEY_CC to VALUE_US,
            KEY_DN to BuildConfig.LIBRARY_PACKAGE_NAME,
            KEY_UGD to VALUE_MOBILE
        )
        val serverConfigResult = safeApiCall(
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

        if (serverConfigResult.isSuccess) {
            CustomLogger.error(TAG, "config fetch from server is successful")
            serverConfigResult.successValue()?.let {
                updateSdkConfig(it)
            }
        } else {
            CustomLogger.error(TAG, "config call fails: ${serverConfigResult.errorValue()?.errorModel?.errorMessage}")
            CustomLogger.debug(TAG, "scheduling config fetch after 2 min")
            SDKConfigSyncWorker.scheduleConfigFetch(context, 120L)
        }
    }

    private suspend fun updateSdkConfig(serverConfig: ConfigResponse) {
        CustomLogger.debug(TAG, "updating sdk config from server in datastore")
        val crIdMap = mutableMapOf<String, String>()
        serverConfig.crIds.map { item ->
            crIdMap.put(item.dfpAdUnitId, item.crId)
        }

        configDataStore.updateData { config ->
            config.toBuilder().apply {
                clear()
                customerId = serverConfig.pub.cId
                partnerId = serverConfig.pub.partnerId
                domainName = serverConfig.targeting.domainName
                countryCode = serverConfig.targeting.countryCode
                auctionTimeOutMillis = serverConfig.timeout.auctionTimeout.toLong()
                dummyCrId = serverConfig.dummyCrId.crId
                projectEventPercentage = serverConfig.logPercentage.projectEvent.toInt()
                opportunityEventPercentage = serverConfig.logPercentage.opportunityEvent.toInt()
                shouldKillSDK = serverConfig.publisherConfig.killSwitch
                bidRequestUrl = serverConfig.urls.auctionLayerUrl
                projectEventUrl = serverConfig.urls.projectEventUrl
                opportunityEventUrl = serverConfig.urls.opportunityEventUrl
                configExpiryMillis = serverConfig.globalConfig.configExpiryInSec?.times(1000) ?: -1
                timeStamp = System.currentTimeMillis()
                clearDpfToCrIdMap()
                putAllDpfToCrIdMap(crIdMap)
            }.build()
        }
    }
}
