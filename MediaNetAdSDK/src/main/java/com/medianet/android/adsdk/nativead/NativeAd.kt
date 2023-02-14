package com.medianet.android.adsdk.nativead

import com.app.logger.CustomLogger
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.medianet.android.adsdk.*
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_MSG
import com.medianet.android.adsdk.utils.Constants.SDK_ON_VACATION_LOG_TAG
import com.medianet.android.adsdk.utils.Util
import com.medianet.android.adsdk.utils.Util.getPrebidAssetFromNativeAdAsset
import com.medianet.android.adsdk.utils.Util.getPrebidEventTracker
import org.prebid.mobile.*

/**
 * Native Ad Class for both original and rendering types
 */
class NativeAd(adUnitId: String): Ad(NativeAdUnit("imp-prebid-banner-native-styles")) {
    // TODO Pass adUnitId to NativeAdUnit once it is configured
    private var mNativeAdUnit: NativeAdUnit = adUnit as NativeAdUnit

    override val adType: AdType = AdType.NATIVE

    /**
     * sets the context type for the native ad
     * which will in turn be sent in the bid request for auction call
     */
    fun setContextType(type: ContextType) {
        mNativeAdUnit.setContextType(Util.getPrebidContextType(type))
    }

    /**
     * sets the sub context type for the native ad
     * which will in turn be sent in the bid request for auction call
     */
    fun setContextSubType(type: ContextSubType) {
        mNativeAdUnit.setContextSubType(Util.getPrebidContextSubType(type))
    }

    /**
     * sets the placement type for the native ad
     * which will in turn be sent in the bid request for auction call
     */
    fun setPlacementType(placementType: PlacementType) {
        mNativeAdUnit.setPlacementType(Util.getPrebidPlacementType(placementType))
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
     * which will in turn be sent in the bid request for auction call
     */
    fun addEventTracker(tracker: EventTracker) {
        mNativeAdUnit.addEventTracker(getPrebidEventTracker(tracker))
    }

    /**
     * adds Assets for the native ad for
     * which will in turn be sent in the bid request for auction call
     */
    fun addAsset(asset: NativeAdAsset) {
        mNativeAdUnit.addAsset(getPrebidAssetFromNativeAdAsset(asset))
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
     * starts the bid auction call
     */
    fun fetchDemand(
        adRequest: AdManagerAdRequest,
        listener: GamEventListener
    ) {
        if(MediaNetAdSDK.isSdkOnVacation()){
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