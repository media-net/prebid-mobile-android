package com.app.analytics.providers

interface IAnalyticsProviderConfig {
    fun getParam(key: String): String
}