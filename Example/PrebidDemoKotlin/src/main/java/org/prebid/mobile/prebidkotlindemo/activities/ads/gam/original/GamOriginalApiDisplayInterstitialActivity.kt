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
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.medianet.android.adsdk.base.listeners.GamEventListener
import com.medianet.android.adsdk.ad.original.interstitial.InterstitialAd
import com.medianet.android.adsdk.MediaNetAdSDK
import com.medianet.android.adsdk.base.Error
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiDisplayInterstitialActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/prebid-demo-app-original-api-display-interstitial"
        const val CONFIG_ID = "imp-prebid-display-interstitial-320-480"
        const val STORED_RESPONSE = "response-prebid-display-interstitial-320-480"
        const val TAG = "GamOriginalApiDisplayInterstitialActivity"
    }

    private var adUnit: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        MediaNetAdSDK.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()
    }

    private fun createAd() {
        // 1. Create InterstitialAdUnit
        adUnit = InterstitialAd(AD_UNIT_ID, 80, 60)

        // 2. Make a bid request to Prebid Server
        val request = AdManagerAdRequest.Builder().build()
        adUnit?.fetchDemand(request, listener = object : GamEventListener {
            override fun onSuccess(keywordMap: Map<String, String>?) {
                AdManagerInterstitialAd.load(
                    this@GamOriginalApiDisplayInterstitialActivity,
                    AD_UNIT_ID,
                    request,
                    createListener()
                )
            }

            override fun onError(error: Error) {
                AdManagerInterstitialAd.load(
                    this@GamOriginalApiDisplayInterstitialActivity,
                    AD_UNIT_ID,
                    request,
                    createListener()
                )
            }
        })
    }

    private fun createListener(): AdManagerInterstitialAdLoadCallback {
        return object : AdManagerInterstitialAdLoadCallback() {

            override fun onAdLoaded(adManagerInterstitialAd: AdManagerInterstitialAd) {
                super.onAdLoaded(adManagerInterstitialAd)

                // 4.  Present the interstitial ad
                adManagerInterstitialAd.show(this@GamOriginalApiDisplayInterstitialActivity)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e("GAM", "Ad failed to load: $loadAdError")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //adUnit?.stopAutoRefresh()
    }
}
