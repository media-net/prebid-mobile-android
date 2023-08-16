package com.medianet.android.adsdk.events

internal object Constants {

    object EventName {
        const val TIME_OUT = "CLIENT_SIDE_TIME_OUT" // PE
        const val BID_REQUEST = "BID_REQUEST_TO_SERVER" // PE
        const val AD_REQUEST_TO_GAM = "AD_REQUEST_TO_GAM" // AP
        const val AD_LOADED = "AD_LOADED_EVENT" // PE
        const val GAM_ERROR = "GAM_ERROR" // PE
    }

    object Keys {
        // default params
        const val EVENT_NAME = "event"
        const val CUSTOMER_ID = "cid"
        const val DOMAIN_NAME = "dn"
        const val ITYPE = "itype"
        const val DFP_DIV_ID = "supcrid"
        const val CR_ID = "crid"
        const val UGD = "ugd"
        const val DTYPE_ID = "dtype_id"
        const val COUNTRY_CODE = "cc"
        const val PARTNER_ID = "pid"
        const val REQUEST_AD_SIZE = "szs"
        const val RANDOM_DATA = "rd"
        const val SDK_VERSION = "sdkversion"
        const val LOGGING_PER = "lper"
        const val LOG_ID = "logid"

        // custom params PE
        const val EVT_ID = "evtid"
        const val PROJECT_TYPE = "project"
        const val UNIQUE_ID = "commit_id"

        // custom params AP
        const val TO_CONSIDER = "toconsider"
        const val PV_ID = "pvid"
        const val DBF = "dbf"
        const val P_TYPE = "ptype"
        const val DFP_AD_PATH = "dfpadpath"
        const val PREBID_VERSION = "pbv"
        const val OS_VERSION = "__over"
        const val REQ_MTYPE = "req_mtype"
        const val AD_TYPES = "adtypes"
        const val REQUEST_ID = "acid"
        const val RESPONSE_AD_SIZES = "size"
        const val GDPR = "gdpr"
        const val SNM = "snm"

        // Error Event params
        const val ERROR_MSG = "error"
        const val ERROR_CODE = "code"
    }

    const val PE_LOG_ID = "kfk"
    const val PE_EVT_ID = "projectevents"
    const val PE_PROJECT_TYPE = "mobile_sdk"
    const val AP_LOG_ID = "aplog"
    const val DEFAULT_TO_CONSIDER_VALUE = "1"
    const val DEFAULT_OPPORTUNITY_EVENT_PVID_VALUE = "-2"
    const val MOBILE_SDK = "MOBILE_SDK"
    const val DEFAULT_UGD_VALUE = "3"
    const val DEFAULT_DTYPE_ID_VALUE = "3"
    const val DEFAULT_DBF_VALUE = "1"
    const val DEFAULT_PTYPE_VALUE = "42"
    const val SNM_SUCCESS_VALUE = "SUCCESS"
    const val SNM_NO_BIDS_VALUE = "NOBIDS"
    const val SNM_ERROR_VALUE = "ERROR"
}
