package com.mas.weather_kotlin.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentDayInfoBinding
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.presenter.DayInfoPresenter
import com.mas.weather_kotlin.mvp.view.DayInfoView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
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
    ): View {
        val fragmentView = FragmentDayInfoBinding.inflate(inflater, container, false).also {
            vb = it
        }.root
        val weatherFont =
            Typeface.createFromAsset(App.instance.resources.assets, "fonts/weather.ttf")
        vb?.sunriseIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_sunrise)
        }
        vb?.sunsetIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_sunset)
        }
        vb?.tvDailyPressureIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_barometer)
        }
        vb?.tvDailyHumidityIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_humidity)
        }
        vb?.tvDailyRainIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_umbrella)
        }
        vb?.tvDailyWindIco?.apply {
            typeface = weatherFont
            text = getString(R.string.wi_strong_wind)
        }
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

    override fun setTitle(text: String) {
        vb?.toolbar?.title = text
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

    override fun setDailyWind(text: String) {
        vb?.tvDailyWind?.text = text
    }

    override fun setWeatherIco(id: Int) {
        context?.let { vb?.ivWeatherIco?.setImageDrawable(ContextCompat.getDrawable(it, id)) }
    }

    override fun setBackground(background: Int?) {
        if (background == null) {
            vb?.llDaily?.background = null
        } else {
            context?.let { vb?.llDaily?.background = ContextCompat.getDrawable(it, background) }
        }
    }

    override fun setToolbarColor(toolbarColor: Int?) {
        if (toolbarColor == null) {
            vb?.toolbar?.background = null
        } else {
            context?.let { vb?.toolbar?.background = ContextCompat.getDrawable(it, toolbarColor) }
        }
    }

    override fun setSunMoonData(sunrise: String, sunset: String) {
        vb?.sunriseTime?.text = sunrise
        vb?.sunsetTime?.text = sunset
    }

    override fun setTempTable(listTemp: List<String>, listFlTemp: List<String>) {
        vb?.tempMorning?.text = listTemp[0]
        vb?.tempDay?.text = listTemp[1]
        vb?.tempEvening?.text = listTemp[2]
        vb?.tempNight?.text = listTemp[3]

        vb?.flMorning?.text = listFlTemp[0]
        vb?.flDay?.text = listFlTemp[1]
        vb?.flEvening?.text = listFlTemp[2]
        vb?.flNight?.text = listFlTemp[3]
    }
}