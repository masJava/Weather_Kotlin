package com.mas.weather_kotlin.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mas.weather_kotlin.databinding.FragmentUsersBinding
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import com.mas.weather_kotlin.ui.adapter.HourlyRVAdapter
import com.mas.weather_kotlin.ui.adapter.UsersRVAdapter
import com.mas.weather_kotlin.ui.image.GlideImageLoader
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class WeatherFragment : MvpAppCompatFragment(), WeatherView, BackButtonListener {

    companion object {
        fun newInstance() = WeatherFragment()
    }

    private val presenter by moxyPresenter {
        WeatherPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    private var vb: FragmentUsersBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = FragmentUsersBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

        var weatherFont = Typeface.createFromAsset(resources.assets, "fonts/weather.ttf")

        vb?.mainTemp?.typeface = weatherFont
        vb?.mainHum?.typeface = weatherFont
        vb?.mainPressure?.typeface = weatherFont
        vb?.mainWindSpeed?.typeface = weatherFont
        vb?.sunrise?.typeface = weatherFont
        vb?.sunset?.typeface = weatherFont

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun initRV() {
        //подключение адаптеров списков
        vb?.rvHourly?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        vb?.rvHourly?.adapter = HourlyRVAdapter(presenter.hourlyListPresenter, GlideImageLoader())
    }

    override fun updateDailyList() {
        //обновление адаптеров списков
//        vb?.rvUsers?.adapter?.notifyDataSetChanged()
    }

    override fun updateHourlyList() {
        //обновление адаптеров списков
        vb?.rvHourly?.adapter?.notifyDataSetChanged()
    }

    override fun setUpdate(update: String) {
        vb?.lastUpdate?.text = update
    }

    override fun setCurrentTemp(temp: String) {
        vb?.mainTemp?.text = temp
    }

    override fun setCurrentHum(hum: String) {
        vb?.mainHum?.text = hum
    }

    override fun setCurrentWind(wind: String) {
        vb?.mainWindSpeed?.text = wind
    }

    override fun setCurrentPress(press: String) {
        vb?.mainPressure?.text = press
    }

    override fun setCurrentSunrise(sunrise: String) {
        vb?.sunrise?.text = sunrise
    }

    override fun setCurrentSunset(sunset: String) {
        vb?.sunset?.text = sunset
    }

    override fun setCurrentCityName(cityName: String) {
        vb?.mainCity?.text = cityName
    }

    override fun setCurrentWeatherIco(weatherIcoId: String) {
        vb?.mainWeatherIco?.let { GlideImageLoader().load(weatherIcoId, it) }
    }

    override fun backPressed() = presenter.backClick()

}