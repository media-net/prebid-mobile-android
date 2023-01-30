package com.medianet.android.adsdk.network

import com.medianet.android.adsdk.model.ConfigResponse
import com.medianet.android.adsdk.network.ApiConstants.CONFIG_CALL_ENDPOINT
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ServerApiService {
    @GET(CONFIG_CALL_ENDPOINT)
    suspend fun getSdkConfig(@Path("cid") cid: String, @QueryMap queryMap: Map<String, String>): ConfigResponse
}