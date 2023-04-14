package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.original

import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.base.Error
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.ad.nativead.EventTracker
import com.medianet.android.adsdk.ad.nativead.NativeAd
import com.medianet.android.adsdk.ad.nativead.assets.DataAsset
import com.medianet.android.adsdk.ad.nativead.assets.ImageAsset
import com.medianet.android.adsdk.ad.nativead.assets.TitleAsset
import com.medianet.android.adsdk.base.listeners.OnBidCompletionListener
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiNativeInBannerActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/unified_native_ad_unit"
        const val CONFIG_ID = "imp-prebid-banner-native-styles"
        const val STORED_RESPONSE = "response-prebid-banner-native-styles"
        val TAG = GamOriginalApiNativeInBannerActivity::class.java.name
    }

    private var nativeAdUnit: NativeAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()
    }

    private fun createAd() {
        // 1. Create Ad unit
        nativeAdUnit = NativeAd(AD_UNIT_ID)
        nativeAdUnit?.setContextType(NativeAd.ContextType.SOCIAL_CENTRIC)
        nativeAdUnit?.setPlacementType(NativeAd.PlacementType.CONTENT_FEED)
        nativeAdUnit?.setContextSubType(NativeAd.ContextSubType.GENERAL_SOCIAL)

        // 2. Configure Native Assets and Trackers
        addNativeAssets(nativeAdUnit)

        // 3. Create GAM Ad View
        val gamView = AdManagerAdView(this)
        gamView.adUnitId = AD_UNIT_ID
        gamView.setAdSizes(AdSize.FLUID)
        adWrapperView.addView(gamView)

        // 4. Make a bid request to Server
        val request = AdManagerAdRequest.Builder().build()
        nativeAdUnit?.fetchDemandForAd(request, object : OnBidCompletionListener {

            override fun onSuccess(keywordMap: Map<String, String>?) {
                gamView.loadAd(request)
            }

            override fun onError(error: Error) {
                Log.e(TAG, "Error: code: ${error.errorCode}, message: ${error.errorMessage}")
                gamView.loadAd(request)
            }
        })
    }

    private fun addNativeAssets(adUnit: NativeAd?)  {
        // ADD ASSETS

        val title = TitleAsset(90)
        title.isRequired = true
        adUnit?.addAsset(title)

        val icon = ImageAsset(20, 20, 20, 20, ImageAsset.ImageType.ICON)
        icon.isRequired = true
        adUnit?.addAsset(icon)

        val image = ImageAsset(200, 200, 200, 200, ImageAsset.ImageType.MAIN)
        image.isRequired = true
        adUnit?.addAsset(image)

        val data = DataAsset(DataAsset.DataType.SPONSORED, 90)
        data.isRequired = true
        adUnit?.addAsset(data)

        val body = DataAsset(DataAsset.DataType.DESC)
        body.isRequired = true
        adUnit?.addAsset(body)

        val cta = DataAsset(DataAsset.DataType.CTA_TEXT)
        cta.isRequired = true
        adUnit?.addAsset(cta)

        // ADD EVENT TRACKERS

        val methods = ArrayList<EventTracker.EventTrackingMethods>()
        methods.add(EventTracker.EventTrackingMethods.IMAGE)

        try {
            val tracker = EventTracker(EventTracker.EventType.IMPRESSION, methods)
            adUnit?.addEventTracker(tracker)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAdUnit?.stopAutoRefresh()
    }

}
