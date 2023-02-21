package com.medianet.android.adsdk.ad.nativead

import com.app.logger.CustomLogger
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.medianet.android.adsdk.*
import com.medianet.android.adsdk.base.Ad
import com.medianet.android.adsdk.base.AdType
import com.medianet.android.adsdk.base.listeners.GamEventListener
import com.medianet.android.adsdk.base.listeners.OnBidCompletionListener
import com.medianet.android.adsdk.ad.nativead.assets.NativeAdAsset
import com.medianet.android.adsdk.base.Error
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidAssetFromNativeAdAsset
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidContextSubType
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidContextType
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidEventTracker
import com.medianet.android.adsdk.utils.MapperUtils.getPrebidPlacementType
import org.prebid.mobile.*

/**
 * native ad class for both original and rendering types of loading an ad
 */
class NativeAd(adUnitId: String) : Ad(NativeAdUnit(adUnitId)) {
    private var mNativeAdUnit: NativeAdUnit = adUnit as NativeAdUnit

    override val adType: AdType = AdType.NATIVE

    /**
     * sets the context type for the native ad
     * which will in turn be sent in the request for bid request call
     * @param type specifies the context type like PRODUCT or CUSTOM or CONTENT_CENTRIC etc
     */
    fun setContextType(type: ContextType) {
        mNativeAdUnit.setContextType(type.getPrebidContextType())
    }

    /**
     * sets the sub context type for the native ad
     * which will in turn be sent in the request for bid request call
     * @param type specifies the sub context type like GENERAL or ARTICLE or VIDEO or AUDIO
     */
    fun setContextSubType(type: ContextSubType) {
        mNativeAdUnit.setContextSubType(type.getPrebidContextSubType())
    }

    /**
     * sets the placement type for the native ad
     * which will in turn be sent in the request for bid request call
     * @param placementType specifies how the placement of ad should be
     */
    fun setPlacementType(placementType: PlacementType) {
        mNativeAdUnit.setPlacementType(placementType.getPrebidPlacementType())
    }

    fun setPlacementCount(placementCount: Int) {
        mNativeAdUnit.setPlacementCount(placementCount)
    }

    fun setSeq(seq: Int) {
        mNativeAdUnit.setSeq(seq)
    }

    fun setAUrlSupport(support: Boolean) {
        mNativeAdUnit.setAUrlSupport(support)
    }

    fun setDUrlSupport(support: Boolean) {
        mNativeAdUnit.setDUrlSupport(support)
    }

    fun setPrivacy(privacy: Boolean) {
        mNativeAdUnit.setPrivacy(privacy)
    }

    fun setExt(jsonObject: Any?) {
        mNativeAdUnit.setExt(jsonObject)
    }

    /**
     * adds event tracker for the native ad
     * which will in turn be sent in the request for bid request call
     * @param tracker is the event tracker added to the bid request
     */
    fun addEventTracker(tracker: EventTracker) {
        mNativeAdUnit.addEventTracker(tracker.getPrebidEventTracker())
    }

    /**
     * adds assets for the native ad for
     * which will in turn be sent in the request for bid request call
     * @param asset specifies the ad asset to be added to native ad
     */
    fun addAsset(asset: NativeAdAsset) {
        mNativeAdUnit.addAsset(asset.getPrebidAssetFromNativeAdAsset())
    }

    enum class ContextType(private var id: Int) {
        CONTENT_CENTRIC(1),
        SOCIAL_CENTRIC(2),
        PRODUCT(3),
        CUSTOM(500)
    }

    enum class ContextSubType(private var id: Int) {
        GENERAL(10),
        ARTICLE(11),
        VIDEO(12),
        AUDIO(13),
        IMAGE(14),
        USER_GENERATED(15),
        GENERAL_SOCIAL(20),
        EMAIL(21),
        CHAT_IM(22),
        SELLING(30),
        APPLICATION_STORE(31),
        PRODUCT_REVIEW_SITES(32),
        CUSTOM(500)
    }

    enum class PlacementType(private var id: Int) {
        CONTENT_FEED(1),
        CONTENT_ATOMIC_UNIT(2),
        OUTSIDE_CORE_CONTENT(3),
        RECOMMENDATION_WIDGET(4),
        CUSTOM(500)
    }

    /**
     * starts the bid request call
     * @param adRequest is the ad request for ad manager
     * @param listener listens to GAM events
     */
    fun fetchDemand(
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {
        if (MediaNetAdSDK.isSdkOnVacation()) {
            CustomLogger.error(SDK_ON_VACATION_LOG_TAG, SDK_ON_VACATION_LOG_MSG)
            return
        }


        fetchDemand(adRequest, object : OnBidCompletionListener {
            override fun onSuccess(keywordMap: Map<String, String>?) {
                listener.onSuccess()
            }

            override fun onError(error: Error) {
                listener.onError(error)
            }
        })
    }
}