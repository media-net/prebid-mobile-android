package com.app.analytics.providers.cached

import android.content.Context
import com.app.analytics.PushEventToServerService
import com.app.analytics.providers.cached.db.IAnalyticsEventRepository

class TimedSyncStrategy(
    private var context: Context,
    private val eventRepository: IAnalyticsEventRepository,
    pushService: PushEventToServerService)
    : EventSyncStrategy(pushService) {

    override fun initialise() {
        //TODO - we need to figure out how to pass repo and push service to worker
        EventDBSyncWorker.scheduleAnalyticsSync(context)
    }

    override fun clean() {
        EventDBSyncWorker.cancelAnalyticsSync(context)
    }
}