package com.mas.weather_kotlin.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentWeatherBinding
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import com.mas.weather_kotlin.ui.adapter.DailyRVAdapter
import com.mas.weather_kotlin.ui.adapter.HourlyRVAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import kotlin.math.roundToInt


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
    private var menuTemp = ""

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
        vb?.toolbarLayout?.setExpandedTitleTypeface(weatherFont)
        vb?.mainPressure?.typeface = weatherFont
        vb?.mainWindSpeed?.typeface = weatherFont
        vb?.sunrise?.typeface = weatherFont
        vb?.sunset?.typeface = weatherFont

        vb?.appBar?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            vb?.swipeRefresh?.isEnabled = verticalOffset == 0
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                vb?.toolbarLayout?.title = "${presenter.settings.city} ${menuTemp.drop(2)}"
            } else {
                vb?.toolbarLayout?.title = ""
            }
        })

        vb?.swipeRefresh?.setOnRefreshListener {
            presenter.getNewWeather()
            hideSwipeRefresh()
        }

        vb?.fab?.setOnClickListener {
            presenter.navigateSettings()
        }

        graphVisibility()

        return fragmentView
    }

    private fun graphVisibility() {
        if (presenter.showGraph()) {
            vb?.chart1?.visibility = View.VISIBLE
        } else {
            vb?.chart1?.visibility = View.GONE
        }
    }

    override fun onResume() {
        graphVisibility()
        //presenter.getNewWeather()
        super.onResume()
    }

    override fun setChart(
        data: CombinedData,
        xAxis: List<String>,
        lAxisMinMax: Pair<Float, Float>,
        rAxisMinMax: Pair<Float, Float>
    ) {
        with(vb?.chart1!!) {
            description?.text = "Daily weather"
            val grayColor: Int =
                context?.resources?.getColor(R.color.scrim_gray, context?.theme) ?: 0
            description?.textColor = grayColor

            this.xAxis.valueFormatter = IndexAxisValueFormatter(xAxis)
            this.xAxis.labelRotationAngle = -45f
            this.xAxis.textColor = grayColor
            this.xAxis.setLabelCount(xAxis.size + 1)
            this.xAxis.granularity = 1f
            this.xAxis.setDrawGridLinesBehindData(true)

            axisLeft?.axisMinimum = lAxisMinMax.first
            axisLeft?.axisMaximum = lAxisMinMax.second
            axisLeft?.textColor = grayColor
            axisRight?.axisMinimum = rAxisMinMax.first
            axisRight?.axisMaximum = rAxisMinMax.second
            axisRight?.textColor = grayColor

            legend.textColor = grayColor


            this.data = data
            description.isEnabled = true

            animateX(1)
            legend?.form = LegendForm.LINE
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e == null || h == null)
                        return
                    if (h.axis == YAxis.AxisDependency.RIGHT && xAxis.size >= e.x.roundToInt() && e.y > 0) {
                        showToast("On ${xAxis[e.x.roundToInt()]} rain (snow): ${e.y}")
                    }
                }

                override fun onNothingSelected() {}
            })
        }


    }

    fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun swipeRefreshVisible() {
        vb?.swipeRefresh?.isRefreshing = true
    }

    override fun hideSwipeRefresh() {
        if (vb?.swipeRefresh?.isRefreshing == true)
            vb?.swipeRefresh?.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun initRV() {
        vb?.rvHourly?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vb?.rvHourly?.adapter = HourlyRVAdapter(presenter.hourlyListPresenter)

        vb?.rvDaily?.layoutManager = LinearLayoutManager(requireContext())
        vb?.rvDaily?.adapter = DailyRVAdapter(presenter.dailyListPresenter)
    }

    override fun updateDailyList() {
        vb?.rvDaily?.adapter?.notifyDataSetChanged()
    }

    override fun updateHourlyList() {
        vb?.rvHourly?.adapter?.notifyDataSetChanged()
    }

    override fun setUpdate(update: String) {
        vb?.lastUpdate?.text = update
    }

    override fun setCurrentTemp(temp: String) {
        vb?.mainTemp?.text = temp
        menuTemp = temp
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
        vb?.tiMainCity?.text = cityName
    }

    override fun setCurrentWeatherIco(weatherIcoId: Int) {
        vb?.mainWeatherIco?.setImageDrawable(context?.getDrawable(weatherIcoId))
    }

    override fun setCurrentBackground(background: Int?) {
        if (background == null) {
            vb?.toolbarLayout?.background = null
        } else {
            vb?.toolbarLayout?.background = context?.getDrawable(background)
        }
    }

    override fun setCurrentToolbarColor(toolbarColor: Int?) {
        if (toolbarColor == null) {
            vb?.toolbarLayout?.contentScrim = null
        } else {
            vb?.toolbarLayout?.contentScrim = context?.getDrawable(toolbarColor)
        }
    }

    override fun hintVisible(visible: Boolean) {
        if (visible) {
            vb?.llHourly?.visibility = View.VISIBLE
        } else {
            vb?.llHourly?.visibility = View.INVISIBLE
        }
    }

    override fun backPressed() = presenter.backClick()

}