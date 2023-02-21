package com.medianet.android.adsdk.ad.nativead

/**
 * tracker that tracks the ad based on various things like impressions etc
 */
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