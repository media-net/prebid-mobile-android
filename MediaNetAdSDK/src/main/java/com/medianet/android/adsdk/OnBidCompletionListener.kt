package com.medianet.android.adsdk

import androidx.annotation.MainThread
import org.prebid.mobile.ResultCode

/**
 * listener interface to listen to bid request call result
 */
interface OnBidCompletionListener {
    @MainThread
    fun onSuccess(keywordMap: Map<String, String>? = null)

    @MainThread
    fun onError(error: Error)
}