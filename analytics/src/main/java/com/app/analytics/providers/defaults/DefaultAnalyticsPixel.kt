package com.app.analytics.providers.defaults

import androidx.annotation.Keep

@Keep
data class DefaultAnalyticsPixel(
    val name: String,
    val pixel: String
)
