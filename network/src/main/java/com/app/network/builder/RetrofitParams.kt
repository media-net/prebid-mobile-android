package com.app.network.builder

import okhttp3.Cache
import okhttp3.Interceptor

class RetrofitParams {
    var networkInterceptor: Interceptor? = null
    var apiInterceptors: List<Interceptor>? = null
    var retryOnConnectionFailure = false
    var cache: Cache? = null
    var readTimeout: Long = 0
    var connectTimeout: Long = 0
    var writeTimeout: Long = 0
}
