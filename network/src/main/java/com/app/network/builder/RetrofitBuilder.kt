package com.app.network.builder

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder<T>(
    private val baseUrl: String,
    private val apiInterface: Class<T>,
    private val retrofitParams: RetrofitParams? = null,
    private val needScalarsFactory: Boolean = false
) {

    private val moshi =
        Moshi.Builder().build()
    private val okHttpClient by lazy { OkHttpClient.Builder() }
    private val retrofit by lazy {
        val mBaseUrl = if (baseUrl.endsWith("/")) baseUrl else baseUrl.plus("/")
        val builder = Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .client(okHttpClient.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
        if (needScalarsFactory) {
            builder.addConverterFactory(ScalarsConverterFactory.create())
        }
        builder.build()
    }

    init {
        initialize()
    }

    private fun initialize() {
        retrofitParams?.let { params ->
            val apiInterceptors = params.apiInterceptors
            if (!apiInterceptors.isNullOrEmpty()) {
                apiInterceptors.forEach {
                    okHttpClient.addInterceptor(it)
                }
            }
            if (params.connectTimeout != 0L) {
                okHttpClient.connectTimeout(params.connectTimeout, TimeUnit.SECONDS)
            }
            if (params.readTimeout != 0L) {
                okHttpClient.readTimeout(params.readTimeout, TimeUnit.SECONDS)
            }
            if (params.writeTimeout != 0L) {
                okHttpClient.writeTimeout(params.writeTimeout, TimeUnit.SECONDS)
            }
            okHttpClient.retryOnConnectionFailure(params.retryOnConnectionFailure)
            params.cache?.let {
                okHttpClient.cache(it)
            }
            params.networkInterceptor?.let {
                okHttpClient.addNetworkInterceptor(it)
            }
        }
    }

    fun create(): T {
        return retrofit.create(apiInterface)
    }
}
