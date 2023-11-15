package com.android.adsdk.ad.nativead.assets

import org.json.JSONObject
import org.prebid.mobile.LogUtil

/**
 * asset class for different types of data to be present in native bid response
 */
data class DataAsset(
    var type: DataType? = null,
    var length: Int = 0,
    var dataExt: Any? = null
): NativeAdAsset(AssetType.DATA) {

    enum class DataType(var id: Int) {
        SPONSORED(1),
        DESC(2),
        RATING(3),
        LIKES(4),
        DOWNLOADS(5),
        PRICE(6),
        SALE_PRICE(7),
        PHONE(8),
        ADDRESS(9),
        DESC2(10),
        DISPLAY_URL(11),
        CTA_TEXT(12),
        CUSTOM(500)
    }

    override fun getJsonObject(): JSONObject {
        val result = JSONObject()
        try {
            result.putOpt("required", if (isRequired) 1 else 0)
            result.putOpt("ext", assetExt)
            val dataObject = JSONObject().apply {
                putOpt("type", type?.id)
                putOpt("len", length)
                putOpt("ext", dataExt)
            }
            result.put("data", dataObject)
        } catch (exception: Exception) {
            LogUtil.error("NativeTitleAsset", "Can't create json object: " + exception.message)
        }
        return result
    }
}