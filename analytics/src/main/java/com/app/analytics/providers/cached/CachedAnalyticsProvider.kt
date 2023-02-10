package com.app.analytics.providers.cached

import android.content.Context
import com.app.analytics.Event
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository
import com.app.analytics.providers.cached.sync_strategy.EventSyncStrategy
import com.app.analytics.providers.cached.sync_strategy.ImmediateSyncStrategy
import com.app.analytics.providers.cached.sync_strategy.TimedSyncStrategy
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel
import com.app.analytics.utils.AnalyticsUtil
import com.app.analytics.utils.AnalyticsUtil.toDbEntry
import com.app.analytics.utils.Constant
import com.app.logger.CustomLogger

class CachedAnalyticsProvider(
    val context: Context,
    private val analyticsBaseUrl: String,
    private val eventRepository: IAnalyticsEventRepository,
    val pushService: PushEventToServerService,
    val syncIntervalInMinutes: Long = 0
) : AnalyticsProvider {

    companion object {
        private val TAG = CachedAnalyticsProvider::class.java.simpleName
    }

    private var syncStrategy: EventSyncStrategy? = null

    override val defaultParams = mutableMapOf<String, Any>()
    override fun getName() = Constant.Providers.PROVIDER_DEFAULT

    override fun initialise() {
        syncStrategy = if (syncIntervalInMinutes > 15) {
            TimedSyncStrategy(context, eventRepository, pushService)
        } else {
            ImmediateSyncStrategy(context, eventRepository, pushService)
        }
        syncStrategy?.initialise()
    }

    override suspend fun pushEvent(event: Event): Boolean {
        CustomLogger.debug(TAG, "pushing event to db: ${event.name}")
        if (analyticsBaseUrl.isBlank() && event.baseUrl.isBlank()) {
            CustomLogger.error(TAG, "Invalid host url: Empty base url to push event")
        }
        val url = event.baseUrl.ifBlank { analyticsBaseUrl }
        val dbPixel = AnalyticsUtil.getDefaultAnalyticsPixel(event = event, baseUrl = url).toDbEntry()
        eventRepository.insert(dbPixel)
        return true
    }

    override suspend fun pushEvents(events: List<Event>): Boolean {
        var allEventsPushed = true
        events.forEach {
            allEventsPushed = allEventsPushed && pushEvent(it)
        }
        return allEventsPushed
    }

    override suspend fun pushPixel(pixel: DefaultAnalyticsPixel): Boolean {
        eventRepository.insert(pixel.toDbEntry())
        return true
    }

    override suspend fun pushPixels(pixels: List<DefaultAnalyticsPixel>): Boolean {
        var allEventsPushed = true
        pixels.forEach {
            allEventsPushed = allEventsPushed && pushPixel(it)
        }
        return allEventsPushed
    }

    override fun setDefaultParams(params: Map<String, Any>) {
        defaultParams.putAll(params)
    }

    override fun clean() {
        syncStrategy?.clean()
        syncStrategy = null
    }
}
