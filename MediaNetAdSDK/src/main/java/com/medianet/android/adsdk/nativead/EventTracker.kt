package com.medianet.android.adsdk.nativead

data class EventTracker(
    var type: EventType,
    var methods: ArrayList<EventTrackingMethods>,
    var extObject: Any? = null
) {
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
}
