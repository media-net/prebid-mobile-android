package com.medianet.android.adsdk

import org.prebid.mobile.api.exceptions.AdException

class MAdException(var type: String, var message: String) {

    val adException = AdException(type, message)

    @JvmName("setMessage1")
    fun setMessage(message: String) {
        adException.setMessage(message)
    }

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return adException.message
    }
}