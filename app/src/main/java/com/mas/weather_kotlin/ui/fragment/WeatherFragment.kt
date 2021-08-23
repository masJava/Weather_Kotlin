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
import com.mas.weather_kotlin.databinding.FragmentWeatherBinding
import com.mas.weather_kotlin.mvp.presenter.WeatherPresenter
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import com.mas.weather_kotlin.ui.adapter.DailyRVAdapter
import com.mas.weather_kotlin.ui.adapter.HourlyRVAdapter
import com.mas.weather_kotlin.ui.image.GlideImageLoader
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
            vb?.swipeRefresh?.isEnabled = verticalOffset == 0
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                vb?.toolbarLayout?.title = presenter.settings.city
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
    )
//                (dailyTemp: List<Entry>, dailyWind: List<Entry>, xAxis: List<String>, dailyRain: List<BarEntry>)
    {
        with(vb?.chart1!!) {
            description?.text = "Daily weather"

            this.xAxis.valueFormatter = IndexAxisValueFormatter(xAxis)
            this.xAxis.labelRotationAngle = -45f
//            this.xAxis.setLabelCount(xAxis.count(), true)

            axisLeft?.axisMinimum = lAxisMinMax.first
            axisLeft?.axisMaximum = lAxisMinMax.second
            axisRight?.axisMinimum = rAxisMinMax.first
            axisRight?.axisMaximum = rAxisMinMax.second


            this.data = data
            description.isEnabled = true

            animateX(1000)
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
        vb?.rvHourly?.adapter = HourlyRVAdapter(presenter.hourlyListPresenter, GlideImageLoader())
//        vb?.rvHourly?.addItemDecoration(
//            DividerItemDecoration(
//                requireContext(),
//                LinearLayoutManager.HORIZONTAL
//            )
//        )

        vb?.rvDaily?.layoutManager = LinearLayoutManager(requireContext())
        vb?.rvDaily?.adapter = DailyRVAdapter(presenter.dailyListPresenter, GlideImageLoader())
//        vb?.rvDaily?.addItemDecoration(
//            DividerItemDecoration(
//                requireContext(),
//                LinearLayoutManager.VERTICAL
//            )
//        )
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
//        vb?.mainWeatherIco?.let { GlideImageLoader().load(weatherIcoId, it) }
        vb?.mainWeatherIco?.setImageDrawable(context?.getDrawable(weatherIcoId))
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