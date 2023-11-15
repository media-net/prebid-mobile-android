package com.android.adsdk.base

object AdSignal {
    enum class Api(value: Int) {
        /** VPAID 1.0  */
        VPAID_1(1),

        /** VPAID 2.0  */
        VPAID_2(2),

        /** MRAID-1  */
        MRAID_1(3),

        /** ORMMA  */
        ORMMA(4),

        /** MRAID-2  */
        MRAID_2(5),

        /** MRAID-3  */
        MRAID_3(6),

        /** OMID-1  */
        OMID_1(7),
    }
}
