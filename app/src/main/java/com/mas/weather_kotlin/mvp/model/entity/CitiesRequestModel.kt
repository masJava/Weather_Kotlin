package com.mas.weather_kotlin.mvp.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class CitiesRequestModel(
    @Expose val name: String = "",
    @Expose val lat: Float = 0f,
    @Expose val lon: Float = 0f,
    @Expose val country: String = "",
    @Expose val localNames: List<LocalNames>? = null,
) : Parcelable