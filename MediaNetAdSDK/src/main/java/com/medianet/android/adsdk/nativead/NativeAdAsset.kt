package com.medianet.android.adsdk.nativead

import org.json.JSONObject
import org.prebid.mobile.NativeAsset

abstract class NativeAdAsset(private var type: AssetType?) {
    enum class AssetType {
        TITLE,
        IMAGE,
        DATA
    }

    fun getType(): AssetType? {
        return type
    }

    abstract fun getJsonObject(): JSONObject?

    abstract fun getPrebidAsset(): NativeAsset
}