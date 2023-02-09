package com.medianet.android.adsdk

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.medianet.android.adsdk.model.StoredConfigs.StoredSdkConfig
import java.io.InputStream
import java.io.OutputStream

object ConfigSerializer : Serializer<StoredSdkConfig> {
    override val defaultValue: StoredSdkConfig = StoredSdkConfig.getDefaultInstance()

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): StoredSdkConfig {
        try {
            return StoredSdkConfig.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: StoredSdkConfig, output: OutputStream) = t.writeTo(output)
}
