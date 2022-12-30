package com.medianet.android.adsdk

import androidx.annotation.IntRange
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import org.prebid.mobile.AdUnit
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.ResultCode

abstract class Ad {

    abstract val adUnit: AdUnit
    abstract val adType: AdType

    //TODO - prebid does not expose it, should we expose it?
    fun getConfigId() = adUnit.configuration.configId

    //TODO - prebid does not expose it, should we expose it?
    fun getAutoRefreshIntervalInSeconds(): Int = adUnit.configuration.autoRefreshDelay
    fun setAutoRefreshIntervalInSeconds(
        @IntRange(
            from = (PrebidMobile.AUTO_REFRESH_DELAY_MIN / 1000).toLong(),
            to = (PrebidMobile.AUTO_REFRESH_DELAY_MAX / 1000).toLong()
        ) seconds: Int) = apply {
        adUnit.setAutoRefreshInterval(seconds)
    }

    fun stopAutoRefresh() = adUnit.stopAutoRefresh()
    fun resumeAutoRefresh() = adUnit.resumeAutoRefresh()

    fun addContextData(key: String, values: Set<String>) = apply {
        adUnit.updateContextData(key, values)
    }
    fun removeContextData(key: String) = apply { adUnit.removeContextData(key) }
    fun clearContextData() = apply { adUnit.clearContextData() }

    //TODO - prebid does not expose it, should we expose it?
    fun getContextData() = adUnit.configuration.contextDataDictionary

    fun getPrebidAdSlot() = adUnit.pbAdSlot
    fun setPrebidAdSlot(slot: String) = apply { adUnit.pbAdSlot =  slot }





    fun fetchDemand(listener: OnBidCompletionListener) {
        adUnit.fetchDemand { resultCode, unmodifiableMap ->
            when(resultCode) {
                ResultCode.SUCCESS -> listener.onSuccess(unmodifiableMap)
                else -> Util.mapResultCodeToError(resultCode)
            }
        }
    }

    fun fetchDemand(request: AdManagerAdRequest, listener: OnBidCompletionListener) {
        adUnit.fetchDemand(request) { code ->
            when(code) {
                ResultCode.SUCCESS -> listener.onSuccess()
                else -> Util.mapResultCodeToError(code)
            }
        }
    }
}