package com.android.adsdk.base.listeners


/**
 * listener interface to listen to sdk initialisation
 */
interface MSdkInitListener {
    fun onInitSuccess()
    fun onInitFailed(error: com.android.adsdk.base.Error)
}