package com.medianet.android.adsdk.network

import com.app.network.builder.RetrofitBuilder
import com.app.network.builder.RetrofitParams
import okhttp3.logging.HttpLoggingInterceptor

/**
 * factory class for retrieving api service instances
 */
object NetworkComponentFactory {
    private lateinit var serverApiService: ServerApiService

    private val retrofitParams: RetrofitParams = RetrofitParams().apply {
        apiInterceptors = mutableListOf(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
        retryOnConnectionFailure = true
    }

    /**
     * returns api service instance along with retrofit interface to http calls
     * @param baseUrl is the base url in the http call
     * @param networkConfig are the config parameters to be added to the RetrofitBuilder
     * @return service instance to make an api call
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
