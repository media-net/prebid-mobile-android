package com.medianet.android.adsdk

import android.util.Log
import androidx.datastore.core.DataStore
import com.medianet.android.adsdk.model.SdkConfiguration
import com.medianet.android.adsdk.model.StoredConfigs.StoredSdkConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class ConfigRepository(private val configDataStore: DataStore<StoredSdkConfig>) {

    companion object {
        private val TAG = ConfigRepository::class.java.name
    }

    private val sdkConfigFlow: Flow<StoredSdkConfig> = configDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sdk config from data store.", exception)
                emit(StoredSdkConfig.getDefaultInstance())
            } else {
                throw exception
            }
        }

    fun getConfigFlow() = sdkConfigFlow

    suspend fun updateSdkConfig(configuration: SdkConfiguration) {
        configDataStore.updateData { config ->
            config.toBuilder().apply {
                clear()
                customerId = configuration.customerId
                partnerId = configuration.partnerId
                domainName = configuration.domainName
                countryCode = configuration.countryCode
                auctionTimeOutMillis = configuration.auctionTimeOutMillis
                dummyCrId = configuration.dummyCCrId
                projectEventPercentage = configuration.projectEventPercentage
                opportunityEventPercentage = configuration.opportunityEventPercentage
                shouldKillSDK = configuration.shouldKillSDK
                bidRequestUrl = configuration.bidRequestUrl
                projectEventUrl = configuration.projectEventUrl
                opportunityEventUrl = configuration.opportunityEventUrl
                clearDpfToCrIdMap()
                putAllDpfToCrIdMap(config.dpfToCrIdMapMap)
            }.build()
        }
    }

    suspend fun clearSdkConfig() {
        configDataStore.updateData { config ->
            config.toBuilder().apply {
                clear()
            }.build()
        }
    }
}

