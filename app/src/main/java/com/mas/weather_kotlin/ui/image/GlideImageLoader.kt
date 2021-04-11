package com.mas.weather_kotlin.ui.image

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.image.IImageLoader

class GlideImageLoader : IImageLoader<ImageView> {
    override fun load(url: String, container: ImageView) {
        Glide.with(container.context)
                .load(url)
                .error(R.drawable.ic_launcher_background)
                .into(container)
    }
}