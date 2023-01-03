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
package org.prebid.mobile.prebidkotlindemo.activities.ads.inapp

import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class InAppVideoBannerActivity : BaseAdActivity() {

    /*companion object {
        const val CONFIG_ID = "imp-prebid-video-outstream"
        const val STORED_RESPONSE = "response-prebid-video-outstream"
        const val WIDTH = 300
        const val HEIGHT = 250
    }

    private var bannerView: BannerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The ID of Mocked Bid Response on PBS. Only for test cases.
        PrebidMobile.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()
    }

    private fun createAd() {
        bannerView = BannerView(
            this,
            CONFIG_ID,
            AdSize(WIDTH, HEIGHT)
        )

        bannerView?.videoPlacementType = VideoPlacementType.IN_BANNER
        bannerView?.setBannerListener(object : BannerViewListener {
            override fun onAdFailed(bannerView: BannerView?, exception: AdException?) {
                Log.e("InAppVideoBanner", "Ad failed: ${exception?.message}")
            }

            override fun onAdLoaded(bannerView: BannerView?) {}
            override fun onAdClicked(bannerView: BannerView?) {}
            override fun onAdDisplayed(bannerView: BannerView?) {}
            override fun onAdClosed(bannerView: BannerView?) {}
        })
        bannerView?.loadAd()

        adWrapperView.addView(bannerView)
    }


    override fun onDestroy() {
        super.onDestroy()
        bannerView?.destroy()
    }*/

}
