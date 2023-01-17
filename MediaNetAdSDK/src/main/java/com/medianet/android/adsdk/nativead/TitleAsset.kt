package com.medianet.android.adsdk.nativead

import org.json.JSONObject
import org.prebid.mobile.NativeAsset
import org.prebid.mobile.NativeTitleAsset

class TitleAsset: NativeAdAsset(AssetType.TITLE) {

    private var nativeTitleAsset = NativeTitleAsset()

    fun setLength(length: Int) {
        nativeTitleAsset.setLength(length)
    }

    fun setRequired(required: Boolean) {
        nativeTitleAsset.isRequired = required
    }

    fun setTitleExt(ext: Any?) {
        nativeTitleAsset.titleExt = ext
    }

    fun setAssetExt(assetExt: Any?) {
        nativeTitleAsset.assetExt = assetExt
    }

    override fun getJsonObject(): JSONObject? {
        return nativeTitleAsset.jsonObject
    }

    override fun getPrebidAsset(): NativeAsset {
        return nativeTitleAsset
    }
}