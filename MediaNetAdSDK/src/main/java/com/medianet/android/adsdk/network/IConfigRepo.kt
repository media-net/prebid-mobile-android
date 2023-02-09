package com.medianet.android.adsdk.network

import com.app.network.Either
import com.app.network.IFailure
import com.medianet.android.adsdk.model.ConfigResponse

interface IConfigRepo {
    suspend fun getSDKConfig(cid: String): Either<IFailure, ConfigResponse?>
}