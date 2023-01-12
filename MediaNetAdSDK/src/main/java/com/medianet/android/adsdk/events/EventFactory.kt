package com.medianet.android.adsdk.events

import com.app.analytics.Event
import com.medianet.android.adsdk.events.Constants.EventName.TIME_OUT
import com.medianet.android.adsdk.events.Constants.EventType.TYPE_PROJECT_EVENT
import com.medianet.android.adsdk.events.Constants.Keys.BROWSER_ID
import com.medianet.android.adsdk.events.Constants.Keys.CUSTOMER_ID

object EventFactory {
    private var configuration: Configuration? = null
    private var defaultParams = mutableMapOf<String, String>()


    fun init(initialConfiguration: Configuration) {
        configuration = initialConfiguration
        updateDefaultParams(configuration)
    }

    private fun updateDefaultParams(configuration: EventFactory.Configuration?) {
        configuration?.let {
            //We will prepare default params form config here
            defaultParams.put(CUSTOMER_ID, it.customerId)
        }
    }

    // All event specific event we will take in method param
    fun getTimeoutEvent(
         browserId: String,
    ): Event? {

        if (configuration == null) {
            // Log error here
            return null
        }

        val params = defaultParams.toMutableMap().apply {
            //We will add all custom params here
            put(BROWSER_ID, browserId)
        }
        return Event (
            name = TIME_OUT,
            type = TYPE_PROJECT_EVENT,
            params = params
        )
    }


    fun updateConfiguration(config: Configuration) {
        configuration = config
        updateDefaultParams(configuration)
    }



    // All common params, we will take here in config
    public class Configuration(
        val customerId: String,
        val customerName: String,
        val dataCenter: String,
        val domain: String,
        val integrationType: String,
        val adSlotId: String,
        val  baseUrl: String
    )
}