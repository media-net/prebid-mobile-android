package org.prebid.mobile.prebidkotlindemo.activities.ads.gam.original

import android.os.Bundle
import org.prebid.mobile.prebidkotlindemo.activities.BaseAdActivity

class GamOriginalApiVideoRewardedActivity : BaseAdActivity() {

    companion object {
        const val AD_UNIT_ID = "/21808260008/prebid-demo-app-original-api-video-interstitial"
        const val CONFIG_ID = "imp-prebid-video-rewarded-320-480"
        const val STORED_RESPONSE = "response-prebid-video-rewarded-320-480-original-api"
    }

    //private var adUnit: RewardedVideoAdUnit? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* // The ID of Mocked Bid Response on PBS. Only for test cases.
        PrebidMobile.setStoredAuctionResponse(STORED_RESPONSE)

        createAd()*/
    }

    /*private fun createAd() {
        // 1. Create RewardedVideoAdUnit
        adUnit = RewardedVideoAdUnit(CONFIG_ID)

        // 2. Configure Video parameters
        adUnit?.parameters = configureVideoParameters()

        // 3. Make a bid request to Prebid Server
        val request = AdManagerAdRequest.Builder().build()
        adUnit?.fetchDemand(request) {

            // 4. Load a GAM Rewarded Ad
            RewardedAd.load(
                this,
                AD_UNIT_ID,
                request,
                createListener()
            )
        }
    }

    private fun configureVideoParameters(): VideoBaseAdUnit.Parameters {
        return VideoBaseAdUnit.Parameters().apply {
            mimes = listOf("video/mp4")
            protocols = listOf(Signals.Protocols.VAST_2_0)
            playbackMethod = listOf(Signals.PlaybackMethod.AutoPlaySoundOff)
        }
    }

    private fun createListener(): RewardedAdLoadCallback {
        return object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {

                // 5. Display rewarded ad
                rewardedAd.show(
                    this@GamOriginalApiVideoRewardedActivity
                ) { }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("GAM", "Ad failed to load: $loadAdError")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        adUnit?.stopAutoRefresh()
    }*/

}
