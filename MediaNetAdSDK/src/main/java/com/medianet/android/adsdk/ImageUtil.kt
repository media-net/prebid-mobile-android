package com.medianet.android.adsdk

import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * util class to load images
 */
object ImageUtil {

    fun download(url: String, imageView: ImageView) {
        Glide.with(imageView)
            .load(url)
            .into(imageView)
    }
}