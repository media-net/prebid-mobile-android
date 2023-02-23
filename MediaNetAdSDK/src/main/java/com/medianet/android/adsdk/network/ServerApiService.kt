package com.medianet.android.adsdk.network

import com.medianet.android.adsdk.model.sdkconfig.ConfigResponse
import com.medianet.android.adsdk.network.ApiConstants.CONFIG_CALL_ENDPOINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * service interface to make http call to the endpoint
 */
internal interface ServerApiService {
    @GET(CONFIG_CALL_ENDPOINT)
    suspend fun getSdkConfig(@Path("cid") cid: String, @QueryMap queryMap: Map<String, String>): Response<ConfigResponse>
}