package com.medianet.android.adsdk.network

import com.app.network.builder.RetrofitBuilder
import com.app.network.builder.RetrofitParams
import okhttp3.logging.HttpLoggingInterceptor

object NetworkComponentFactory {
    private lateinit var serverApiService: ServerApiService

    private val retrofitParams: RetrofitParams = RetrofitParams().apply {
        apiInterceptors = mutableListOf(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
        retryOnConnectionFailure = true
    }

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