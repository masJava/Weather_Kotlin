package com.mas.weather_kotlin.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mas.weather_kotlin.databinding.FragmentDayInfoBinding
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.presenter.DayInfoPresenter
import com.mas.weather_kotlin.mvp.view.DayInfoView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import com.mas.weather_kotlin.ui.image.GlideImageLoader
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class DayInfoFragment : MvpAppCompatFragment(), DayInfoView, BackButtonListener {

    companion object {
        private const val DAY_ARG = "day"
        fun newInstance(day: DailyRestModel) = DayInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(DAY_ARG, day)
            }
        }
    }

    private val presenter by moxyPresenter {
        val day = arguments?.getParcelable<DailyRestModel>(DAY_ARG) as DailyRestModel
        DayInfoPresenter(day).apply {
            App.instance.appComponent.inject(this)
        }
    }

    private var vb: FragmentDayInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = FragmentDayInfoBinding.inflate(inflater, container, false).also {
        vb = it
    }.root
        val weatherFont = Typeface.createFromAsset(App.instance.resources.assets, "fonts/weather.ttf")
        vb?.tvDay?.typeface = weatherFont
        vb?.tvTempMin?.typeface = weatherFont
        vb?.tvTempMax?.typeface = weatherFont
        vb?.tvDailyRain?.typeface = weatherFont
        vb?.tvDailyHumidity?.typeface = weatherFont
        vb?.tvDailyPressure?.typeface = weatherFont
        return fragmentView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun backPressed() = presenter.backClick()

    override fun setDay(text: String) {
        vb?.tvDay?.text = text
    }

    override fun setTempMax(text: String) {
        vb?.tvTempMax?.text = text
    }

    override fun setTempMin(text: String) {
        vb?.tvTempMin?.text = text
    }

    override fun setDailyPressure(text: String) {
        vb?.tvDailyPressure?.text = text
    }

    override fun setDailyHumidity(text: String) {
        vb?.tvDailyHumidity?.text = text
    }

    override fun setDailyRain(text: String) {
        vb?.tvDailyRain?.text = text
    }

    override fun loadWeatherIco(url: String) {
        vb?.ivWeatherIco?.let { GlideImageLoader().load(url, it) }
    }

}