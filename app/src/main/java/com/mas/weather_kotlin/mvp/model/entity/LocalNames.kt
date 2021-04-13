package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalNames(
    @Expose val ru: String = "",
    @Expose val featureName: String = "",
    ) : Parcelable