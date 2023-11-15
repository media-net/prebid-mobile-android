package com.android.adsdk.network.repository

import android.content.Context
import androidx.datastore.core.DataStore
import com.android.adsdk.model.StoredConfigs
import com.android.adsdk.model.sdkconfig.ConfigResponse
import com.android.adsdk.model.sdkconfig.SdkConfiguration
import com.android.adsdk.network.SDKConfigSyncWorker
import com.android.adsdk.network.ServerApiService
import com.android.adsdk.utils.ConfigUtils
import com.android.adsdk.utils.Constants.KEY_DN
import com.app.logger.CustomLogger
import com.app.network.RetryPolicy
import com.app.network.wrapper.safeApiCall
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

/**
* repository class for fetching sdk config
*/
internal class ConfigRepoImpl(private val serverApiService: ServerApiService?, private val configDataStore: DataStore<StoredConfigs.StoredSdkConfig>):
    IConfigRepo {

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
            ConfigUtils.storedConfigToSdkConfig(it)
        }

    /**
     * fetches the last sdk config emitted by flow
     * @param cid is the config id of publisher
     * @return the sdk config data
     */
    override suspend fun getSDKConfig(
        cid: String
    ): SdkConfiguration? {
        return sdkConfigFlow.last()
    }

    override fun getSDKConfigFlow(): Flow<SdkConfiguration?> {
        return sdkConfigFlow
    }

    /**
     * clears sdk config stored in the datastore
     */
    override suspend fun clearSdkConfig() {
        configDataStore.updateData { config ->
            config.toBuilder().apply {
                clear()
            }.build()
        }
    }


    /**
     * fetches config from server
     * @param cid is the config id of publisher
     * @param context specifies the context of application where MediaNetAdSdk has been integrated
     */
    override suspend fun refreshSdkConfig(cid: String, context: Context) {
        val configParams = mapOf(
            KEY_DN to context.packageName
        )
        val serverConfigResult = safeApiCall(
            apiCall = {
                serverApiService?.getSdkConfig(cid, configParams)
            },
            successTransform = {
                val configExpiry = ConfigUtils.calculateConfigExpiryTime(it?.code(), it?.headers()?.get("Cache-Control"))
                it?.body()?.apply {
                    globalConfig.configExpiryInSec = configExpiry
                }
            },
            retryPolicy = RetryPolicy(maxTries = 0)
        )

        if (serverConfigResult.isSuccess) {
            CustomLogger.debug(TAG, "config fetch from server is successful")
            serverConfigResult.successValue()?.let {
                updateSdkConfig(it)
            }
        } else {
            CustomLogger.error(TAG, "config call fails: ${serverConfigResult.errorValue()?.errorModel?.errorMessage}")
            CustomLogger.debug(TAG, "scheduling config fetch after 2 min")
            SDKConfigSyncWorker.scheduleConfigFetch(context, 120L)
        }

    }


    /**
     * updates the config in the data store
     * @param serverConfig is the config response from server fetch
     */
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
                projectEventPercentage = ConfigUtils.getLoggingPercentage(serverConfig.logPercentage.projectEvent).toInt()
                opportunityEventPercentage = ConfigUtils.getLoggingPercentage(serverConfig.logPercentage.opportunityEvent).toInt()
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