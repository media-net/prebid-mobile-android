package com.medianet.android.adsdk.utils

import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.medianet.android.adsdk.*
import com.medianet.android.adsdk.base.AdType
import com.medianet.android.adsdk.base.MAdSize
import com.medianet.android.adsdk.ad.nativead.EventTracker
import com.medianet.android.adsdk.ad.nativead.NativeAd
import com.medianet.android.adsdk.base.MLogLevel
import com.medianet.android.adsdk.model.banner.ContentModel
import com.medianet.android.adsdk.model.banner.DataModel
import com.medianet.android.adsdk.model.banner.ProducerModel
import com.medianet.android.adsdk.model.banner.SegmentModel
import com.medianet.android.adsdk.ad.nativead.assets.DataAsset
import com.medianet.android.adsdk.ad.nativead.assets.ImageAsset
import com.medianet.android.adsdk.ad.nativead.assets.NativeAdAsset
import com.medianet.android.adsdk.ad.nativead.assets.TitleAsset
import org.prebid.mobile.*
import org.prebid.mobile.api.data.AdUnitFormat
import org.prebid.mobile.api.exceptions.AdException
import java.util.*
import com.medianet.android.adsdk.base.Error

object MapperUtils {

    fun ResultCode.mapResultCodeToError(): Error {
        return when (this) {
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

    fun MLogLevel.mapLogLevelToPrebidLogLevel(): PrebidMobile.LogLevel {
        return when (this) {
            MLogLevel.DEBUG -> PrebidMobile.LogLevel.DEBUG
            MLogLevel.ERROR -> PrebidMobile.LogLevel.ERROR
            MLogLevel.WARN -> PrebidMobile.LogLevel.WARN
            MLogLevel.NONE -> PrebidMobile.LogLevel.NONE
            else -> PrebidMobile.LogLevel.DEBUG
        }
    }

    fun ContentModel.mapContentModelToContentObject(): ContentObject {
        val contentObject = ContentObject()
        val dataObjectList = arrayListOf<DataObject>()
        apply {
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
            contentObject.producer = producerObject.mapProducerModelToProducerObject()
            for (data in dataModels) {
                dataObjectList.add(data.mapDataModelToDataObject())
            }
            contentObject.dataList = dataObjectList
        }
        return contentObject
    }

    private fun ProducerModel?.mapProducerModelToProducerObject(): ContentObject.ProducerObject? {
        if (this == null) return null
        val producerObject = ContentObject.ProducerObject()
        apply {
            producerObject.id = id
            producerObject.name = name
            producerObject.setCategories(categories)
            producerObject.domain = domain
        }
        return producerObject
    }

    private fun DataModel.mapDataModelToDataObject(): DataObject {
        val dataObject = DataObject()
        val segmentsList = arrayListOf<DataObject.SegmentObject>()
        apply {
            dataObject.id = id
            dataObject.name = name
            for (segment in segments) {
                segmentsList.add(segment.mapSegmentModelToSegmentObject())
            }
            dataObject.segments = segmentsList
        }
        return dataObject
    }

    private fun SegmentModel.mapSegmentModelToSegmentObject(): DataObject.SegmentObject {
        val segmentObject = DataObject.SegmentObject()
        apply {
            segmentObject.id = id
            segmentObject.name = name
            segmentObject.value = value
        }
        return segmentObject
    }

    fun LoadAdError.mapGamLoadAdErrorToError(): Error {
        return Error.GAM_LOAD_AD_ERROR.apply {
            errorCode = code
            errorMessage = message
        }
    }

    //Rendering Interstitial Ad format
    fun EnumSet<AdType>.mapInterstitialAdFormat(): EnumSet<AdUnitFormat> {
        val enumSetOfAdUnitFormat = EnumSet.noneOf(AdUnitFormat::class.java)
        for (format in this) {
            when (format) {
                AdType.DISPLAY -> enumSetOfAdUnitFormat.add(AdUnitFormat.DISPLAY)
                AdType.VIDEO -> enumSetOfAdUnitFormat.add(AdUnitFormat.VIDEO)
                else -> enumSetOfAdUnitFormat.add(null)
            }
        }
        return enumSetOfAdUnitFormat
    }

    //Ad Exception to Error class
    fun AdException?.mapAdExceptionToError(): Error {
        return when (this?.message) {
            AdException.INIT_ERROR -> Error.INIT_ERROR
            AdException.INVALID_REQUEST -> Error.INVALID_REQUEST
            AdException.INTERNAL_ERROR -> Error.INTERNAL_ERROR
            AdException.SERVER_ERROR -> Error.SERVER_ERROR
            AdException.THIRD_PARTY -> Error.THIRD_PARTY
            else -> Error.MISCELLANIOUS_ERROR
        }
    }

    //To convert AdSize
    fun AdSize.getPrebidAdSizeFromGAMAdSize(): org.prebid.mobile.AdSize {
        return org.prebid.mobile.AdSize(width, height)
    }

    private fun mapAdSizeToMAdSize(size: org.prebid.mobile.AdSize) =
        MAdSize(height = size.height, width = size.width)

    fun mapAdSizesToMAdSizes(adSizes: HashSet<org.prebid.mobile.AdSize>): List<MAdSize> {
        return adSizes.map {
            mapAdSizeToMAdSize(it)
        }.toList()
    }

    /* Native Ads */
    fun NativeAd.ContextType.getPrebidContextType(): NativeAdUnit.CONTEXT_TYPE {
        return when (this) {
            NativeAd.ContextType.CUSTOM -> NativeAdUnit.CONTEXT_TYPE.CUSTOM
            NativeAd.ContextType.PRODUCT -> NativeAdUnit.CONTEXT_TYPE.PRODUCT
            NativeAd.ContextType.SOCIAL_CENTRIC -> NativeAdUnit.CONTEXT_TYPE.SOCIAL_CENTRIC
            NativeAd.ContextType.CONTENT_CENTRIC -> NativeAdUnit.CONTEXT_TYPE.CONTENT_CENTRIC
            else -> NativeAdUnit.CONTEXT_TYPE.CUSTOM
        }
    }

    fun NativeAd.ContextSubType.getPrebidContextSubType(): NativeAdUnit.CONTEXTSUBTYPE {
        return when (this) {
            NativeAd.ContextSubType.ARTICLE -> NativeAdUnit.CONTEXTSUBTYPE.ARTICAL
            NativeAd.ContextSubType.AUDIO -> NativeAdUnit.CONTEXTSUBTYPE.AUDIO
            NativeAd.ContextSubType.APPLICATION_STORE -> NativeAdUnit.CONTEXTSUBTYPE.APPLICATION_STORE
            NativeAd.ContextSubType.CHAT_IM -> NativeAdUnit.CONTEXTSUBTYPE.CHAT_IM
            NativeAd.ContextSubType.CUSTOM -> NativeAdUnit.CONTEXTSUBTYPE.CUSTOM
            NativeAd.ContextSubType.EMAIL -> NativeAdUnit.CONTEXTSUBTYPE.EMAIL
            NativeAd.ContextSubType.GENERAL -> NativeAdUnit.CONTEXTSUBTYPE.GENERAL
            NativeAd.ContextSubType.GENERAL_SOCIAL -> NativeAdUnit.CONTEXTSUBTYPE.GENERAL_SOCIAL
            NativeAd.ContextSubType.IMAGE -> NativeAdUnit.CONTEXTSUBTYPE.IMAGE
            NativeAd.ContextSubType.SELLING -> NativeAdUnit.CONTEXTSUBTYPE.SELLING
            NativeAd.ContextSubType.PRODUCT_REVIEW_SITES -> NativeAdUnit.CONTEXTSUBTYPE.PRODUCT_REVIEW_SITES
            NativeAd.ContextSubType.USER_GENERATED -> NativeAdUnit.CONTEXTSUBTYPE.USER_GENERATED
            NativeAd.ContextSubType.VIDEO -> NativeAdUnit.CONTEXTSUBTYPE.VIDEO
            else -> NativeAdUnit.CONTEXTSUBTYPE.CUSTOM
        }
    }

    fun NativeAd.PlacementType.getPrebidPlacementType(): NativeAdUnit.PLACEMENTTYPE {
        return when (this) {
            NativeAd.PlacementType.CUSTOM -> NativeAdUnit.PLACEMENTTYPE.CUSTOM
            NativeAd.PlacementType.CONTENT_ATOMIC_UNIT -> NativeAdUnit.PLACEMENTTYPE.CONTENT_ATOMIC_UNIT
            NativeAd.PlacementType.CONTENT_FEED -> NativeAdUnit.PLACEMENTTYPE.CONTENT_FEED
            NativeAd.PlacementType.OUTSIDE_CORE_CONTENT -> NativeAdUnit.PLACEMENTTYPE.OUTSIDE_CORE_CONTENT
            NativeAd.PlacementType.RECOMMENDATION_WIDGET -> NativeAdUnit.PLACEMENTTYPE.RECOMMENDATION_WIDGET
            else -> NativeAdUnit.PLACEMENTTYPE.CUSTOM
        }
    }

    private fun ImageAsset.ImageType.getPrebidImageType(): NativeImageAsset.IMAGE_TYPE {
        return when (this) {
            ImageAsset.ImageType.ICON -> NativeImageAsset.IMAGE_TYPE.ICON
            ImageAsset.ImageType.MAIN -> NativeImageAsset.IMAGE_TYPE.MAIN
            ImageAsset.ImageType.CUSTOM -> NativeImageAsset.IMAGE_TYPE.CUSTOM
            else -> NativeImageAsset.IMAGE_TYPE.CUSTOM
        }
    }

    private fun DataAsset.DataType.getPrebidDataType(): NativeDataAsset.DATA_TYPE {
        return when (this) {
            DataAsset.DataType.DESC -> NativeDataAsset.DATA_TYPE.DESC
            DataAsset.DataType.DESC2 -> NativeDataAsset.DATA_TYPE.DESC2
            DataAsset.DataType.CTA_TEXT -> NativeDataAsset.DATA_TYPE.CTATEXT
            DataAsset.DataType.SPONSORED -> NativeDataAsset.DATA_TYPE.SPONSORED
            DataAsset.DataType.CUSTOM -> NativeDataAsset.DATA_TYPE.CUSTOM
            DataAsset.DataType.ADDRESS -> NativeDataAsset.DATA_TYPE.ADDRESS
            DataAsset.DataType.DISPLAY_URL -> NativeDataAsset.DATA_TYPE.DESPLAYURL
            DataAsset.DataType.DOWNLOADS -> NativeDataAsset.DATA_TYPE.DOWNLOADS
            DataAsset.DataType.LIKES -> NativeDataAsset.DATA_TYPE.LIKES
            DataAsset.DataType.PRICE -> NativeDataAsset.DATA_TYPE.PRICE
            DataAsset.DataType.PHONE -> NativeDataAsset.DATA_TYPE.PHONE
            DataAsset.DataType.RATING -> NativeDataAsset.DATA_TYPE.RATING
            DataAsset.DataType.SALE_PRICE -> NativeDataAsset.DATA_TYPE.SALEPRICE
            else -> NativeDataAsset.DATA_TYPE.CUSTOM
        }
    }

    private fun EventTracker.EventType.getPrebidEventType(): NativeEventTracker.EVENT_TYPE {
        return when (this) {
            EventTracker.EventType.CUSTOM -> NativeEventTracker.EVENT_TYPE.CUSTOM
            EventTracker.EventType.IMPRESSION -> NativeEventTracker.EVENT_TYPE.IMPRESSION
            EventTracker.EventType.VIEWABLE_MRC100 -> NativeEventTracker.EVENT_TYPE.VIEWABLE_MRC100
            EventTracker.EventType.VIEWABLE_MRC50 -> NativeEventTracker.EVENT_TYPE.VIEWABLE_MRC50
            EventTracker.EventType.VIEWABLE_VIDEO50 -> NativeEventTracker.EVENT_TYPE.VIEWABLE_VIDEO50
            else -> NativeEventTracker.EVENT_TYPE.CUSTOM
        }
    }

    private fun ArrayList<EventTracker.EventTrackingMethods>.getPrebidTrackingMethodTypeArray(): ArrayList<NativeEventTracker.EVENT_TRACKING_METHOD> {
        val methodsArray = arrayListOf<NativeEventTracker.EVENT_TRACKING_METHOD>()
        for (methodType in this) {
            methodsArray.add(
                when (methodType) {
                    EventTracker.EventTrackingMethods.CUSTOM -> NativeEventTracker.EVENT_TRACKING_METHOD.CUSTOM
                    EventTracker.EventTrackingMethods.IMAGE -> NativeEventTracker.EVENT_TRACKING_METHOD.IMAGE
                    EventTracker.EventTrackingMethods.JS -> NativeEventTracker.EVENT_TRACKING_METHOD.JS
                    else -> NativeEventTracker.EVENT_TRACKING_METHOD.CUSTOM
                }
            )
        }
        return methodsArray
    }

    fun NativeAdAsset.getPrebidAssetFromNativeAdAsset(): NativeAsset? {
        return when (this) {
            is TitleAsset -> {
                val titleAsset = NativeTitleAsset()
                titleAsset.setLength(this.length)
                titleAsset.isRequired = this.isRequired
                titleAsset.assetExt = this.assetExt
                titleAsset.titleExt = this.titleExt
                titleAsset
            }

            is ImageAsset -> {
                val imageAsset =
                    NativeImageAsset(this.width, this.height, this.minWidth, this.minHeight)
                imageAsset.imageType = this.type?.getPrebidImageType()
                imageAsset.isRequired = this.isRequired
                imageAsset.imageExt = this.imageExt
                imageAsset.assetExt = this.assetExt
                for (mime in this.mimes) {
                    imageAsset.addMime(mime)
                }
                imageAsset
            }

            is DataAsset -> {
                val dataAsset = NativeDataAsset()
                dataAsset.len = this.length
                dataAsset.isRequired = this.isRequired
                dataAsset.dataType = this.type?.getPrebidDataType()
                dataAsset.dataExt = this.dataExt
                dataAsset.assetExt = this.assetExt
                dataAsset
            }
            else -> {
                null
            }
        }
    }

    fun EventTracker.getPrebidEventTracker(): NativeEventTracker {
        return NativeEventTracker(
            this.type.getPrebidEventType(), this.methods.getPrebidTrackingMethodTypeArray()
        )
    }

}