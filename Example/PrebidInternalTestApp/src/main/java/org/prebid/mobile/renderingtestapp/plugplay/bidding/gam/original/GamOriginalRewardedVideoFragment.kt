/*
 *    Copyright 2018-2021 Prebid.org, Inc.
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

package org.prebid.mobile.renderingtestapp.plugplay.bidding.gam.original

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.events_bids.*
import kotlinx.android.synthetic.main.fragment_bidding_interstitial.*
import org.prebid.mobile.RewardedVideoAdUnit
import org.prebid.mobile.renderingtestapp.R
import org.prebid.mobile.renderingtestapp.plugplay.bidding.base.BaseBidRewardedFragment

class GamOriginalRewardedVideoFragment : BaseBidRewardedFragment() {
    companion object {
        private const val TAG = "GamOriginalRewardedVideo"
    }
    private var adUnit: RewardedVideoAdUnit? = null
    private var displayAdCallback: (() -> Unit)? = null

    override fun initUi(view: View, savedInstanceState: Bundle?) {
        super.initUi(view, savedInstanceState)
        btnLoad?.setOnClickListener {
            handleLoadOriginalInterstitialClick()
        }
    }

    override fun initRewardedAd(adUnitId: String?, configId: String?) {
        createAd()
    }

    private fun createAd() {
        val builder = AdManagerAdRequest.Builder()
        adUnit = RewardedVideoAdUnit(configId)
        adUnit?.fetchDemand(builder) {
            val request = builder.build()
            RewardedAd.load(
                requireContext(),
                adUnitId,
                request,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        displayAdCallback = {
                            rewardedAd.show(
                                requireActivity()
                            ) { }
                        }
                        Log.d(TAG, "onAdLoaded() called with: reward = [${rewardedAd.rewardItem}]")
                        btnAdLoaded?.isEnabled = true
                        btnLoad?.setText(R.string.text_show)
                        btnLoad?.isEnabled = true
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.d(TAG, "onAdFailed() called with: exception = [$loadAdError]")
                        btnAdFailed?.isEnabled = true
                        btnLoad?.isEnabled = true
                    }
                }
            )
        }
    }

    private fun handleLoadOriginalInterstitialClick() {
        when (btnLoad?.text) {
            getString(R.string.text_load) -> {
                btnLoad?.isEnabled = false
                resetEventButtons()
                createAd()
            }
            getString(R.string.text_show) -> {
                btnLoad?.text = getString(R.string.text_load)
                displayAdCallback?.invoke()
            }
        }
    }

}