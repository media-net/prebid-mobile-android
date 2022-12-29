package com.medianet.android.adsdk

import org.prebid.mobile.ResultCode

sealed class Result

sealed class Error(val errorCode: Int, val errorMessage: String): Result() {

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
}

