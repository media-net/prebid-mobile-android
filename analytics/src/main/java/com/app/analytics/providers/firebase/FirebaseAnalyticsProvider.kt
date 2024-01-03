package com.app.analytics.providers.firebase

import android.content.Context
import android.os.Bundle
import com.app.analytics.Event
import com.app.analytics.providers.AnalyticsProvider
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel
import com.app.analytics.utils.Constant
import com.app.logger.CustomLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class FirebaseAnalyticsProvider(
        val context: Context,
        private val analyticsBaseUrl: String
    ) : AnalyticsProvider {

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    companion object {
        private val TAG = FirebaseAnalyticsProvider::class.java.simpleName
        private val ERROR_TAG = "prebid_error_event"
        private val PARAM_ERROR = "param_error"
        private val PARAM_BASE_URL = "param_url"
        private val PARAM_TYPE = "param_type"
        private val PARAM_COMMIT_ID = "param_commit_id"
        private val PARAM_CR_ID = "param_cr_id"
        private val PREFIX = "ysdk_"

    }

    override val defaultParams = mutableMapOf<String, Any>()

    override fun getName() = Constant.Providers.PROVIDER_FIREBASE

    override suspend fun pushEvent(event: Event): Boolean {
        CustomLogger.debug(TAG, "pushing event to db: ${event.name}")
        if (event.baseUrl.isBlank()) {
            CustomLogger.error(TAG, "Invalid host url: Empty base url to push event")
            firebaseAnalytics.logEvent(ERROR_TAG) {
                param(PARAM_ERROR, "Empty Url in event: ${event.name} at ${event.timeStamp}")
            }
        }

        firebaseAnalytics.logEvent(PREFIX + event.name) {
            param(PARAM_BASE_URL, event.baseUrl)
            param(PARAM_CR_ID, event.params.get("crid") ?: "")
            param(PARAM_COMMIT_ID, event.params.get("commit_id") ?: "")
            param(PARAM_BASE_URL, event.baseUrl)
            param(PARAM_TYPE, event.type)
        }
        return true
    }

    override suspend fun pushEvents(events: List<Event>): Boolean {
        events.forEach {
            pushEvent(it)
        }
        return true
    }

    override suspend fun pushPixel(pixel: DefaultAnalyticsPixel): Boolean {
        CustomLogger.debug(TAG, "Sending pixel: $pixel")
        return true
    }

    override suspend fun pushPixels(pixels: List<DefaultAnalyticsPixel>): Boolean {
        pixels.forEach {
            pushPixel(it)
        }
        return true
    }

    override fun setDefaultParams(params: Map<String, Any>) {
        defaultParams.putAll(params)
    }

    override fun clean() {}

    fun Map<String, String>.toBundle(): Bundle {
        val bundle = Bundle()
        for ((key, value) in this) {
            bundle.putString(key, value)
        }
        return bundle
    }
}
