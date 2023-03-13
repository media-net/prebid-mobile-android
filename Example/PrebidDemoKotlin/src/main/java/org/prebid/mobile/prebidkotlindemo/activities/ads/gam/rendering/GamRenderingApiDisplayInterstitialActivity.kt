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

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import com.medianet.android.adsdk.ad.rendering.interstitial.InterstitialAdView
import com.medianet.android.adsdk.MediaNetAdSDK
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity
import com.medianet.android.adsdk.base.Error
import com.medianet.android.adsdk.ad.rendering.AdEventListener

class GamRenderingApiDisplayInterstitialActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/45361917/prebidsdkinterstitial"
        const val CONFIG_ID = "imp-prebid-display-interstitial-320-480"
        const val STORED_RESPONSE = "response-prebid-display-interstitial-320-480"
        const val TAG = "GamRenderingApiDisplayInterstitialActivity"
    }

    private var adUnit: InterstitialAdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)

        WebView.setWebContentsDebuggingEnabled(true)

        createAd()
    }

   private fun createAd() {
        adUnit = InterstitialAdView(this, AD_UNIT_ID)
        adUnit?.setInterstitialAdListener(object: AdEventListener {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
                adUnit?.show()
            }

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

        })
        adUnit?.loadAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        adUnit?.destroy()
    }
}
