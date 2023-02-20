package com.app.analytics

import com.app.logger.CustomLogger

class SamplingMap {

    companion object {
        private val TAG = SamplingMap::class.java.simpleName
    }
    val map: MutableMap<String, Int> = mutableMapOf()

    fun put(eventType: String, loggingPercentage: Int) {
        if (loggingPercentage < 0 || loggingPercentage > 100) {
            CustomLogger.error(TAG, "Logging percentage should be between 0-100")
            return
        }
        if (eventType.isBlank()) {
            CustomLogger.error(TAG, "Event type can not be blank")
            return
        }
        map[eventType] = loggingPercentage
    }
}
