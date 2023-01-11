package com.app.analytics.providers.cached

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData

class EventsLifecycleOwner: LifecycleOwner {

    private val mLifecycleRegistry = LifecycleRegistry(this)

    fun startListening() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun stopListening() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    fun removeObserver(observer: LifecycleObserver) {
        mLifecycleRegistry.removeObserver(observer)
    }
}