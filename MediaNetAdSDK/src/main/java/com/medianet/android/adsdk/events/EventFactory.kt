package com.medianet.android.adsdk.events

import com.app.analytics.Event
import com.medianet.android.adsdk.base.MAdSize
import com.medianet.android.adsdk.events.Constants.AP_LOG_ID
import com.medianet.android.adsdk.events.Constants.DEFAULT_DBF_VALUE
import com.medianet.android.adsdk.events.Constants.DEFAULT_DTYPE_ID_VALUE
import com.medianet.android.adsdk.events.Constants.DEFAULT_OPPORTUNITY_EVENT_PVID_VALUE
import com.medianet.android.adsdk.events.Constants.DEFAULT_PTYPE_VALUE
import com.medianet.android.adsdk.events.Constants.DEFAULT_TO_CONSIDER_VALUE
import com.medianet.android.adsdk.events.Constants.DEFAULT_UGD_VALUE
import com.medianet.android.adsdk.events.Constants.Keys.COUNTRY_CODE
import com.medianet.android.adsdk.events.Constants.Keys.CR_ID
import com.medianet.android.adsdk.events.Constants.Keys.CUSTOMER_ID
import com.medianet.android.adsdk.events.Constants.Keys.DBF
import com.medianet.android.adsdk.events.Constants.Keys.DFP_AD_PATH
import com.medianet.android.adsdk.events.Constants.Keys.DFP_DIV_ID
import com.medianet.android.adsdk.events.Constants.Keys.DOMAIN_NAME
import com.medianet.android.adsdk.events.Constants.Keys.DTYPE_ID
import com.medianet.android.adsdk.events.Constants.Keys.EVENT_NAME
import com.medianet.android.adsdk.events.Constants.Keys.EVT_ID
import com.medianet.android.adsdk.events.Constants.Keys.GDPR
import com.medianet.android.adsdk.events.Constants.Keys.ITYPE
import com.medianet.android.adsdk.events.Constants.Keys.LOGGING_PER
import com.medianet.android.adsdk.events.Constants.Keys.LOG_ID
import com.medianet.android.adsdk.events.Constants.Keys.OS_VERSION
import com.medianet.android.adsdk.events.Constants.Keys.PARTNER_ID
import com.medianet.android.adsdk.events.Constants.Keys.PREBID_VERSION
import com.medianet.android.adsdk.events.Constants.Keys.PROJECT_TYPE
import com.medianet.android.adsdk.events.Constants.Keys.PV_ID
import com.medianet.android.adsdk.events.Constants.Keys.P_TYPE
import com.medianet.android.adsdk.events.Constants.Keys.REQUEST_AD_SIZE
import com.medianet.android.adsdk.events.Constants.Keys.SDK_VERSION
import com.medianet.android.adsdk.events.Constants.Keys.TO_CONSIDER
import com.medianet.android.adsdk.events.Constants.Keys.UGD
import com.medianet.android.adsdk.events.Constants.Keys.UNIQUE_ID
import com.medianet.android.adsdk.events.Constants.MOBILE_SDK
import com.medianet.android.adsdk.events.Constants.PE_EVT_ID
import com.medianet.android.adsdk.events.Constants.PE_LOG_ID
import com.medianet.android.adsdk.events.Constants.PE_PROJECT_TYPE
import com.medianet.android.adsdk.model.sdkconfig.SdkConfiguration
import com.medianet.android.adsdk.utils.MapperUtils.getSizeString
import java.util.UUID

/**
 * factory class to create different types of events
 */
internal object EventFactory {

    private var sdkConfig: SdkConfiguration? = null
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
            put(PV_ID, DEFAULT_OPPORTUNITY_EVENT_PVID_VALUE)
            put(DBF, DEFAULT_DBF_VALUE)
            put(P_TYPE, DEFAULT_PTYPE_VALUE)
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
        eventType: LoggingEvents,
        eventParams: Map<String, String>,
    ): Event {
        val params = commonParams.toMutableMap()
        params.putAll(eventParams)
        var baseUrl = ""
        params[EVENT_NAME] = eventName
        params[DFP_DIV_ID] = dfpDivId
        sdkConfig?.let {
            params[CR_ID] = it.getCrId(dfpDivId)
        }
        if (sizes != null && sizes.isNotEmpty()) {
            val sizeStr = getSizeString(sizes)
            params[REQUEST_AD_SIZE] = sizeStr
        }

        when (eventType) {
            LoggingEvents.PROJECT -> {
                params.putAll(projectEventParams)
                params[EVENT_NAME] = eventName
                params[UNIQUE_ID] = UUID.randomUUID().toString()
                baseUrl = sdkConfig?.projectEventUrl ?: ""
            }
            LoggingEvents.OPPORTUNITY -> {
                params.putAll(opportunityEventParams)
                baseUrl = sdkConfig?.opportunityEventUrl ?: ""
                params[DFP_AD_PATH] = dfpDivId
                sdkConfig?.let {
                    params[PREBID_VERSION] = it.prebidVersion
                    params[OS_VERSION] = it.osVersion
                    params[GDPR] = if (it.isSubjectToGDPR) "1" else "0"
                }
            }
        }

        return Event(
            name = eventName,
            type = eventType.type,
            params = params,
            baseUrl = baseUrl,
        )
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
            put(LOGGING_PER, sdkConfig.projectEventPercentage.toString())
        }

        opportunityEventParams.apply {
            put(LOGGING_PER, sdkConfig.opportunityEventPercentage.toString())
        }
    }

    fun clear() {
        sdkConfig = null
    }
}
