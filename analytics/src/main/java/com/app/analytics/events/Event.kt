package com.app.analytics.events

abstract class Event{
     abstract var name: String
     val timeStamp = System.currentTimeMillis()
     abstract val params: MutableMap<String, Any>
}