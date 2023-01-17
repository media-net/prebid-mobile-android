package com.medianet.android.adsdk.nativead

import com.medianet.android.adsdk.Ad
import com.medianet.android.adsdk.AdType
import com.medianet.android.adsdk.Util
import org.prebid.mobile.*

class NativeAd(adUnitId: String): Ad() {
    // TODO Pass adUnitId to NativeAdUnit once it is configured
    private var mNativeAdUnit: NativeAdUnit = NativeAdUnit("imp-prebid-banner-native-styles")

    override val adUnit: AdUnit = mNativeAdUnit
    override val adType: AdType = AdType.NATIVE

    fun setContextType(type: ContextType) {
        mNativeAdUnit.setContextType(Util.getPrebidContextType(type))
        val title = NativeTitleAsset()
    }

    fun setContextSubType(type: ContextSubType) {
        mNativeAdUnit.setContextSubType(Util.getPrebidContextSubType(type))
    }

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

    fun addEventTracker(tracker: EventTracker?) {
        mNativeAdUnit.addEventTracker(tracker?.getPrebidEventTracker())
    }

    fun addAsset(asset: NativeAdAsset?) {
        mNativeAdUnit.addAsset(asset?.getPrebidAsset())
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
}