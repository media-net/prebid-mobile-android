package com.android.adsdk.model.sdkconfig

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  response model class for sdk config fetch
 */
@Keep
@JsonClass(generateAdapter = true)
internal data class ConfigResponse(
    @Json(name = "crids")
    val crIds: List<Crid>,
    @Json(name = "glb_cfg")
    val globalConfig: GlobalConfig,
    @Json(name = "log")
    val logPercentage: LoggingPercentage,
    @Json(name = "pub")
    val pub: PublisherIds,
    @Json(name = "pub_cfg")
    val publisherConfig: PublisherConfig,
    @Json(name = "tgt")
    val targeting: Targeting,
    @Json(name = "to")
    val timeout: TimeOut,
    @Json(name = "urls")
    val urls: Urls,
    @Json(name = "dummy_crid")
    val dummyCrId: Crid
)

@JsonClass(generateAdapter = true)
data class Crid(
    @Json(name = "div")
    val dfpAdUnitId: String,
    @Json(name = "id")
    val crId: String,
    @Json(name = "nm")
    val name: String
)

@JsonClass(generateAdapter = true)
data class GlobalConfig(
    @Json(name = "buff_sync_sec")
    val bufferSyncSeconds: Int,

    @Transient
    var configExpiryInSec: Long? = null
)

@JsonClass(generateAdapter = true)
data class LoggingPercentage(
    @Json(name = "opp_event")
    val opportunityEvent: Double,
    @Json(name = "pro_event")
    val projectEvent: Double
)

@JsonClass(generateAdapter = true)
data class PublisherIds(
    @Json(name = "cid")
    val cId: String,
    @Json(name = "pid")
    val partnerId: String
)

@JsonClass(generateAdapter = true)
data class PublisherConfig(
    @Json(name = "df")
    val defaultFlag: Boolean,
    @Json(name = "ks")
    val killSwitch: Boolean
)

@JsonClass(generateAdapter = true)
data class Targeting(
    @Json(name = "cc")
    val countryCode: String,
    @Json(name = "dn")
    val domainName: String,
    @Json(name = "ugd")
    val ugd: String,
    @Json(name = "v")
    val version: String
)

@JsonClass(generateAdapter = true)
data class TimeOut(
    @Json(name = "auc_to")
    val auctionTimeout: Int
)

@JsonClass(generateAdapter = true)
data class Urls(
    @Json(name = "auc_layer")
    val auctionLayerUrl: String,
    @Json(name = "opp_event")
    val opportunityEventUrl: String,
    @Json(name = "pro_event")
    val projectEventUrl: String
)