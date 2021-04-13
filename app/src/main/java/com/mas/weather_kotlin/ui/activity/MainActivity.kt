package com.mas.weather_kotlin.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.ActivityMainBinding
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
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

    @Inject
    lateinit var settings: SettingsModel

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

        sharedPref = baseContext.getSharedPreferences("WEATHER", Context.MODE_PRIVATE)
        loadPreferences()
    }

    private fun loadPreferences() {
        settings.city = sharedPref.getString("CITY", "").toString()
        settings.lat = sharedPref.getString("LAT", "").toString()
        settings.lon = sharedPref.getString("LON", "").toString()
        settings.position = sharedPref.getInt("POSITION", 0)
    }

    private fun savePreferences() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("CITY", settings.city)
        editor.putString("LAT", settings.lat)
        editor.putString("LON", settings.lon)
        editor.putInt("POSITION", settings.position)
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