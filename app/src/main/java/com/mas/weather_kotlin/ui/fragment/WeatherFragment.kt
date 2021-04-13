package com.mas.weather_kotlin.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentWeatherBinding
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import com.mas.weather_kotlin.ui.adapter.DailyRVAdapter
import com.mas.weather_kotlin.ui.adapter.HourlyRVAdapter
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

    private var vb: FragmentWeatherBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = FragmentWeatherBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

        val weatherFont = Typeface.createFromAsset(resources.assets, "fonts/weather.ttf")

        vb?.mainTemp?.typeface = weatherFont
        vb?.mainHum?.typeface = weatherFont
        vb?.mainPressure?.typeface = weatherFont
        vb?.mainWindSpeed?.typeface = weatherFont
        vb?.sunrise?.typeface = weatherFont
        vb?.sunset?.typeface = weatherFont

        vb?.appBar?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                //  Collapsed
                vb?.toolbarLayout?.title = presenter.settings.city

            } else {
                //Expanded
                vb?.toolbarLayout?.title = ""
            }
        })

        vb?.fab?.setOnClickListener {
            presenter.navigateSettings()
        }

        return fragmentView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun initRV() {
        //подключение адаптеров списков
        vb?.rvHourly?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vb?.rvHourly?.adapter = HourlyRVAdapter(presenter.hourlyListPresenter, GlideImageLoader())
        vb?.rvHourly?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.HORIZONTAL
            )
        )

        vb?.rvDaily?.layoutManager = LinearLayoutManager(requireContext())
        vb?.rvDaily?.adapter = DailyRVAdapter(presenter.dailyListPresenter, GlideImageLoader())
        vb?.rvDaily?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun updateDailyList() {
        //обновление адаптеров списков
        vb?.rvDaily?.adapter?.notifyDataSetChanged()
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