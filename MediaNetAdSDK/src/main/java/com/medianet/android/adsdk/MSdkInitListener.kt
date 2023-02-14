package com.medianet.android.adsdk

/**
 * Listener Interface to listen to SDK Initialisation
 */
interface MSdkInitListener {
    fun onInitSuccess()
    fun onInitFailed(error: Error)
}