package com.medianet.android.adsdk.nativead

import org.json.JSONObject

abstract class NativeAdAsset(private var type: AssetType?) {

    var assetExt: Any? = null
    var isRequired: Boolean = false

    enum class AssetType {
        TITLE,
        IMAGE,
        DATA
    }

    fun getType(): AssetType? {
        return type
    }

    abstract fun getJsonObject(): JSONObject?
}
