package com.app.network

import retrofit2.HttpException

object Util {

    fun shouldRetryHttpCall(e: Exception): Boolean {
        return (e is HttpException && e.code() in 401..451).not()
    }

    fun isClientSideHttpErrorError(e: Exception): Boolean {
        return e is HttpException && e.code() in 401..451
    }
}
