package com.medianet.android.adsdk

data class MSegmentObject(
    /**
     * ID of the data segment specific to the data provider.
     */
    var id: String? = null,

    /**
     * Name of the data segment specific to the data provider.
     */
    var name: String? = null,

    /**
     * String representation of the data segment value.
     */
    var value: String? = null
)
