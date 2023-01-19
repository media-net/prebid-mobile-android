package com.app.analytics

import android.content.Context
import com.app.analytics.events.Event
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.providers.AnalyticsProviderFactory
import com.app.analytics.utils.NetworkWatcher
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
            NetworkWatcher.init(context)
            addProviders(providers)
            isInitialised = true
            sendPendingEventIfAny()
            CustomLogger.debug(TAG, "Analytics SDK initialised")
        }
    }


    private fun addProviders(providers: List<AnalyticsProvider>) {
        config?.apply {
            AnalyticsProviderFactory.clear()
            if (config?.isTestMode == true) {
                CustomLogger.debug(TAG, "Debug Mode is on so only adding DebugAnalyticsProvider")
                AnalyticsProviderFactory.addDebugAnalytics()
                return
            }
            AnalyticsProviderFactory.addAllProvider(providers)
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

    fun updateConfig(config: Configuration) {
        this.config = config
        addProviders(customProviders)
    }


    class Configuration private constructor(
        val eventSamplingEnabled: Boolean,
        val isTestMode: Boolean,
        val analyticsUrl: String,
        val samplingMap: Map<String, Int>
    ) {
        data class Builder(
            private var eventSamplingEnabled: Boolean = false,
            private var isTestMode: Boolean = false,
            private var analyticsUrl: String = "",
            private var samplingMap: Map<String, Int> = mutableMapOf()
        ) {
            fun enableEventSampling(enable: Boolean, samplingMap: SamplingMap) = apply {
                eventSamplingEnabled = enable
                this.samplingMap = samplingMap.map
            }
            fun setTestMode(isTest: Boolean) = apply { isTestMode = isTest }
            fun setAnalyticsUrl(baseUrl: String) = apply { analyticsUrl = baseUrl }

            fun build(): Configuration = Configuration(
                eventSamplingEnabled = eventSamplingEnabled,
                isTestMode = isTestMode,
                analyticsUrl = analyticsUrl,
                samplingMap = samplingMap
            )
        }
    }

    fun clear() {
        analyticsScope.coroutineContext.cancelChildren()
        config = null
        pendingEvents.clear()
        AnalyticsProviderFactory.clear()
        NetworkWatcher.stop()
    }
}