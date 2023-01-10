package com.app.analytics.providers

object AnalyticsProviderFactory {

    private val providers: MutableList<AnalyticsProvider> = mutableListOf()

    fun addProvider(provider: AnalyticsProvider): Boolean {
        val alreadyAvailable = providers.any {
            it.getName() == provider.getName()
        }
        return if (alreadyAvailable.not()) {
            providers.add(provider)
            true
        } else {
            false
        }
    }

    fun removeProvider(provider: AnalyticsProvider): Boolean {
        val ifRemovedAny = providers.removeAll {
            it.getName() == provider.getName()
        }
        return ifRemovedAny
    }

    fun getProviders(): List<AnalyticsProvider> {
        return providers
    }

    fun getProvider(providerName: String): AnalyticsProvider? {
        return providers.firstOrNull { it.getName() == providerName }
    }
}