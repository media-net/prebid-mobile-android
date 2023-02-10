package com.medianet.android.adsdk.nativead

import org.json.JSONArray
import org.json.JSONObject
import org.prebid.mobile.LogUtil

data class ImageAsset(
    var width: Int,
    var height: Int,
    var minWidth: Int,
    var minHeight: Int,
    var type: ImageType? = null,
    var imageExt: Any? = null,
    var mimes: ArrayList<String> = ArrayList()
) : NativeAdAsset(AssetType.IMAGE) {

    enum class ImageType(var id: Int) {
        ICON(1),
        MAIN(3),
        CUSTOM(500)
    }

    override fun getJsonObject(): JSONObject {
        val result = JSONObject()
        try {
            result.putOpt("required", if (isRequired) 1 else 0)
            result.putOpt("ext", assetExt)

            val imageObject = JSONObject().apply {
                putOpt("type", type?.id)
                put("w", width)
                put("wmin", minWidth)
                put("h", height)
                put("hmin", minHeight)
                putOpt("ext", imageExt)

                mimes.takeIf { it.isNotEmpty() }?.let { mimesList ->
                    val mimesArray = JSONArray()
                    mimesList.forEach { mime ->
                        mimesArray.put(mime)
                    }
                    putOpt("mimes", mimesArray)
                }
            }
            result.put("img", imageObject)
        } catch (exception: Exception) {
            LogUtil.error("NativeImageAsset", "Can't create json object: " + exception.message)
        }
        return result
    }
}
