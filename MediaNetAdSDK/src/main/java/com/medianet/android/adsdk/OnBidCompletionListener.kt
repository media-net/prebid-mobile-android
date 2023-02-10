package com.medianet.android.adsdk

import androidx.annotation.MainThread

interface OnBidCompletionListener {
    @MainThread
    fun onSuccess(keywordMap: Map<String, String>? = null)

    @MainThread
    fun onError(error: Error)
}
