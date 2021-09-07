package com.mas.weather_kotlin.ui.activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.ActivityMainBinding
import com.mas.weather_kotlin.mvp.presenter.MainPresenter
import com.mas.weather_kotlin.mvp.view.MainView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainView {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    lateinit var sharedPref: SharedPreferences

    private val navigator = AppNavigator(this, R.id.container)
    private var vb: ActivityMainBinding? = null
    private val presenter by moxyPresenter {
        MainPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb?.root)

        sharedPref =
            baseContext.getSharedPreferences(App.instance.packageName, Context.MODE_PRIVATE)
        loadPreferences()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 123
            );

            Log.d("my", "Location false")
            return
        }

    }

    private fun loadPreferences() = with(App.settings) {
        city = sharedPref.getString("CITY", "").toString()
        lat = sharedPref.getString("LAT", "").toString()
        lon = sharedPref.getString("LON", "").toString()
        gpsKey = sharedPref.getBoolean("GPSKEY", false)
        percentRain = sharedPref.getBoolean("PERCENTRAIN", false)
        swTemp = sharedPref.getBoolean("SWTEMP", false)
        swWind = sharedPref.getBoolean("SWWIND", false)
        swRain = sharedPref.getBoolean("SWRAIN", false)
        jsonTxt = sharedPref.getString("OLDJSON", "").toString()
    }

    private fun savePreferences() = with(App.settings) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("CITY", city)
        editor.putString("LAT", lat)
        editor.putString("LON", lon)
        editor.putBoolean("GPSKEY", gpsKey)
        editor.putBoolean("PERCENTRAIN", percentRain)
        editor.putBoolean("SWTEMP", swTemp)
        editor.putBoolean("SWWIND", swWind)
        editor.putBoolean("SWRAIN", swRain)
        editor.putString("OLDJSON", jsonTxt)
        editor.apply()
        Log.d("my", "settings save")

    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
        navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackButtonListener && it.backPressed()) {
                return
            }
        }
        presenter.backClick()
    }

}