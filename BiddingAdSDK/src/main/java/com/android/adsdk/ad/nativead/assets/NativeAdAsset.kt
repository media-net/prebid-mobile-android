package com.android.adsdk.ad.nativead.assets

import org.json.JSONObject

/**
 * base class to be implemented by all native ad assets
 */
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