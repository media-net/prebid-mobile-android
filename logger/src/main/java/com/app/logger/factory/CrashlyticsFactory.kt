package com.app.logger.factory

import com.app.logger.ICrashlytics

object CrashlyticsFactory {
    private lateinit var crashlyticsSources: MutableList<ICrashlytics>

    fun setUp() {
        crashlyticsSources = mutableListOf()
    }
    /**
     * method for adding source of crashlytics
     */
    fun addSource(source: ICrashlytics) {
        crashlyticsSources.add(source)
    }

    fun getSources(): List<ICrashlytics> {
        return crashlyticsSources
    }

}