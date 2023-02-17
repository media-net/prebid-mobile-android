package com.medianet.android.adsdk

import androidx.annotation.MainThread
import org.prebid.mobile.ResultCode

/**
 * Listener interface to listen to Bid Request Call Result
 */
interface OnBidCompletionListener {
    @MainThread
    fun onSuccess(keywordMap: Map<String, String>? = null)

    @MainThread
    fun onError(error: Error)
}