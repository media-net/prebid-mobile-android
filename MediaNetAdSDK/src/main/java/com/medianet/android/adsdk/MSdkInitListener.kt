package com.medianet.android.adsdk

interface MSdkInitListener {
    fun onInitSuccess()
    fun onInitFailed(error: Error)
}
