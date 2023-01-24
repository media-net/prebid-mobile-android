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
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.medianet.android.adsdk.Ad
import com.medianet.android.adsdk.BannerAd
import com.medianet.android.adsdk.Error
import com.medianet.android.adsdk.GamEventListener
import com.medianet.android.adsdk.MediaNetAdSDK
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiDisplayBanner320x50Activity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/prebid_demo_app_original_api_banner"
        const val CONFIG_ID = "imp-prebid-banner-320-50"
        const val STORED_RESPONSE = "response-prebid-banner-320-50"
        const val WIDTH = 320
        const val HEIGHT = 50
    }

    private var adUnit: Ad? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)
        createAd()
    }

    private fun createAd() {
        adUnit = BannerAd(AD_UNIT_ID, WIDTH, HEIGHT)
            .setAutoRefreshIntervalInSeconds(refreshTimeSeconds)

        val adView = AdManagerAdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSizes(AdSize(WIDTH, HEIGHT))

        adWrapperView.addView(adView)

        val request = AdManagerAdRequest.Builder().build()
        adUnit?.fetchDemand(adView, request, object: GamEventListener {
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
                Log.d("Tushar", "onAdFailedToLoad")
            }

            override fun onAdOpened() {
                Log.d("Tushar", "onAdOpened")
            }

            override fun onAdImpression() {
                Log.d("Tushar", "onAdImpression")
            }

            override fun onEvent(key: String, value: String) {
                Log.d("Tushar", "onEvent")
            }

            override fun onSuccess(keywordMap: Map<String, String>?) {
                Log.d("Tushar", "onSuccess")
            }

            override fun onError(error: Error) {
                Log.d("Tushar", "Error: code: ${error.errorCode}, message: ${error.errorMessage}")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        adUnit?.stopAutoRefresh()
    }

}
