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
package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.rendering

import MediaNetAdSDK
import android.os.Bundle
import android.util.Log
import com.android.adsdk.ad.rendering.AdEventListener
import com.android.adsdk.ad.rendering.banner.BannerAdView
import com.android.adsdk.base.Error
import com.android.adsdk.base.AdViewSize
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamRenderingApiDisplayBanner320x50Activity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/45361917/prebidsdkad"
        const val CONFIG_ID = "imp-prebid-banner-320-50"
        const val STORED_RESPONSE = "response-prebid-banner-320-50"
        const val WIDTH = 320
        const val HEIGHT = 50
        const val TAG = "GamRenderingApiDisplayBanner320x50Activity"
    }

    private var bannerAdView: BannerAdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()
    }

    private fun createAd() {
        val bannerAdView = BannerAdView(this, AD_UNIT_ID, AdViewSize(WIDTH, HEIGHT))
            .setAutoRefreshInterval(refreshTimeSeconds)
            .setBannerAdListener(object : AdEventListener {
                override fun onAdClicked() {
                    Log.d(TAG, "onAdClicked")
                }

                override fun onAdClosed() {
                    Log.d(TAG, "onAdClosed")
                }

                override fun onAdDisplayed() {
                    Log.d(TAG, "onAdDisplayed")
                }

                override fun onAdFailed(error: Error) {
                    Log.d(TAG, "onAdFailed code ${error.errorCode} message ${error.errorMessage}")
                }

                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded")
                }

            })
        adWrapperView.addView(bannerAdView.getView())
        bannerAdView.loadAd()
    }


    override fun onDestroy() {
        super.onDestroy()
        bannerAdView?.destroy()
    }

}
