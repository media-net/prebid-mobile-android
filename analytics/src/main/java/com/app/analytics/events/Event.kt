package com.app.analytics.events

data class Event(
     val name: String,
     val timeStamp: Long = System.currentTimeMillis(),
     val baseUrl: String = "",
     val params: Map<String, String> = mutableMapOf(),
     val type: String
)