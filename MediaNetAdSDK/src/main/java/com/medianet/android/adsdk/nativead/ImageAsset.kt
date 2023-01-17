package com.medianet.android.adsdk.nativead

import com.medianet.android.adsdk.Util
import org.json.JSONObject
import org.prebid.mobile.NativeAsset
import org.prebid.mobile.NativeImageAsset

class ImageAsset(width: Int, height: Int, minWidth: Int, minHeight: Int): NativeAdAsset(AssetType.IMAGE) {

    enum class ImageType(id: Int) {
        ICON(1),
        MAIN(3),
        CUSTOM(500)
    }

    private var nativeImageAsset = NativeImageAsset(width, height, minWidth, minHeight)

    fun setImageType(type: ImageType) {
        nativeImageAsset.imageType = Util.getPrebidImageType(type)
    }

    fun setRequired(required: Boolean) {
        nativeImageAsset.isRequired = required
    }

    fun setAssetExt(assetExt: Any) {
        nativeImageAsset.assetExt = assetExt
    }

    fun setImageExt(imageExt: Any) {
        nativeImageAsset.imageExt = imageExt
    }

    override fun getJsonObject(): JSONObject? {
        return nativeImageAsset.jsonObject
    }

    override fun getPrebidAsset(): NativeAsset {
        return nativeImageAsset
    }
}