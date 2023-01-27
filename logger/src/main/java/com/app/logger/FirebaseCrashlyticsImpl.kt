package com.app.logger

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashlyticsImpl : ICrashlytics {

    private val firBaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    override fun logException(throwable: Throwable) {
        firBaseCrashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        firBaseCrashlytics.log(message)
    }

    // for configuring userid/or other keys for logging
    override fun setCustomKey(key: String, value: String) {
        firBaseCrashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        firBaseCrashlytics.setUserId(userId)
    }

}