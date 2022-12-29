package com.medianet.android.adsdk

import androidx.annotation.MainThread
import org.prebid.mobile.ResultCode

interface OnBidCompletionListener {
    @MainThread
    fun onSuccess()

    @MainThread
    fun onError(error: Error)
}