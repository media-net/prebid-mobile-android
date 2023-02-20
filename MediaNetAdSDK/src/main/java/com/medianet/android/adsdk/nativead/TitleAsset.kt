package com.medianet.android.adsdk.nativead

import org.json.JSONObject
import org.prebid.mobile.LogUtil

/**
 * asset class for title text to be present in native bid response
 */
data class TitleAsset (
    var length: Int = 0,
    var titleExt: Any? = null,
): NativeAdAsset(AssetType.TITLE) {

    override fun getJsonObject(): JSONObject {
        val result = JSONObject()
        try {
            result.putOpt("required", if (isRequired) 1 else 0)
            result.putOpt("ext", assetExt)
            val titleObject = JSONObject().apply {
                putOpt("len", length)
                putOpt("ext", titleExt)
            }
            result.put("title", titleObject)
        } catch (exception: Exception) {
            LogUtil.error("NativeTitleAsset", "Can't create json object: " + exception.message)
        }
        return result
    }
}