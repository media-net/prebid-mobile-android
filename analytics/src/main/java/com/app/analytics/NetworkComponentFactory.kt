package com.app.analytics

import com.app.network.builder.RetrofitBuilder
import com.app.network.builder.RetrofitParams
import okhttp3.logging.HttpLoggingInterceptor

object NetworkComponentFactory {

    private lateinit var pushToServerService: PushEventToServerService

    private val retrofitParams: RetrofitParams = RetrofitParams().apply {
        networkInterceptor = if (BuildConfig.DEBUG) HttpLoggingInterceptor() else null
    }

    fun getPushToServerService(baseUrl: String, networkConfig: RetrofitParams? = null): PushEventToServerService  {
        if (this::pushToServerService.isInitialized.not()) {
            pushToServerService = RetrofitBuilder(
                baseUrl = baseUrl,
                apiInterface = PushEventToServerService::class.java,
                retrofitParams = networkConfig ?: retrofitParams
            ).create()
        }

        return pushToServerService
    }

}