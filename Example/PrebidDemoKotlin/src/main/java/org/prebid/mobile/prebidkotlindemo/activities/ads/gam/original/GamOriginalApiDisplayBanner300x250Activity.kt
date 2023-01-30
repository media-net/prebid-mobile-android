/*
 *    Copyright 2018-2019 Prebid.org, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.original

import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.BannerAd
import com.medianet.android.adsdk.Error
import com.medianet.android.adsdk.GamEventListener
import com.medianet.android.adsdk.MediaNetAdSDK
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity


class GamOriginalApiDisplayBanner300x250Activity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/prebid_demo_app_original_api_banner_300x250_order"
        const val CONFIG_ID = "imp-prebid-banner-300-250"
        const val STORED_RESPONSE = "response-prebid-banner-300-250"
        const val WIDTH = 300
        const val HEIGHT = 250
        val TAG = GamOriginalApiDisplayBanner300x250Activity::class.java.name
    }

    private var adUnit: BannerAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)
        createAd()
    }

    private fun createAd() {

        // 1. Create BannerAdUnit
        adUnit = BannerAd(AD_UNIT_ID, WIDTH, HEIGHT)
        adUnit?.setAutoRefreshIntervalInSeconds(refreshTimeSeconds)

        // 2. Configure banner parameters (for video ads)
        /*val parameters = BannerBaseAdUnit.Parameters()
        parameters.api = listOf(Signals.Api.MRAID_3, Signals.Api.OMID_1)*/

        // 3. Create AdManagerAdView
        val adView = AdManagerAdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSizes(AdSize(WIDTH, HEIGHT))

        // Add GMA SDK banner view to the app UI
        adWrapperView.addView(adView)

        // 4. Make a bid request to Prebid Server
        val request = AdManagerAdRequest.Builder().build()
        adUnit?.fetchDemandAndLoad(adView, request, object: GamEventListener {
            override fun onAdLoaded() {
                Log.e("Nikhil", "onAdLoaded")
            }

            override fun onAdClicked() {
                Log.e("Nikhil", "onAdClicked")
            }

            override fun onAdClosed() {
                Log.e("Nikhil", "onAdClosed")
            }

            override fun onAdFailedToLoad(error: Error) {
                Log.e("Nikhil", "onAdFailedToLoad")
            }

            override fun onAdOpened() {
                Log.e("Nikhil", "onAdOpened")
            }

            override fun onAdImpression() {
                Log.e("Nikhil", "onAdImpression")
            }

            override fun onEvent(key: String, value: String) {
                Log.e("Nikhil", "onEvent")
            }

            override fun onSuccess(keywordMap: Map<String, String>?) {
                Log.e("Nikhil", "onSuccess")
            }

            override fun onError(error: Error) {
                Log.e(TAG, "Error: code: ${error.errorCode}, message: ${error.errorMessage}")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        adUnit?.stopAutoRefresh()
    }

    private fun createGAMListener(adView: AdManagerAdView): AdListener {
        return object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()

                // 6. Update ad view
                /*AdViewUtils.findPrebidCreativeSize(adView, object : AdViewUtils.PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        adView.setAdSizes(AdSize(width, height))
                    }

                    override fun failure(error: PbFindSizeError) {}
                })*/
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }

            override fun onAdClosed() {

            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }

            override fun onAdImpression () {
                super.onAdImpression()
            }

            /*override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
            }*/
        }
    }

}
