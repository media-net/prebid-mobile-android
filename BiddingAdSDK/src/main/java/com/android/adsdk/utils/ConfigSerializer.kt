package com.android.adsdk.utils

import androidx.datastore.core.Serializer
import com.android.adsdk.model.StoredConfigs.StoredSdkConfig
import com.app.logger.CustomLogger
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * serializer class to read and write sdk config from datastore
 */
internal object ConfigSerializer : Serializer<StoredSdkConfig> {
    override val defaultValue: StoredSdkConfig = StoredSdkConfig.getDefaultInstance()
    const val TAG = "ConfigSerializer"

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): StoredSdkConfig {
        try {
            return StoredSdkConfig.parseFrom(input)
        } catch (exception: IOException) {
            CustomLogger.error(TAG, "Error while reading config from data store: ${exception.message}")
            exception.printStackTrace()
            return defaultValue
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: StoredSdkConfig, output: OutputStream) = t.writeTo(output)
}
