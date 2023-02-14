package com.medianet.android.adsdk.network

import com.app.network.builder.RetrofitBuilder
import com.app.network.builder.RetrofitParams
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Factory Class for retrieving API Service Instances
 */
object NetworkComponentFactory {
    private lateinit var serverApiService: ServerApiService

    private val retrofitParams: RetrofitParams = RetrofitParams().apply {
        apiInterceptors = mutableListOf(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
        retryOnConnectionFailure = true
    }

    /**
     * returns API Service Instance along with retrofit interface to HTTP calls
     */
    fun getServerApiService(baseUrl: String, networkConfig: RetrofitParams? = null): ServerApiService {
        if (this::serverApiService.isInitialized.not()) {
            serverApiService = RetrofitBuilder(
                baseUrl = baseUrl,
                apiInterface = ServerApiService::class.java,
                retrofitParams = networkConfig ?: retrofitParams
            ).create()
        }
        return serverApiService
    }
}