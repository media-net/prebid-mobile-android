package com.app.logger

import com.app.logger.factory.CrashlyticsFactory


object CrashlyticsHelper {

    fun initialize() {
        createCrashlyticsSources()
    }

    //method to add multiple sources of CrashAnalytics
    private fun createCrashlyticsSources() {
        CrashlyticsFactory.setUp()
        CrashlyticsFactory.addSource(FirebaseCrashlyticsImpl())
    }

    fun log(message: String) {
        for (source in CrashlyticsFactory.getSources()) {
            source.log(message)
        }
    }

    fun logError(exception: Throwable) {
        for (source in CrashlyticsFactory.getSources()) {
            source.logException(exception)
        }
    }

    fun setCustomKey(key: String, value: String) {
        for (source in CrashlyticsFactory.getSources()) {
            source.setCustomKey(key, value)
        }
    }

    fun setUserIdentifier(userId: String) {
        for (source in CrashlyticsFactory.getSources()) {
            source.setUserId(userId)
        }
    }
}