package com.medianet.android.adsdk.events

import com.app.analytics.Event
import com.app.analytics.utils.Constant.Providers.DEFAULT_DTYPE_ID_VALUE
import com.app.analytics.utils.Constant.Providers.DEFAULT_TO_CONSIDER_VALUE
import com.app.analytics.utils.Constant.Providers.DEFAULT_UGD_VALUE
import com.app.analytics.utils.Constant.Providers.MOBILE_SDK
import com.medianet.android.adsdk.base.MAdSize
import com.medianet.android.adsdk.events.Constants.Keys.AD_SIZE
import com.medianet.android.adsdk.events.Constants.Keys.AD_SIZES
import com.medianet.android.adsdk.events.Constants.Keys.AP_LOG_ID
import com.medianet.android.adsdk.events.Constants.Keys.COUNTRY_CODE
import com.medianet.android.adsdk.events.Constants.Keys.CR_ID
import com.medianet.android.adsdk.events.Constants.Keys.CUSTOMER_ID
import com.medianet.android.adsdk.events.Constants.Keys.DFP_DIV_ID
import com.medianet.android.adsdk.events.Constants.Keys.DOMAIN_NAME
import com.medianet.android.adsdk.events.Constants.Keys.DTYPE_ID
import com.medianet.android.adsdk.events.Constants.Keys.EVENT_NAME
import com.medianet.android.adsdk.events.Constants.Keys.EVT_ID
import com.medianet.android.adsdk.events.Constants.Keys.ITYPE
import com.medianet.android.adsdk.events.Constants.Keys.LOGGING_PER
import com.medianet.android.adsdk.events.Constants.Keys.LOG_ID
import com.medianet.android.adsdk.events.Constants.Keys.PARTNER_ID
import com.medianet.android.adsdk.events.Constants.Keys.PE_EVT_ID
import com.medianet.android.adsdk.events.Constants.Keys.PE_LOG_ID
import com.medianet.android.adsdk.events.Constants.Keys.PE_PROJECT_TYPE
import com.medianet.android.adsdk.events.Constants.Keys.PROJECT_TYPE
import com.medianet.android.adsdk.events.Constants.Keys.SDK_VERSION
import com.medianet.android.adsdk.events.Constants.Keys.TO_CONSIDER
import com.medianet.android.adsdk.events.Constants.Keys.UGD
import com.medianet.android.adsdk.model.sdkconfig.SdkConfiguration

/**
 * factory class to create different types of events
 */
internal object EventFactory {

    private var sdkConfig: SdkConfiguration?  = null
    private val commonParams = mutableMapOf<String, String>()
    private val projectEventParams = mutableMapOf<String, String>()
    private val opportunityEventParams = mutableMapOf<String, String>()

    init {
        projectEventParams.apply {
            put(LOG_ID, PE_LOG_ID)
            put(EVT_ID, PE_EVT_ID)
            put(PROJECT_TYPE, PE_PROJECT_TYPE)
        }
        opportunityEventParams.apply {
            put(LOG_ID, AP_LOG_ID)
            put(TO_CONSIDER, DEFAULT_TO_CONSIDER_VALUE)
        }
    }

    /**
     * creates and returns event object based on the function params
     * @param eventName is the unique name of the event
     * @param dfpDivId is the adUnit's configuration config id
     * @param sizes are the sizes set for the ad slot
     * @return the created event object
     */
    fun getEvent(
        eventName: String,
        dfpDivId: String,
        sizes: List<MAdSize>?,
        eventType: LoggingEvents
    ): Event {
        val params = commonParams.toMutableMap()
        var baseUrl = ""
        when (eventType) {
            LoggingEvents.PROJECT -> {
                params.putAll(projectEventParams)
                params[EVENT_NAME] = eventName
                baseUrl = sdkConfig?.projectEventUrl ?: ""
            }
            LoggingEvents.OPPORTUNITY -> {
                params.putAll(opportunityEventParams)
                baseUrl = sdkConfig?.opportunityEventUrl ?: ""
            }
        }
        params[EVENT_NAME] = eventName
        params[DFP_DIV_ID] = dfpDivId
        sdkConfig?.let {
            params[CR_ID] = it.getCrId(dfpDivId)
        }
        if (sizes != null && sizes.isNotEmpty()) {
            val sizeStr = getSizeString(sizes)
            if (sizes.size > 1) {
                params[AD_SIZE] = sizeStr
            } else {
                params[AD_SIZES] = sizeStr
            }
        }

        return Event (
            name = eventName,
            type = eventType.type,
            params = params,
            baseUrl = baseUrl
        )
    }

    /**
     * converts list of MAdSize Objects to a plain string
     * @param sizes list of MAdSize objects
     * @return converted string result (Eg: 320X420|200X100)
     */
    private fun getSizeString(sizes: List<MAdSize>): String {
        val sb = StringBuilder()
        sizes.forEachIndexed { index, size ->
            if (index != 0) {
                sb.append("|")
            }
            sb.append(size.width).append("X").append(size.height)
        }
        return sb.toString()
    }

    /**
     * updates the common params sent in the events when sdk config gets updated
     * @param sdkConfig sdk config data for the publisher account Id
     */
    fun updateConfiguration(sdkConfig: SdkConfiguration) {
        this.sdkConfig = sdkConfig
        commonParams.apply {
            put(CUSTOMER_ID, sdkConfig.customerId)
            put(PARTNER_ID, sdkConfig.partnerId)
            put(DOMAIN_NAME, sdkConfig.domainName)
            put(COUNTRY_CODE, sdkConfig.countryCode)
            put(SDK_VERSION, sdkConfig.sdkVersion)
            put(UGD, DEFAULT_UGD_VALUE)
            put(DTYPE_ID, DEFAULT_DTYPE_ID_VALUE)
            put(ITYPE, MOBILE_SDK)
        }

        projectEventParams.apply {
            put(LOGGING_PER, sdkConfig.projectEventPercentage.takeIf { it > 0 }?.let { 100.div(it).toString() } ?: "0")
        }

        opportunityEventParams.apply {
            put(LOGGING_PER, sdkConfig.opportunityEventPercentage.takeIf { it > 0 }?.let { 100.div(it).toString() } ?: "0")
        }
    }

    fun clear() {
        sdkConfig = null
    }
}