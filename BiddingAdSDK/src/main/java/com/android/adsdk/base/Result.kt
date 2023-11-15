package com.android.adsdk.base

sealed class Result

sealed class Error(var errorCode: Int, var errorMessage: String) : Result() {

    /**
     * The ad request failed due to empty account id
     */
    object INVALID_ACCOUNT_ID : Error(131, "Empty account id.")

    /**
     * The ad request failed due to empty config id on the ad unit
     */
    object INVALID_CONFIG_ID : Error(132, "Empty config id.")

    /**
     * The ad request failed because a CUSTOM host used without providing host url
     */
    object INVALID_HOST_URL : Error(133, "Empty host url for custom Prebid Server host.")

    /**
     * For banner view, we don't support multi-size request
     */
    object INVALID_BANNER_SIZE : Error(134, "Banner Ad's height or width can not be zero.")

    /**
     * Unable to obtain the Application Context, check if you have set it through PrebidMobile.setApplicationContext()
     */
    object INVALID_CONTEXT : Error(135, "Invalid context")

    /**
     * Currently, we only support Banner, Interstitial, DFP Banner, Interstitial
     */
    object INVALID_AD_OBJECT : Error(136, "Invalid Ad object")

    /**
     * The ad request failed due to a network error.
     */
    object NETWORK_ERROR : Error(137, "Network error.")

    /**
     * The ad request took longer than set time out
     */
    object REQUEST_TIMEOUT : Error(138, "The ad request took longer than set time out.")

    /**
     * No bids available from demand source
     */
    object NO_BIDS : Error(139, "No bids available from demand source")

    /**
     * Prebid Server responded with some error messages
     */
    object PREBID_SERVER_ERROR : Error(140, "Prebid Server responded with some error messages")

    /**
     * Missing assets requirement for native ad unit
     */
    object INVALID_NATIVE_REQUEST : Error(141, "Missing assets requirement for native ad unit")

    object MISCELLANIOUS_ERROR : Error(199, "Something went wrong")

    object SDK_INIT_ERROR : Error(142, "SDK initialisation failed")

    object GAM_LOAD_AD_ERROR : Error(143, "Error in loading Ad")

    object INVALID_REQUEST : Error(144, "Invalid request")

    object INTERNAL_ERROR : Error(145, "SDK internal error")

    object INIT_ERROR : Error(146, "Initialization failed")

    object SERVER_ERROR : Error(147, "Server error")

    object THIRD_PARTY : Error(148, "Third Party SDK")

    object CONFIG_ERROR_CONFIG_KILL_SWITCH : Error(149, "SDK Config error: Your Contract with AdSdk has ended")
    object CONFIG_ERROR_CONFIG_FAILURE : Error(149, "SDK Config error: Config failure")
}
