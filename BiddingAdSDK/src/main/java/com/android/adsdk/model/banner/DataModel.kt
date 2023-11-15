package com.android.adsdk.model.banner

class DataModel {
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
    var segments: ArrayList<SegmentModel> = ArrayList()

    fun addSegment(segmentModel: SegmentModel) {
        segments.add(segmentModel)
    }
}