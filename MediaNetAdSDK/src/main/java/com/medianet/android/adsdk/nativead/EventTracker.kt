package com.medianet.android.adsdk.nativead

import com.medianet.android.adsdk.Util
import org.prebid.mobile.NativeEventTracker

class EventTracker(type: EventType, methods: ArrayList<EventTrackingMethods>) {

    enum class EventType {
        IMPRESSION,
        VIEWABLE_MRC50,
        VIEWABLE_MRC100,
        VIEWABLE_VIDEO50,
        CUSTOM
    }

    enum class EventTrackingMethods {
        IMAGE,
        JS,
        CUSTOM
    }

    private val nativeEventTracker = NativeEventTracker(Util.getPrebidEventType(type), Util.getPrebidTrackingMethodTypeArray(methods))

    fun setExt(extObject: Any?) {
        nativeEventTracker.setExt(extObject)
    }

    fun getPrebidEventTracker(): NativeEventTracker {
        return nativeEventTracker
    }
}