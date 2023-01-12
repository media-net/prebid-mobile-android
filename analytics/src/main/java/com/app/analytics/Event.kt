package com.app.analytics

data class Event(
     val name: String,
     val timeStamp: Long = System.currentTimeMillis(),
     val params: Map<String, String> = mutableMapOf(),
     val type: String
)