//package com.mas.weather_kotlin.ui.gps
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Looper
//import android.util.Log
//import com.google.android.gms.location.*
//import com.mas.weather_kotlin.mvp.model.entity.GpsLocation
//import com.mas.weather_kotlin.mvp.model.gps.IGPSLocation
//import io.reactivex.rxjava3.core.Observable
//
//@SuppressLint("MissingPermission")
//class AndroidGPSLocation(context: Context) : IGPSLocation {
//    private var fusedLocationClient: FusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(context)
//
//    private lateinit var locationCallback: LocationCallback
//
//    private var locationRequest: LocationRequest = LocationRequest.create().apply {
//        interval = 10000
//        fastestInterval = 5000
//        smallestDisplacement = 300f
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
//    }
//
//    public var gpsLocation: Observable<GpsLocation> = Observable.just(GpsLocation())
//
//    init {
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//                if (locationResult.locations.isNotEmpty()) {
//                    gpsLocation = Observable.just(GpsLocation(
//                        locationResult.lastLocation.latitude,
//                        locationResult.lastLocation.longitude
//                    ))
////                    val latLon =
////                        "Latitude: " + location.latitude.toString() + " \nLongitude: " + location.longitude.toString()
////                    Log.d("my", latLon)
//                    fusedLocationClient.removeLocationUpdates(locationCallback)
//                }
//            }
//        }
//        val requestLocationUpdates = fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.getMainLooper() /* Looper */
//        )
//    }
//
//
//    override fun getGPSLocation(): GpsLocation {
//        var location = GpsLocation()
//        return location
//    }
//
//}