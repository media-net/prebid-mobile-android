package com.medianet.android.adsdk

/**
 * listener interface to listen to sdk initialisation
 */
interface MSdkInitListener {
    fun onInitSuccess()
    fun onInitFailed(error: Error)
}
