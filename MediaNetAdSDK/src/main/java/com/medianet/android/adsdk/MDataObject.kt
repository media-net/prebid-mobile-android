package com.medianet.android.adsdk

class MDataObject {
    /**
     * Exchange-specific ID for the data provider.
     */
    var id: String? = null

    /**
     * Exchange-specific name for the data provider.
     */
    var name: String? = null

    /**
     * Segment objects are essentially key-value pairs that convey specific units of data.
     */
    var segments: ArrayList<MSegmentObject> = ArrayList()

    fun addSegment(mSegmentObject: MSegmentObject) {
        segments.add(mSegmentObject)
    }
}