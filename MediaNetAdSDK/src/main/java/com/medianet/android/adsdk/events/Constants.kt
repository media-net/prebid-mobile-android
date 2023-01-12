package com.medianet.android.adsdk.events

object Constants {

    object EventName {
        const val TIME_OUT = "time_out_event"
        const val AD_REQUEST_TO_GAM = "ad_loaded_event"
        const val AD_RENDERED = "ad_rendered"
    }

    object EventType {
        const val TYPE_PROJECT_EVENT = "PE"
        const val TYPE_SLOT_OPPORTUNITY_EVENT = "SO"
    }

    object Keys {

        //default params
        const val CUSTOMER_ID = "cid"
        const val CUSTOMER_NAME = "cname"

        //custom params
        const val BROWSER_ID = "browser_id"
    }

}