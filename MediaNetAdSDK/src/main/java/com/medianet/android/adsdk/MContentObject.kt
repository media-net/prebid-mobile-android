package com.medianet.android.adsdk

class MContentObject {

    /**
     * ID uniquely identifying the content.
     */
    var id: String? = null

    /**
     * Episode number.
     */
    var episode: Int? = null

    /**
     * Content title.
     */
    var title: String? = null

    /**
     * Content series.
     */
    var series: String? = null

    /**
     * Content season.
     */
    var season: String? = null

    /**
     * Artist credited with the content.
     */
    var artist: String? = null

    /**
     * Genre that best describes the content.
     */
    var genre: String? = null

    /**
     * Album to which the content belongs; typically for audio.
     */
    var album: String? = null

    /**
     * International Standard Recording Code conforming to ISO- 3901.
     */
    var isrc: String? = null

    /**
     * URL of the content, for buy-side contextualization or review.
     */
    var url: String? = null

    /**
     * Array of IAB content categories that describe the content producer.
     */
    var categories: ArrayList<String> = emptyList<String>() as ArrayList<String>

    /**
     * Production quality.
     */
    var productionQuality: Int? = null

    /**
     * Type of content (game, video, text, etc.).
     */
    var context: Int? = null

    /**
     * Content rating (e.g., MPAA).
     */
    var contentRating: String? = null

    /**
     * User rating of the content (e.g., number of stars, likes, etc.).
     */
    var userRating: String? = null

    /**
     * Media rating per IQG guidelines.
     */
    var qaMediaRating: Int? = null

    /**
     * Comma separated list of keywords describing the content.
     */
    var keywords: MutableList<String> = mutableListOf()

    /**
     * Live stream. 0 = not live, 1 = content is live (e.g., stream, live blog).
     */
    var liveStream: Int? = null

    /**
     * Source relationship. 0 = indirect, 1 = direct.
     */
    var sourceRelationship: Int? = null

    /**
     * Length of content in seconds; appropriate for video or audio.
     */
    var length: Int? = null

    /**
     * Content language using ISO-639-1-alpha-2.
     */
    var language: String? = null

    /**
     * Indicator of whether or not the content is embeddable (e.g., an embeddable video player), where 0 = no, 1 = yes.
     */
    var embeddable: Int? = null

    /**
     * Additional content data.
     */
    var mDataObjects: ArrayList<MDataObject> = ArrayList()

    /**
     * This object defines the producer of the content in which the ad will be shown.
     */
    var producerObject: MProducerObject? = null

    fun addData(data: MDataObject) = mDataObjects.add(data)
    fun addDataList(dataList: List<MDataObject>) = mDataObjects.addAll(dataList)
    fun clearDataList() = mDataObjects.clear()

    fun getCommaSeparatedKeyWords(): String = keywords.joinToString(separator = ",")
}
