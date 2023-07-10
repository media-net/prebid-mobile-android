package com.medianet.android.adsdk.base.listeners

import androidx.annotation.MainThread
import com.medianet.android.adsdk.base.Error

/**
 * listener interface to listen to bid request call result
 */
interface OnBidCompletionListener {
    @MainThread
    fun onSuccess(keywordMap: Map<String, String>? = null)

    @MainThread
    fun onError(error: Error)
}