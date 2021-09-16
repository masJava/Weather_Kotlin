package com.mas.weather_kotlin.di.module

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.network.AndroidNetworkStatus
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun gson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    @Singleton
    @Provides
    fun api(gson: Gson): IDataSource = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(IDataSource::class.java)

    @Singleton
    @Provides
    fun networkStatus(app: App): INetworkStatus = AndroidNetworkStatus(app)

    @Named("mainThread")
    @Provides
    fun uiScheduler(): Scheduler = AndroidSchedulers.mainThread()

//    @Singleton
//    @Provides
//    fun fusedLocationClient(app: App): FusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(app)

//    @Singleton
//    @Provides
//    fun gpsLocation(app: App): IGPSLocation = AndroidGPSLocation(app)
companion object {
    private const val BASE_URL = "https://api.openweathermap.org/"
}

}