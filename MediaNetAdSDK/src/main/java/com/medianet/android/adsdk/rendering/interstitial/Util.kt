package com.medianet.android.adsdk.rendering.interstitial

import org.prebid.mobile.api.data.AdUnitFormat
import org.prebid.mobile.api.rendering.listeners.InterstitialAdUnitListener
import java.util.EnumSet

object Util {
    fun mapAdFormat(enumSetOfAdFormat: EnumSet<AdFormat>): EnumSet<AdUnitFormat> {
        val enumSetOfAdUnitFormat = EnumSet.noneOf(AdUnitFormat::class.java)
        for (format in enumSetOfAdFormat) {
            when (format) {
                AdFormat.DISPLAY -> enumSetOfAdUnitFormat.add(AdUnitFormat.DISPLAY)
                AdFormat.VIDEO -> enumSetOfAdUnitFormat.add(AdUnitFormat.VIDEO)
                null -> enumSetOfAdUnitFormat.add(null)
            }
        }
        return enumSetOfAdUnitFormat
    }
}