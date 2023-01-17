package com.medianet.android.adsdk.nativead

import com.medianet.android.adsdk.Util
import org.json.JSONObject
import org.prebid.mobile.NativeAsset
import org.prebid.mobile.NativeDataAsset

class DataAsset: NativeAdAsset(AssetType.DATA) {

    enum class DataType(id: Int) {
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

    private var nativeDataAsset = NativeDataAsset()

    fun setDataType(type: DataType) {
        nativeDataAsset.dataType = Util.getPrebidDataType(type)
    }

    fun setLength(length: Int) {
        nativeDataAsset.len = length
    }

    fun setRequired(required: Boolean) {
        nativeDataAsset.isRequired = required
    }

    fun setAssetExt(assetExt: Any) {
        nativeDataAsset.assetExt = assetExt
    }

    fun setDataExt(dataExt: Any) {
        nativeDataAsset.dataExt = dataExt
    }

    override fun getJsonObject(): JSONObject? {
        return nativeDataAsset.jsonObject
    }

    override fun getPrebidAsset(): NativeAsset {
        return nativeDataAsset
    }
}