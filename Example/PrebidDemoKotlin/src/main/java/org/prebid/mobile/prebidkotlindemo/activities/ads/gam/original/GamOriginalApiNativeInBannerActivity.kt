package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.original

import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.Error
import com.medianet.android.adsdk.GamEventListener
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.nativead.*
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiNativeInBannerActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/unified_native_ad_unit"
        const val CONFIG_ID = "imp-prebid-banner-native-styles"
        const val STORED_RESPONSE = "response-prebid-banner-native-styles"
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

        // 4. Make a bid request to Prebid Server
        val request = AdManagerAdRequest.Builder().build()
        nativeAdUnit?.fetchDemand(gamView, request, object : GamEventListener {
            override fun onAdLoaded() {
                Log.d("Tushar", "onAdLoaded")
            }

            override fun onAdClicked() {
                Log.d("Tushar", "onAdClicked")
            }

            override fun onAdClosed() {
                Log.d("Tushar", "onAdClosed")
            }

            override fun onAdFailedToLoad(error: Error) {
                Log.d("Tushar", "Error code: ${error.errorCode}, message: ${error.errorMessage}")
            }

            override fun onAdOpened() {
                Log.d("Tushar", "onAdOpened")
            }

            override fun onAdImpression() {
                Log.d("Tushar", "onAdImpression")
            }

            override fun onSuccess(keywordMap: Map<String, String>?) {
                gamView.loadAd(request)
            }
        })
    }

    private fun addNativeAssets(adUnit: NativeAd?)  {
        // ADD ASSETS

        val title = TitleAsset()
        title.setLength(90)
        title.setRequired(true)
        adUnit?.addAsset(title)

        val icon = ImageAsset(20, 20, 20, 20)
        icon.setImageType(ImageAsset.ImageType.ICON)
        icon.setRequired(true)
        adUnit?.addAsset(icon)

        val image = ImageAsset(200, 200, 200, 200)
        image.setImageType(ImageAsset.ImageType.MAIN)
        image.setRequired(true)
        adUnit?.addAsset(image)

        val data = DataAsset()
        data.setLength(90)
        data.setDataType(DataAsset.DataType.SPONSORED)
        data.setRequired(true)
        adUnit?.addAsset(data)

        val body = DataAsset()
        body.setDataType(DataAsset.DataType.DESC)
        body.setRequired(true)
        adUnit?.addAsset(body)

        val cta = DataAsset()
        cta.setDataType(DataAsset.DataType.CTA_TEXT)
        cta.setRequired(true)
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
