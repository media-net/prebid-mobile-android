package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.rendering

import MediaNetAdSDK
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.adsdk.ad.nativead.EventTracker
import com.android.adsdk.ad.nativead.NativeAd
import com.android.adsdk.ad.nativead.NativeInAppAd
import com.android.adsdk.ad.nativead.assets.DataAsset
import com.android.adsdk.ad.nativead.assets.ImageAsset
import com.android.adsdk.ad.nativead.assets.TitleAsset
import com.android.adsdk.ad.nativead.listeners.NativeAdEventListener
import com.android.adsdk.ad.nativead.listeners.NativeAdListener
import com.android.adsdk.base.Error
import com.android.adsdk.base.listeners.OnBidCompletionListener
import com.android.adsdk.utils.AdUtils
import com.android.adsdk.utils.ImageUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.formats.OnAdManagerAdViewLoadedListener
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import com.google.android.gms.ads.nativead.NativeCustomFormatAd.OnCustomFormatAdLoadedListener
import org.prebid.mobile.prebidkotlindemo.R
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamRenderingApiNativeActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/apollo_custom_template_native_ad_unit"
        const val CONFIG_ID = "imp-prebid-banner-native-styles"
        const val STORED_RESPONSE = "response-prebid-banner-native-styles"
        const val CUSTOM_FORMAT_ID = "11934135"
        val TAG = GamRenderingApiNativeActivity::class.java.name
    }

    private var adView: AdManagerAdView? = null
    private var unifiedNativeAd: com.google.android.gms.ads.nativead.NativeAd? = null
    private var adUnit: NativeAd? = null
    private lateinit var adLoader: AdLoader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()
    }

    private fun createAd() {
        adUnit = configureNativeAdUnit()
        val adRequest = AdManagerAdRequest.Builder().build()
        adLoader = createAdLoader(adWrapperView)
        adUnit?.fetchDemandForAd(adRequest, object : OnBidCompletionListener {

            override fun onSuccess(keywordMap: Map<String, String>?) {
                adLoader.loadAd(adRequest)
            }

            override fun onError(error: Error) {
                Log.e(TAG, "Error: code: ${error.errorCode}, message: ${error.errorMessage}")
                adLoader.loadAd(adRequest)
            }
        })
    }

    private fun configureNativeAdUnit(): NativeAd {
        val adUnit = NativeAd(CONFIG_ID)
        adUnit.setContextType(NativeAd.ContextType.SOCIAL_CENTRIC)
        adUnit.setPlacementType(NativeAd.PlacementType.CONTENT_FEED)
        adUnit.setContextSubType(NativeAd.ContextSubType.GENERAL_SOCIAL)
        val methods = ArrayList<EventTracker.EventTrackingMethods>()
        methods.add(EventTracker.EventTrackingMethods.IMAGE)
        methods.add(EventTracker.EventTrackingMethods.JS)
        try {
            val tracker = EventTracker(EventTracker.EventType.IMPRESSION, methods)
            adUnit.addEventTracker(tracker)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val title = TitleAsset()
        title.length = 90
        title.isRequired = true
        adUnit.addAsset(title)

        val icon = ImageAsset(20, 20, 20, 20)
        icon.type = ImageAsset.ImageType.ICON
        icon.isRequired = true
        adUnit.addAsset(icon)

        val image = ImageAsset(200, 200, 200, 200)
        image.type = ImageAsset.ImageType.MAIN
        image.isRequired = true
        adUnit.addAsset(image)

        val data = DataAsset()
        data.length = 90
        data.type = DataAsset.DataType.SPONSORED
        data.isRequired = true
        adUnit.addAsset(data)

        val body = DataAsset()
        body.isRequired = true
        body.type = DataAsset.DataType.DESC
        adUnit.addAsset(body)

        val cta = DataAsset()
        cta.isRequired = true
        cta.type = DataAsset.DataType.CTA_TEXT
        adUnit.addAsset(cta)
        return adUnit
    }

    private fun createAdLoader(
        wrapper: ViewGroup,
    ): AdLoader {
        val onGamAdLoaded = OnAdManagerAdViewLoadedListener { adManagerAdView: AdManagerAdView? ->
            Log.d("GamNative", "Gam loaded")
            adView = adManagerAdView
            wrapper.addView(adManagerAdView)
        }
        val onUnifiedAdLoaded = OnNativeAdLoadedListener {
                Log.d("GamNative", "Unified native loaded")
                this.unifiedNativeAd = it
            }
        val onCustomAdLoaded = OnCustomFormatAdLoadedListener { nativeCustomTemplateAd: NativeCustomFormatAd? ->
                Log.d("GamNative", "Custom ad loaded")
                AdUtils.findNative(nativeCustomTemplateAd!!, object : NativeAdListener {

                    override fun onNativeLoaded(ad: NativeInAppAd) {
                        // 6. Render native ad
                        inflateNativeInAppAd(ad, wrapper)
                    }

                    override fun onNativeNotFound() {
                        Log.e("GamNative", "onNativeNotFound")
                    }

                    override fun onNativeNotValid() {
                        Log.e("GamNative", "onNativeNotValid")
                    }
                })
            }
        return AdLoader.Builder(wrapper.context, AD_UNIT_ID)
            .forAdManagerAdView(onGamAdLoaded, AdSize.BANNER)
            .forNativeAd(onUnifiedAdLoaded)
            .forCustomFormatAd(
                CUSTOM_FORMAT_ID,
                onCustomAdLoaded
            ) { _: NativeCustomFormatAd?, _: String? -> }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Toast.makeText(wrapper.context, "DFP onAdFailedToLoad", Toast.LENGTH_SHORT)
                        .show()
                }
            }).build()
    }

    private fun inflateNativeInAppAd(
        ad: NativeInAppAd, wrapper: ViewGroup
    ) {
        val nativeContainer = View.inflate(wrapper.context, R.layout.layout_native, null)
        ad.registerView(nativeContainer, object : NativeAdEventListener {
            override fun onAdClicked() {}

            override fun onAdImpression() {}

            override fun onAdExpired() {}
        })
        val icon = nativeContainer.findViewById<ImageView>(R.id.imgIcon)
        ImageUtil.download(ad.getIconUrl(), icon)
        val title = nativeContainer.findViewById<TextView>(R.id.tvTitle)
        title.text = ad.getTitle()
        val image = nativeContainer.findViewById<ImageView>(R.id.imgImage)
        ImageUtil.download(ad.getImageUrl(), image)
        val description = nativeContainer.findViewById<TextView>(R.id.tvDesc)
        description.text = ad.getDescription()
        val cta = nativeContainer.findViewById<Button>(R.id.btnCta)
        cta.text = ad.getCallToAction()
        wrapper.addView(nativeContainer)
    }


    override fun onDestroy() {
        super.onDestroy()
        adView?.destroy()
        unifiedNativeAd?.destroy()
        adUnit?.stopAutoRefresh()
    }

}
