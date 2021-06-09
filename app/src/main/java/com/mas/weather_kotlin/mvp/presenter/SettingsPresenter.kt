package com.mas.weather_kotlin.mvp.presenter

import android.annotation.SuppressLint
import android.util.Log
import com.github.terrakok.cicerone.Router
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.view.SettingsView
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class SettingsPresenter() : MvpPresenter<SettingsView>() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var weather: IOpenWeather

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var settings: SettingsModel

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

    var cities = mutableListOf<CitiesRequestModel>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("my", "Settings")
        if (settings.city.isNotEmpty())
            loadCityList(settings.city)
//        viewState.setSpinnerPosition(settings.position)
        viewState.setCity(settings.city)
        viewState.setGPSSwitch(settings.gpsKey)
    }

    fun loadCityList(city: String): MutableList<CitiesRequestModel> {
        weather.getCities(city)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        if (citiesRequest.size==0){viewState.showToast("Unknown city")}
                        cities.clear()
                        cities.addAll(citiesRequest)
                        viewState.setSpinnerAdapter(cities)
                    }
                    Log.d("my", "cities")
                },
                { t -> Log.d("my", t.message.toString()) })
        return cities
    }

    fun loadCityListGPS(lat: Double, lon: Double): MutableList<CitiesRequestModel> {
        weather.getCitiesGPS(lat, lon)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        cities.clear()
                        cities.addAll(citiesRequest)
                        viewState.setSpinnerAdapter(cities)
                        cityToSettings(0)
                            //viewState.setCity(settings.city)
                    }
                    Log.d("my", "citiesGPS")
                },
                { t -> Log.d("my", t.message.toString()) })
        return cities
    }

    fun cityToSettings(position: Int) {
        if (position < cities.size) {

            with(cities[position]) {
                settings.city =
                    if (local_names?.ru == "") local_names.featureName else local_names?.ru.toString()
                settings.lat = lat.toString()
                settings.lon = lon.toString()
            }
            settings.position = position
        }
    }

    fun gpsSettingsChange(isChecked: Boolean) {
        settings.gpsKey = isChecked

        if (isChecked) {
            getGpsLocation()
        } else {
            viewState.setGPSText("")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getGpsLocation() {

        lateinit var locationCallback: LocationCallback
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            smallestDisplacement = 300f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    val location =
                        locationResult.lastLocation
                    val latLon =
                        "Latitude: " + location.latitude.toString() + " \nLongitude: " + location.longitude.toString()
                    viewState.setGPSText(latLon)
                    loadCityListGPS(location.latitude,location.longitude)
                    Log.d("my", latLon)
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    fun goToWeather() {
        router.newRootScreen(screens.weather())
    }


    fun backClick(): Boolean {
        Log.d("my", "settings back")
        router.exit()
        return true
    }
}