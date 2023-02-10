package com.app.analytics.providers

import android.content.Context
import com.app.analytics.DbComponentFactory
import com.app.analytics.NetworkComponentFactory
import com.app.analytics.providers.cached.CachedAnalyticsProvider
import com.app.analytics.providers.debug.DebugAnalyticsProvider
import com.app.analytics.providers.defaults.DefaultAnalyticsProvider

object AnalyticsProviderFactory {

    private val providers: MutableSet<AnalyticsProvider> = mutableSetOf()

    fun addProvider(provider: AnalyticsProvider): Boolean {
        return providers.add(provider)
    }

    fun addAllProvider(provider: List<AnalyticsProvider>): Boolean {
        return providers.addAll(provider)
    }

    fun removeProvider(provider: AnalyticsProvider): Boolean {
        val ifRemovedAny = providers.removeAll {
            it.getName() == provider.getName()
        }
        return ifRemovedAny
    }

    fun getProviders(): List<AnalyticsProvider> {
        return providers.toList()
    }

    fun getProvider(providerName: String): AnalyticsProvider? {
        return providers.firstOrNull { it.getName() == providerName }
    }

    fun addDefaultAnalytics(baseUrl: String) {
        val pushService = NetworkComponentFactory.getPushToServerService(baseUrl)
        val defaultProvider = DefaultAnalyticsProvider(baseUrl, pushService)
        providers.add(defaultProvider)
    }

    fun addCachedAnalytics(applicationContext: Context, baseUrl: String, syncIntervalInMinutes: Long) {
        val provider = getCachedProvider(applicationContext, baseUrl, syncIntervalInMinutes)
        providers.add(provider)
    }

    fun getCachedProvider(applicationContext: Context, baseUrl: String, syncIntervalInMinutes: Long): CachedAnalyticsProvider {
        val pushService = NetworkComponentFactory.getPushToServerService(baseUrl)
        val eventDbRepo = DbComponentFactory.getEventDbRepository(applicationContext)
        val provider = CachedAnalyticsProvider(applicationContext, baseUrl, eventDbRepo, pushService, syncIntervalInMinutes)
        return provider
    }

    fun addDebugAnalytics() {
        providers.add(DebugAnalyticsProvider())
    }

    fun clear() {
        providers.forEach {
            it.clean()
        }
        providers.clear()
    }
}
