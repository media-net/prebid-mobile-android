package com.medianet.android.adsdk.events

internal object Constants {

    object EventName {
        const val TIME_OUT = "CLIENT_SIDE_TIME_OUT" //PE
        const val BID_REQUEST = "BID_REQUEST_TO_SERVER" //PE
        const val AD_REQUEST_TO_GAM = "AD_REQUEST_TO_GAM"  //AP
        const val AD_LOADED = "AD_LOADED_EVENT" //PE
    }

    object Keys {
        //default params
        const val CUSTOMER_ID = "cid"
        const val DOMAIN_NAME = "dn"
        const val ITYPE = "itype"
        const val DFP_DIV_ID = "supcrid"
        const val CR_ID = "crid"
        const val UGD = "ugd"
        const val DTYPE_ID = "dtype_id"
        const val COUNTRY_CODE = "cc"
        const val PARTNER_ID = "pid"
        const val AD_SIZE = "size"
        const val AD_SIZES = "szs"
        const val RANDOM_DATA = "rd"
        const val SDK_VERSION = "sdkversion"
        const val LOGGING_PER = "lper"
        const val LOG_ID = "logid"
        const val TO_CONSIDER = "toconsider"

        //custom params PE
        const val EVT_ID = "evtid"
        const val PROJECT_TYPE = "project"
        const val EVENT_NAME = "event"

        const val PE_LOG_ID = "kfk"
        const val PE_EVT_ID = "projectevents"
        const val PE_PROJECT_TYPE = "mobile_sdk"

        const val AP_LOG_ID = "aplog"
    }

}