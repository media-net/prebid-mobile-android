package com.app.analytics

import retrofit2.http.GET
import retrofit2.http.Url

interface PushEventToServerService {
    @GET
    suspend fun pushAnalyticsEvent(@Url pixel: String)
}