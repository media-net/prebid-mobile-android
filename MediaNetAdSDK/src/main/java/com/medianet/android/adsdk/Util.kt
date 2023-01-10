package com.medianet.android.adsdk

import com.google.android.gms.ads.LoadAdError
import org.prebid.mobile.ContentObject
import org.prebid.mobile.ContentObject.ProducerObject
import org.prebid.mobile.DataObject
import org.prebid.mobile.DataObject.SegmentObject
import org.prebid.mobile.PrebidMobile.LogLevel
import org.prebid.mobile.ResultCode
import org.prebid.mobile.api.data.AdUnitFormat
import org.prebid.mobile.api.exceptions.AdException
import java.util.*

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
            MLogLevel.ERROR -> LogLevel.ERROR
            MLogLevel.WARN -> LogLevel.WARN
            MLogLevel.NONE -> LogLevel.NONE
            else -> LogLevel.DEBUG
        }
    }

    fun mapMContentObjectToContentObject(mContentObject: MContentObject): ContentObject {
        val contentObject = ContentObject()
        val dataObjectList = arrayListOf<DataObject>()
        mContentObject.apply {
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
            for (data in mDataObjects) {
                dataObjectList.add(mapMDataObjectToDataObject(data))
            }
            contentObject.dataList = dataObjectList
        }
        return contentObject
    }

    private fun mapMProducerObjectToProducerObject(mProducerObject: MProducerObject?): ProducerObject? {
        if (mProducerObject == null) return null
        val producerObject = ProducerObject()
        mProducerObject.apply {
            producerObject.id = id
            producerObject.name = name
            producerObject.setCategories(categories)
            producerObject.domain = domain
        }
        return producerObject
    }

    fun mapMDataObjectToDataObject(mDataObject: MDataObject): DataObject {
        val dataObject = DataObject()
        val segmentsList = arrayListOf<SegmentObject>()
        mDataObject.apply {
            dataObject.id = id
            dataObject.name = name
            for (segment in segments) {
                segmentsList.add(mapMSegmentObjectToSegmentObject(segment))
            }
            dataObject.segments = segmentsList
        }
        return dataObject
    }

    private fun mapMSegmentObjectToSegmentObject(mSegmentObject: MSegmentObject): SegmentObject {
        val segmentObject = SegmentObject()
        mSegmentObject.apply {
            segmentObject.id = id
            segmentObject.name = name
            segmentObject.value = value
        }
        return segmentObject
    }

    fun mapGamLoadAdErrorToError(gamError: LoadAdError): Error {
        return  Error.GAM_LOAD_AD_ERROR.apply {
            errorCode = gamError.code
            errorMessage = gamError.message
        }
    }

    //Rendering Interstitial Ad format
    fun mapInterstitialAdFormat(enumSetOfAdFormat: EnumSet<AdType>): EnumSet<AdUnitFormat> {
        val enumSetOfAdUnitFormat = EnumSet.noneOf(AdUnitFormat::class.java)
        for (format in enumSetOfAdFormat) {
            when (format) {
                AdType.DISPLAY -> enumSetOfAdUnitFormat.add(AdUnitFormat.DISPLAY)
                AdType.VIDEO -> enumSetOfAdUnitFormat.add(AdUnitFormat.VIDEO)
                else -> enumSetOfAdUnitFormat.add(null)
            }
        }
        return enumSetOfAdUnitFormat
    }

    //Ad Exception to Error class
    fun mapAdExceptionToError(adException: AdException?): Error {
        return when (adException?.message) {
            AdException.INIT_ERROR -> Error.INIT_ERROR
            AdException.INVALID_REQUEST -> Error.INVALID_REQUEST
            AdException.INTERNAL_ERROR -> Error.INTERNAL_ERROR
            AdException.SERVER_ERROR -> Error.SERVER_ERROR
            AdException.THIRD_PARTY -> Error.THIRD_PARTY
            else -> Error.MISCELLANIOUS_ERROR
        }
    }
}