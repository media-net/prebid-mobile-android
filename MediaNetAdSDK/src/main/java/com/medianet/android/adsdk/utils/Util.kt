package com.medianet.android.adsdk.utils

import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.medianet.android.adsdk.AdType
import com.medianet.android.adsdk.MAdSize
import com.medianet.android.adsdk.MContentObject
import com.medianet.android.adsdk.MDataObject
import com.medianet.android.adsdk.MLogLevel
import com.medianet.android.adsdk.MProducerObject
import com.medianet.android.adsdk.MSegmentObject
import com.medianet.android.adsdk.model.SdkConfiguration
import com.medianet.android.adsdk.model.StoredConfigs
import com.medianet.android.adsdk.nativead.DataAsset
import com.medianet.android.adsdk.nativead.EventTracker
import com.medianet.android.adsdk.nativead.ImageAsset
import com.medianet.android.adsdk.nativead.NativeAd
import com.medianet.android.adsdk.nativead.NativeAdAsset
import com.medianet.android.adsdk.nativead.TitleAsset
import java.util.EnumSet
import org.prebid.mobile.ContentObject
import org.prebid.mobile.ContentObject.ProducerObject
import org.prebid.mobile.DataObject
import org.prebid.mobile.DataObject.SegmentObject
import org.prebid.mobile.NativeAdUnit.CONTEXTSUBTYPE
import org.prebid.mobile.NativeAdUnit.CONTEXT_TYPE
import org.prebid.mobile.NativeAdUnit.PLACEMENTTYPE
import org.prebid.mobile.NativeAsset
import org.prebid.mobile.NativeDataAsset
import org.prebid.mobile.NativeDataAsset.DATA_TYPE
import org.prebid.mobile.NativeEventTracker
import org.prebid.mobile.NativeEventTracker.EVENT_TRACKING_METHOD
import org.prebid.mobile.NativeEventTracker.EVENT_TYPE
import org.prebid.mobile.NativeImageAsset
import org.prebid.mobile.NativeImageAsset.IMAGE_TYPE
import org.prebid.mobile.NativeTitleAsset
import org.prebid.mobile.PrebidMobile.LogLevel
import org.prebid.mobile.ResultCode
import org.prebid.mobile.api.data.AdUnitFormat
import org.prebid.mobile.api.exceptions.AdException

object Util {

    fun mapResultCodeToError(code: ResultCode): com.medianet.android.adsdk.Error {
        return when (code) {
            ResultCode.INVALID_ACCOUNT_ID -> com.medianet.android.adsdk.Error.INVALID_ACCOUNT_ID
            ResultCode.INVALID_CONFIG_ID -> com.medianet.android.adsdk.Error.INVALID_CONFIG_ID
            ResultCode.INVALID_HOST_URL -> com.medianet.android.adsdk.Error.INVALID_HOST_URL
            ResultCode.INVALID_SIZE -> com.medianet.android.adsdk.Error.INVALID_BANNER_SIZE
            ResultCode.INVALID_CONTEXT -> com.medianet.android.adsdk.Error.INVALID_CONTEXT
            ResultCode.INVALID_AD_OBJECT -> com.medianet.android.adsdk.Error.INVALID_AD_OBJECT
            ResultCode.NETWORK_ERROR -> com.medianet.android.adsdk.Error.NETWORK_ERROR
            ResultCode.TIMEOUT -> com.medianet.android.adsdk.Error.REQUEST_TIMEOUT
            ResultCode.NO_BIDS -> com.medianet.android.adsdk.Error.NO_BIDS
            ResultCode.PREBID_SERVER_ERROR -> com.medianet.android.adsdk.Error.PREBID_SERVER_ERROR
            ResultCode.INVALID_NATIVE_REQUEST -> com.medianet.android.adsdk.Error.INVALID_NATIVE_REQUEST
            else -> com.medianet.android.adsdk.Error.MISCELLANIOUS_ERROR
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

    fun mapGamLoadAdErrorToError(gamError: LoadAdError): com.medianet.android.adsdk.Error {
        return com.medianet.android.adsdk.Error.GAM_LOAD_AD_ERROR.apply {
            errorCode = gamError.code
            errorMessage = gamError.message
        }
    }

    // Rendering Interstitial Ad format
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

    // Ad Exception to Error class
    fun mapAdExceptionToError(adException: AdException?): com.medianet.android.adsdk.Error {
        return when (adException?.message) {
            AdException.INIT_ERROR -> com.medianet.android.adsdk.Error.INIT_ERROR
            AdException.INVALID_REQUEST -> com.medianet.android.adsdk.Error.INVALID_REQUEST
            AdException.INTERNAL_ERROR -> com.medianet.android.adsdk.Error.INTERNAL_ERROR
            AdException.SERVER_ERROR -> com.medianet.android.adsdk.Error.SERVER_ERROR
            AdException.THIRD_PARTY -> com.medianet.android.adsdk.Error.THIRD_PARTY
            else -> com.medianet.android.adsdk.Error.MISCELLANIOUS_ERROR
        }
    }

    // To convert AdSize
    fun getPrebidAdSizeFromGAMAdSize(adSize: AdSize): org.prebid.mobile.AdSize {
        return org.prebid.mobile.AdSize(adSize.width, adSize.height)
    }

    private fun mapAdSizeToMAdSize(size: org.prebid.mobile.AdSize) = MAdSize(height = size.height, width = size.width)

    fun mapAdSizesToMAdSizes(adSizes: HashSet<org.prebid.mobile.AdSize>): List<MAdSize> {
        return adSizes.map {
            mapAdSizeToMAdSize(it)
        }.toList()
    }

    /* Native Ads */
    fun getPrebidContextType(contextType: NativeAd.ContextType): CONTEXT_TYPE {
        return when (contextType) {
            NativeAd.ContextType.CUSTOM -> CONTEXT_TYPE.CUSTOM
            NativeAd.ContextType.PRODUCT -> CONTEXT_TYPE.PRODUCT
            NativeAd.ContextType.SOCIAL_CENTRIC -> CONTEXT_TYPE.SOCIAL_CENTRIC
            NativeAd.ContextType.CONTENT_CENTRIC -> CONTEXT_TYPE.CONTENT_CENTRIC
            else -> CONTEXT_TYPE.CUSTOM
        }
    }

    fun getPrebidContextSubType(contextSubType: NativeAd.ContextSubType): CONTEXTSUBTYPE {
        return when (contextSubType) {
            NativeAd.ContextSubType.ARTICLE -> CONTEXTSUBTYPE.ARTICAL
            NativeAd.ContextSubType.AUDIO -> CONTEXTSUBTYPE.AUDIO
            NativeAd.ContextSubType.APPLICATION_STORE -> CONTEXTSUBTYPE.APPLICATION_STORE
            NativeAd.ContextSubType.CHAT_IM -> CONTEXTSUBTYPE.CHAT_IM
            NativeAd.ContextSubType.CUSTOM -> CONTEXTSUBTYPE.CUSTOM
            NativeAd.ContextSubType.EMAIL -> CONTEXTSUBTYPE.EMAIL
            NativeAd.ContextSubType.GENERAL -> CONTEXTSUBTYPE.GENERAL
            NativeAd.ContextSubType.GENERAL_SOCIAL -> CONTEXTSUBTYPE.GENERAL_SOCIAL
            NativeAd.ContextSubType.IMAGE -> CONTEXTSUBTYPE.IMAGE
            NativeAd.ContextSubType.SELLING -> CONTEXTSUBTYPE.SELLING
            NativeAd.ContextSubType.PRODUCT_REVIEW_SITES -> CONTEXTSUBTYPE.PRODUCT_REVIEW_SITES
            NativeAd.ContextSubType.USER_GENERATED -> CONTEXTSUBTYPE.USER_GENERATED
            NativeAd.ContextSubType.VIDEO -> CONTEXTSUBTYPE.VIDEO
            else -> CONTEXTSUBTYPE.CUSTOM
        }
    }

    fun getPrebidPlacementType(placementType: NativeAd.PlacementType): PLACEMENTTYPE {
        return when (placementType) {
            NativeAd.PlacementType.CUSTOM -> PLACEMENTTYPE.CUSTOM
            NativeAd.PlacementType.CONTENT_ATOMIC_UNIT -> PLACEMENTTYPE.CONTENT_ATOMIC_UNIT
            NativeAd.PlacementType.CONTENT_FEED -> PLACEMENTTYPE.CONTENT_FEED
            NativeAd.PlacementType.OUTSIDE_CORE_CONTENT -> PLACEMENTTYPE.OUTSIDE_CORE_CONTENT
            NativeAd.PlacementType.RECOMMENDATION_WIDGET -> PLACEMENTTYPE.RECOMMENDATION_WIDGET
            else -> PLACEMENTTYPE.CUSTOM
        }
    }

    private fun getPrebidImageType(imageType: ImageAsset.ImageType): IMAGE_TYPE {
        return when (imageType) {
            ImageAsset.ImageType.ICON -> IMAGE_TYPE.ICON
            ImageAsset.ImageType.MAIN -> IMAGE_TYPE.MAIN
            ImageAsset.ImageType.CUSTOM -> IMAGE_TYPE.CUSTOM
            else -> IMAGE_TYPE.CUSTOM
        }
    }

    private fun getPrebidDataType(dataType: DataAsset.DataType): DATA_TYPE {
        return when (dataType) {
            DataAsset.DataType.DESC -> DATA_TYPE.DESC
            DataAsset.DataType.DESC2 -> DATA_TYPE.DESC2
            DataAsset.DataType.CTA_TEXT -> DATA_TYPE.CTATEXT
            DataAsset.DataType.SPONSORED -> DATA_TYPE.SPONSORED
            DataAsset.DataType.CUSTOM -> DATA_TYPE.CUSTOM
            DataAsset.DataType.ADDRESS -> DATA_TYPE.ADDRESS
            DataAsset.DataType.DISPLAY_URL -> DATA_TYPE.DESPLAYURL
            DataAsset.DataType.DOWNLOADS -> DATA_TYPE.DOWNLOADS
            DataAsset.DataType.LIKES -> DATA_TYPE.LIKES
            DataAsset.DataType.PRICE -> DATA_TYPE.PRICE
            DataAsset.DataType.PHONE -> DATA_TYPE.PHONE
            DataAsset.DataType.RATING -> DATA_TYPE.RATING
            DataAsset.DataType.SALE_PRICE -> DATA_TYPE.SALEPRICE
            else -> DATA_TYPE.CUSTOM
        }
    }

    private fun getPrebidEventType(eventType: EventTracker.EventType): EVENT_TYPE {
        return when (eventType) {
            EventTracker.EventType.CUSTOM -> EVENT_TYPE.CUSTOM
            EventTracker.EventType.IMPRESSION -> EVENT_TYPE.IMPRESSION
            EventTracker.EventType.VIEWABLE_MRC100 -> EVENT_TYPE.VIEWABLE_MRC100
            EventTracker.EventType.VIEWABLE_MRC50 -> EVENT_TYPE.VIEWABLE_MRC50
            EventTracker.EventType.VIEWABLE_VIDEO50 -> EVENT_TYPE.VIEWABLE_VIDEO50
            else -> EVENT_TYPE.CUSTOM
        }
    }

    private fun getPrebidTrackingMethodTypeArray(methods: ArrayList<EventTracker.EventTrackingMethods>): ArrayList<EVENT_TRACKING_METHOD> {
        val methodsArray = arrayListOf<EVENT_TRACKING_METHOD>()
        for (methodType in methods) {
            methodsArray.add(
                when (methodType) {
                    EventTracker.EventTrackingMethods.CUSTOM -> EVENT_TRACKING_METHOD.CUSTOM
                    EventTracker.EventTrackingMethods.IMAGE -> EVENT_TRACKING_METHOD.IMAGE
                    EventTracker.EventTrackingMethods.JS -> EVENT_TRACKING_METHOD.JS
                    else -> EVENT_TRACKING_METHOD.CUSTOM
                }
            )
        }
        return methodsArray
    }

    fun getPrebidAssetFromNativeAdAsset(asset: NativeAdAsset): NativeAsset? {
        return when (asset) {
            is TitleAsset -> {
                val titleAsset = NativeTitleAsset()
                titleAsset.setLength(asset.length)
                titleAsset.isRequired = asset.isRequired
                titleAsset.assetExt = asset.assetExt
                titleAsset.titleExt = asset.titleExt
                titleAsset
            }

            is ImageAsset -> {
                val imageAsset = NativeImageAsset(asset.width, asset.height, asset.minWidth, asset.minHeight)
                imageAsset.imageType = asset.type?.let { getPrebidImageType(it) }
                imageAsset.isRequired = asset.isRequired
                imageAsset.imageExt = asset.imageExt
                imageAsset.assetExt = asset.assetExt
                for (mime in asset.mimes) {
                    imageAsset.addMime(mime)
                }
                imageAsset
            }

            is DataAsset -> {
                val dataAsset = NativeDataAsset()
                dataAsset.len = asset.length
                dataAsset.isRequired = asset.isRequired
                dataAsset.dataType = asset.type?.let { getPrebidDataType(it) }
                dataAsset.dataExt = asset.dataExt
                dataAsset.assetExt = asset.assetExt
                dataAsset
            }
            else -> {
                null
            }
        }
    }

    fun getPrebidEventTracker(eventTracker: EventTracker): NativeEventTracker {
        return NativeEventTracker(getPrebidEventType(eventTracker.type), getPrebidTrackingMethodTypeArray(eventTracker.methods))
    }

    private fun parseConfigExpiryTime(headerValue: String?): Long? {
        return headerValue?.split(",")?.find { it.contains("max-age") }?.split("=")?.get(1)?.trim()?.toLongOrNull()
    }

    fun calculateConfigExpiryTime(statusCode: Int?, headerValue: String?): Long {
        return parseConfigExpiryTime(headerValue) ?: when (statusCode) {
            500 -> 300L // 300 sec = 5 min
            else -> 120L // 120 sec = 2 min (For error codes 502, 503, 504)
        }
    }

    fun storedConfigToSdkConfig(storedConfig: StoredConfigs.StoredSdkConfig): SdkConfiguration? {
        // Data store will return default value of StoredSdkConfig initially n which configId is wmpty
        if (storedConfig.customerId.isNullOrBlank()) return null
        return SdkConfiguration(
            customerId = storedConfig.customerId,
            partnerId = storedConfig.partnerId,
            domainName = storedConfig.domainName,
            countryCode = storedConfig.countryCode,
            auctionTimeOutMillis = storedConfig.auctionTimeOutMillis,
            dummyCCrId = storedConfig.dummyCrId,
            projectEventPercentage = storedConfig.projectEventPercentage,
            opportunityEventPercentage = storedConfig.opportunityEventPercentage,
            shouldKillSDK = storedConfig.shouldKillSDK,
            bidRequestUrl = storedConfig.bidRequestUrl,
            projectEventUrl = storedConfig.projectEventUrl,
            opportunityEventUrl = storedConfig.opportunityEventUrl,
            dpfToCrIdMap = storedConfig.dpfToCrIdMapMap
        )
    }
}
