package com.medianet.android.adsdk

import androidx.annotation.NonNull
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import org.prebid.mobile.BannerAdUnit
import org.prebid.mobile.ResultCode


/**
 * Author: Nikhil
 *
 *
 */
class BannerAd(@NonNull configId: String, adSize: AdSize = AdSize.BANNER): BannerAdUnit(configId, adSize.width, adSize.height) {

    constructor(@NonNull configId: String, width: Int, height: Int) : this(configId, AdSize(width, height))

    fun addAdditionalSize(size: AdSize) = apply {
        addAdditionalSize(size.width, size.height)
    }

    fun setRefreshInterval(interval: Int) = apply { setAutoRefreshInterval(interval) }

    fun fetchDemand(request: AdManagerAdRequest, listener: OnBidCompletionListener) {
        fetchDemand(request) { code ->
            when(code) {
                ResultCode.SUCCESS -> listener.onSuccess()
                else -> Util.mapResultCodeToError(code)
            }
        }
    }
}