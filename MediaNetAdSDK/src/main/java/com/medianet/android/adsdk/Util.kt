package com.medianet.android.adsdk

import org.prebid.mobile.ContentObject
import org.prebid.mobile.ContentObject.ProducerObject
import org.prebid.mobile.DataObject
import org.prebid.mobile.PrebidMobile.LogLevel
import org.prebid.mobile.ResultCode

object Util {

    fun mapResultCodeToError(code: ResultCode): Error {
        return when (code) {
            ResultCode.INVALID_ACCOUNT_ID -> Error.INVALID_ACCOUNT_ID
            ResultCode.INVALID_CONFIG_ID -> Error.INVALID_CONFIG_ID
            ResultCode.INVALID_HOST_URL -> Error.INVALID_HOST_URL
            ResultCode.INVALID_SIZE -> Error.INVALID_BANNER_SIZE
            ResultCode.INVALID_CONTEXT -> Error.INVALID_CONTEXT
            ResultCode.INVALID_AD_OBJECT -> Error.INVALID_AD_OBJECT
            ResultCode.NETWORK_ERROR -> Error.NETWORK_ERROR
            ResultCode.TIMEOUT -> Error.REQUEST_TIMEOUT
            ResultCode.NO_BIDS -> Error.NO_BIDS
            ResultCode.PREBID_SERVER_ERROR -> Error.PREBID_SERVER_ERROR
            ResultCode.INVALID_NATIVE_REQUEST -> Error.INVALID_NATIVE_REQUEST
            else -> Error.MISCELLANIOUS_ERROR
        }
    }

    fun mapLogLevelToPrebidLogLevel(level: MLogLevel): LogLevel {
        return when (level) {
            MLogLevel.DEBUG -> LogLevel.DEBUG
            MLogLevel.DEBUG -> LogLevel.DEBUG
            MLogLevel.ERROR -> LogLevel.ERROR
            MLogLevel.WARN -> LogLevel.WARN
            MLogLevel.NONE -> LogLevel.NONE
            else -> LogLevel.DEBUG
        }
    }

    fun mapMDataObjectToContentObject(mDataObject: MContentObject): ContentObject {
        val contentObject = ContentObject()
        mDataObject.apply {
            contentObject.id = id
            contentObject.episode = episode
            contentObject.title = title
            contentObject.series = series
            contentObject.season = season
            contentObject.artist = artist
            contentObject.genre = genre
            contentObject.album = album
            contentObject.isrc = isrc
            contentObject.url = url
            contentObject.categories = categories
            contentObject.productionQuality = productionQuality
            contentObject.context = context
            contentObject.contentRating = contentRating
            contentObject.userRating = userRating
            contentObject.qaMediaRating = qaMediaRating
            contentObject.keywords = getCommaSeparatedKeyWords()
            contentObject.liveStream = liveStream
            contentObject.sourceRelationship = sourceRelationship
            contentObject.length = length
            contentObject.language = language
            contentObject.producer = mapMProducerObjectToProducerObject(producerObject)
            //TODO - when we will create DataObject
            //contentObject.dataList = dataObjects
            return contentObject
        }
    }

    fun mapMProducerObjectToProducerObject(mProducerObject: MProducerObject?): ProducerObject? {
        if (mProducerObject == null) return null
        val producerObject = ProducerObject()
        mProducerObject?.apply {
            producerObject.id = id
            producerObject.name = name
            producerObject.setCategories(categories)
            producerObject.domain = domain
        }
        return producerObject
    }
}