package com.app.analytics

import android.content.Context
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.providers.AnalyticsProviderFactory
import com.app.logger.CustomLogger
import kotlinx.coroutines.*

object AnalyticsSDK {

    private val TAG = AnalyticsSDK::class.java.simpleName
    private var isInitialised = false
    private val pendingEvents = mutableListOf<Event>()
    private var config: Configuration? = null
    val analyticsScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var customProviders: List<AnalyticsProvider> = emptyList()

    fun init(context: Context, configuration: Configuration, providers: List<AnalyticsProvider> = mutableListOf()) {
        if (isInitialised.not()) {
            config = configuration
            customProviders = providers
            addProviders(context, providers)
            isInitialised = true
            sendPendingEventIfAny()
            CustomLogger.debug(TAG, "Analytics SDK initialised")
        }
    }


    private fun addProviders(context: Context, providers: List<AnalyticsProvider>) {
        config?.apply {
            AnalyticsProviderFactory.clear()
            AnalyticsProviderFactory.addAllProvider(providers)
            if (useCustomProvidersOnly) {
                CustomLogger.debug(TAG, "useCustomProvidersOnly is true so only adding given providers")
                return
            }

            if (config?.isDebugMode == true) {
                CustomLogger.debug(TAG, "Debug Mode is on so only adding DebugAnalyticsProvider")
                AnalyticsProviderFactory.addDebugAnalytics()
                return
            } else {
                if (analyticsUrl.isBlank()) {
                    CustomLogger.error(TAG, "analyticsUrl is not empty so not adding default/cached providers")
                    return
                }
                if (config?.cachingEnabled == true && (config?.syncIntervalInMinutes ?: 0) >= 0) {
                    CustomLogger.debug(TAG, "Caching enabled so CachedAnalyticsProvider debug provider")
                    AnalyticsProviderFactory.addCachedAnalytics(
                        context.applicationContext,
                        analyticsUrl,
                        syncIntervalInMinutes
                    )
                } else {
                    CustomLogger.debug(TAG, "Caching disabled so DefaultAnalyticsProvider debug provider")
                    AnalyticsProviderFactory.addDefaultAnalytics(config?.analyticsUrl ?: "")
                }
            }

        }
    }

    private fun sendPendingEventIfAny() {
        if (pendingEvents.isNotEmpty()) {
            CustomLogger.debug(TAG, "Analytics SDK initialised so pushing events of pending queue")
            pushEvents(pendingEvents)
            pendingEvents.clear()
        }
    }

    fun pushEvent(event: Event): Boolean {
        return if (isInitialised) {
            AnalyticsProviderFactory.getProviders().forEach { provider ->
                analyticsScope.launch {
                    CustomLogger.debug(TAG, "Pushing event: ${event.name} to provider: ${provider.getName()}")
                    if (skipEvent(event).not()) {
                        provider.pushEvent(event)
                    }
                }
            }
            true
        } else {
            CustomLogger.debug(TAG, "Analytics SDK not initialised yet so saving event in Queue: ${event.name}")
            pendingEvents.add(event)
            true
        }
    }

    private fun skipEvent(event: Event): Boolean {
        config?.apply {
            if (!eventSamplingEnabled) return false
            synchronized(this) {
                val loggingPer = samplingMap[event.type] ?: 0
                val random = (0..100).random()
                return (random <= loggingPer).not()
            }
        }
        return false
    }

    private fun pushEvents(events: List<Event>): Boolean {
        return if (isInitialised) {
            AnalyticsProviderFactory.getProviders().forEach { provider ->
                analyticsScope.launch {
                    provider.pushEvents(events)
                }
            }
            true
        } else false
    }

    fun getConfig() = config

    fun updateConfig(context: Context, config: Configuration) {
        this.config = config
        addProviders(context, customProviders)
    }


    class Configuration private constructor(
        val cachingEnabled: Boolean,
        val eventSamplingEnabled: Boolean,
        val loggingEnabled: Boolean,
        val isDebugMode: Boolean,
        val syncIntervalInMinutes: Long ,
        val analyticsUrl: String,
        val useCustomProvidersOnly: Boolean = false,
        val samplingMap: Map<String, Int>
    ) {
        data class Builder(
            private var cachingEnabled: Boolean = true,
            private var eventSamplingEnabled: Boolean = false,
            private var loggingEnabled: Boolean = true,
            private var isDebugMode: Boolean = false,
            private var syncIntervalInMinutes: Long = -1,
            private var analyticsUrl: String = "",
            private var useCustomProvidersOnly: Boolean = false,
            private var samplingMap: Map<String, Int> = mutableMapOf()
        ) {
            fun enableEventCaching(enable: Boolean, syncIntervalInMinutes: Long = 0) = apply {
                cachingEnabled = enable
                this.syncIntervalInMinutes = syncIntervalInMinutes
            }
            fun enableEventSampling(enable: Boolean, samplingMap: SamplingMap) = apply {
                eventSamplingEnabled = enable
                this.samplingMap = samplingMap.map
            }
            fun enableLogging(enable: Boolean) = apply { loggingEnabled = enable }
            fun setDebugMode(isDebug: Boolean) = apply { isDebugMode = isDebug }
            fun setSyncInterval(minutes: Long) = apply { syncIntervalInMinutes = minutes }
            fun setAnalyticsUrl(baseUrl: String) = apply { analyticsUrl = baseUrl }
            fun enableDefaultProviders(enable: Boolean) = apply { useCustomProvidersOnly = enable.not() }

            fun build(): Configuration = Configuration(
                cachingEnabled = cachingEnabled,
                eventSamplingEnabled = eventSamplingEnabled,
                loggingEnabled = loggingEnabled,
                isDebugMode = isDebugMode,
                syncIntervalInMinutes = syncIntervalInMinutes,
                analyticsUrl = analyticsUrl,
                useCustomProvidersOnly = useCustomProvidersOnly,
                samplingMap = samplingMap
            )
        }
    }

    fun clear() {
        analyticsScope.coroutineContext.cancelChildren()
        config = null
        pendingEvents.clear()
        AnalyticsProviderFactory.clear()
    }
}