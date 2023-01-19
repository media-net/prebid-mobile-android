package com.app.analytics.utils

import android.net.Uri
import com.app.analytics.Event
import com.app.analytics.providers.defaults.DefaultAnalyticsPixel
import com.app.analytics.providers.cached.db.EventDBEntity

object AnalyticsUtil {

    fun getDefaultAnalyticsPixel(event: Event, baseUrl: String): DefaultAnalyticsPixel {
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
        event.params.forEach { paramEntry ->
            uriBuilder.appendQueryParameter(paramEntry.key, paramEntry.value)
        }
        return DefaultAnalyticsPixel(
            name = event.name,
            pixel = uriBuilder.toString()
        )
    }

    fun getDefaultAnalyticsPixel(event: Event): DefaultAnalyticsPixel {
        val uriBuilder = Uri.parse(event.baseUrl).buildUpon()
        event.params.forEach { paramEntry ->
            uriBuilder.appendQueryParameter(paramEntry.key, paramEntry.value)
        }
        return DefaultAnalyticsPixel(
            name = event.name,
            pixel = uriBuilder.toString()
        )
    }

    fun DefaultAnalyticsPixel.toDbEntry(): EventDBEntity {
        return EventDBEntity(
            name = this.name,
            pixel = pixel
        )
    }

    fun EventDBEntity.toPixel(): DefaultAnalyticsPixel {
        return DefaultAnalyticsPixel(
            name = this.name,
            pixel = pixel
        )
    }
}
