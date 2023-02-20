package com.medianet.android.adsdk

import androidx.annotation.MainThread

/**
 * listener interface to listen to bid request call result
 */
interface OnBidCompletionListener {
    @MainThread
    fun onSuccess(keywordMap: Map<String, String>? = null)

    @MainThread
    fun onError(error: Error)
}
