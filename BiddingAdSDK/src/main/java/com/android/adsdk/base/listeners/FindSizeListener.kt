package com.android.adsdk.base.listeners

import com.android.adsdk.base.FindSizeError

interface FindSizeListener {
    fun success(width: Int, height: Int)
    fun failure(error: FindSizeError)
}