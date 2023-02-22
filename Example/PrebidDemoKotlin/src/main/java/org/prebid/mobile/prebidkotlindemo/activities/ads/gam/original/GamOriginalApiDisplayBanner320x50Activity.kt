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
import com.medianet.android.adsdk.ad.original.banner.BannerAd
import com.medianet.android.adsdk.base.Error
import com.medianet.android.adsdk.base.listeners.GamEventListener
import com.medianet.android.adsdk.MediaNetAdSDK
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiDisplayBanner320x50Activity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/45361917/prebidsdkad"
        const val CONFIG_ID = "imp-prebid-banner-320-50"
        const val STORED_RESPONSE = "response-prebid-banner-320-50"
        const val WIDTH = 320
        const val HEIGHT = 50
        const val TAG = "GamOriginalApiDisplayBanner320x50Activity"
    }

    private var adUnit: BannerAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)
        createAd()
    }

    private fun createAd() {
        adUnit = BannerAd(AD_UNIT_ID, WIDTH, HEIGHT)
        adUnit?.setAutoRefreshIntervalInSeconds(refreshTimeSeconds)

        val adView = AdManagerAdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSizes(AdSize(WIDTH, HEIGHT))

        adWrapperView.addView(adView)

        val request = AdManagerAdRequest.Builder().build()
        adUnit?.fetchDemandAndLoad(adView, request, object: GamEventListener {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
            }

            override fun onAdFailedToLoad(error: Error) {
                Log.d(TAG, "onAdFailedToLoad")
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression")
            }

            override fun onEvent(key: String, value: String) {
                Log.d(TAG, "onEvent")
            }

            override fun onSuccess(keywordMap: Map<String, String>?) {
                Log.d(TAG, "onSuccess")
            }

            override fun onError(error: Error) {
                Log.d(TAG, "Error: code: ${error.errorCode}, message: ${error.errorMessage}")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        adUnit?.stopAutoRefresh()
    }

}
