package com.medianet.android.adsdk

import com.medianet.android.adsdk.model.ConfigResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ServerApiService {
    @GET("/adserving/sdk/v1/adservingview/supply/{cid}")
    suspend fun getSdkConfig(@Path("cid") cid: String, @QueryMap queryMap: Map<String, String>): ConfigResponse
}