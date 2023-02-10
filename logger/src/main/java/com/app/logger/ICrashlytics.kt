package com.app.logger

interface ICrashlytics {
    fun logException(throwable: Throwable)
    fun log(message: String)
    fun setCustomKey(key: String, value: String)
    fun setUserId(userId: String)
}
