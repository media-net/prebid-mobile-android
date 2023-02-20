package com.medianet.android.adsdk

class MProducerObject {

    /**
     * Content producer or originator ID.
     */
    var id: String? = null

    /**
     * Content producer or originator name (e.g., “Warner Bros”).
     */
    var name: String? = null

    /**
     * Array of IAB content categories that describe the content producer.
     */
    var categories: ArrayList<String> = emptyList<String>() as ArrayList<String>

    /**
     * Highest level domain of the content producer (e.g., “producer.com”).
     */
    var domain: String? = null

    fun addCategory(category: String) = categories.add(category)
}
