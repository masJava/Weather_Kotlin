package com.mas.weather_kotlin.mvp.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.terrakok.cicerone.Router
import com.google.android.gms.location.*
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.Tools
import com.mas.weather_kotlin.mvp.model.entity.GpsLocation
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import com.mas.weather_kotlin.mvp.model.entity.current.CurrentRestModel
import com.mas.weather_kotlin.mvp.model.entity.daily.DailyRestModel
import com.mas.weather_kotlin.mvp.model.entity.hourly.HourlyRestModel
import com.mas.weather_kotlin.mvp.model.round1
import com.mas.weather_kotlin.mvp.model.toStrTime
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.presenter.list.IDailyListPresenter
import com.mas.weather_kotlin.mvp.presenter.list.IHourlyListPresenter
import com.mas.weather_kotlin.mvp.repo.IOpenWeather
import com.mas.weather_kotlin.mvp.view.WeatherView
import com.mas.weather_kotlin.mvp.view.list.IDailyItemView
import com.mas.weather_kotlin.mvp.view.list.IHourItemView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.weatherToWidget
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt


class WeatherPresenter() : MvpPresenter<WeatherView>() {

    @Inject
    lateinit var weather: IOpenWeather

    @Inject
    lateinit var screens: IScreens

    @Inject
    lateinit var router: Router

    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler


    class DailyListPresenter : IDailyListPresenter {
        val dailyWeather = mutableListOf<DailyRestModel>()
        var timeZone = 0L
        override var itemClickListener: ((IDailyItemView) -> Unit)? = null
        var percentRain: Boolean = false

        override fun bindView(view: IDailyItemView) {
            val day = dailyWeather[view.pos]
            val time = day.dt.toStrTime(Tools().PATTERN_EEE_D_MMM, timeZone)
            var maxT = ""
            var minT = ""
            var humidity: String
            var pressure: String
            var rain: String
            with(App.instance.resources) {
                if (day.temp != null) {
                    maxT =
                        getString(R.string.wi_day_sunny) + " %.1f\u00b0C".format(day.temp.day.round1())
                    minT =
                        getString(R.string.wi_night_clear) + "  %.1f\u00b0C".format(day.temp.night.round1())
                }
                humidity = "${getString(R.string.wi_humidity)} ${day.humidity} %"
                pressure =
                    "${getString(R.string.wi_barometer)} ${(day.pressure / 1.333).roundToInt()} " + App.instance.getString(
                        R.string.pressure_mm_Hg
                    )

                if (percentRain) {
                    rain = "${getString(R.string.wi_umbrella)} ${(day.pop * 100).roundToInt()} % "
                } else {
                    rain = "${getString(R.string.wi_umbrella)} %.2f ".format(day.rain) +
                            App.instance.getString(R.string.rain_mm)
                }

            }

            view.setDay(time)
            view.setTempMax(maxT)
            view.setTempMin(minT)
            view.setDailyHumidity(humidity)
            view.setDailyPressure(pressure)
            view.setDailyRain(rain)
            val weatherIcoId = Tools().getIconId(day.weather?.get(0)?.icon)
            view.loadWeatherIco(weatherIcoId)
        }

        override fun getCount() = dailyWeather.size
    }

    class HourlyListPresenter : IHourlyListPresenter {
        val hourlyWeather = mutableListOf<HourlyRestModel>()
        var timeZone = 0L
        override var itemClickListener: ((IHourItemView) -> Unit)? = null
        var percentRain: Boolean = false

        override fun bindView(view: IHourItemView) {
            val hour = hourlyWeather[view.pos]
            var time = hour.dt.toStrTime(Tools().PATTERN_HH_MM, timeZone)
            if (time.equals("00:00")) {
                time = hour.dt.toStrTime(Tools().PATTERN_DD_MM, timeZone) + "\n" + time
            } else time = "\n" + time
            view.setHour(time)

            val rain = if (percentRain) {
                if (hour.rain != null) "${(hour.pop * 100).roundToInt()}%" else ""
            } else {
                if (hour.rain != null) hour.rain.rain.toString() else ""
            }

            view.setRainfall(rain)

            view.setTemp("%.1f".format(hour.temp.round1()))

//            val weatherIcoUrl = "https://openweathermap.org/img/wn/%s@2x.png".format(hour.weather?.get(0)?.icon)
            val weatherIcoId = Tools().getIconId(hour.weather?.get(0)?.icon)
            view.loadWeatherIco(weatherIcoId)
        }

        override fun getCount() = hourlyWeather.size
    }

    var currentWeather = CurrentRestModel()
    var hourlyListPresenter = HourlyListPresenter()
    var dailyListPresenter = DailyListPresenter()

    override fun onFirstViewAttach() {
        Tools().parseJson(App.settings.jsonTxt)
            .observeOn(uiScheduler)
            .subscribe(
                {
                    if (it != null) {
                        setWeatherToView(it)
                    }
                },
                { t -> Log.d("my", t.message.toString()) })
        getNewWeather()
    }

    fun getNewWeather() {
        super.onFirstViewAttach()
        viewState.swipeRefreshVisible()
        if (App.settings.gpsKey) {
            getGpsLocation()
        } else {
            loadData()
        }
        viewState.initRV()

        dailyListPresenter.itemClickListener = { view ->
            val day = dailyListPresenter.dailyWeather[view.pos]
            Log.d("my", day.temp.toString())
            router.navigateTo(screens.dayInfo(day))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getGpsLocation() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(App.instance.baseContext)

        lateinit var locationCallback: LocationCallback

        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            smallestDisplacement = 300f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = GpsLocation(
                        (locationResult.lastLocation.latitude * 100).roundToInt() / 100.0,
                        (locationResult.lastLocation.longitude * 100).roundToInt() / 100.0,
                    )
                    Log.d(
                        "my",
                        "Latitude: " + location.latitude.toString() + " \nLongitude: " + location.longitude.toString()
                    )
                    App.settings.lat = location.latitude.toString()
                    App.settings.lon = location.longitude.toString()

                    loadCityListGPS(location.latitude, location.longitude)
                    // loadData()

                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
        val requestLocationUpdates = fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper() /* Looper */
        )
    }

    fun loadCityListGPS(lat: Double, lon: Double) {
        viewState.setCurrentCityName(App.settings.city)
        viewState.swipeRefreshVisible()
//        google geo
        geo(lat, lon, 1)
            .observeOn(uiScheduler)
            .subscribe({
                if (it != null)
                    if (it.count() > 0) {
                        App.settings.city = it.get(0).locality
                        loadData()
                        viewState.setCurrentCityName(App.settings.city)
                    }
                Log.d("myGeo", "googleGeo")
            }, { t ->
                Log.d("myGeo", t.message.toString())
                openWeatherGeocode(lat, lon)
            }
            )


    }

    private fun openWeatherGeocode(lat: Double, lon: Double) {
        weather.getCitiesGPS(lat, lon)
            .observeOn(uiScheduler)
            .subscribe(
                { citiesRequest ->
                    if (citiesRequest != null) {
                        if (citiesRequest.count() > 0) {
                            App.settings.city = with(citiesRequest[0]) {
                                if (local_names?.ru == "") local_names.featureName else local_names?.ru.toString()
                            }
                            loadData()
                            viewState.setCurrentCityName(App.settings.city)
                        }
                    }
                    Log.d("myGeo", "openweatherGeo")
                },
                { t -> Log.d("my", t.message.toString()) })
    }

    private fun geo(lat: Double, lon: Double, maxResult: Int) =
        Single.fromCallable {
            Geocoder(App.instance.baseContext).getFromLocation(lat, lon, maxResult)
        }

    fun loadData() {
        Log.d("my", "LoadData")
        weather.getJsonStr(App.settings.lat, App.settings.lon)
            .observeOn(uiScheduler)
            .subscribe(
                { jsonWeather ->
                    App.settings.jsonTxt = jsonWeather.toString()
                    Tools().parseJson(App.settings.jsonTxt)
                        .observeOn(uiScheduler)
                        .subscribe(
                            { weatherRequestRestModel ->
                                if (weatherRequestRestModel != null) {
                                    setWeatherToView(weatherRequestRestModel)
                                    weatherToWidget(weatherRequestRestModel)
                                }
                            },
                            { t -> Log.d("my", t.message.toString()) })
//                    Log.d("my", jsonWeather.toString())
                },
                { t -> Log.d("my", t.message.toString()) }
            )
    }

    private fun setWeatherToView(weather: WeatherRequestRestModel) {
        App.settings.timeZone = weather.timezone_offset
        if (weather.current != null) {
            currentWeather = weather.current
            currentWeatherUpdate()
        }
        if (weather.hourly != null) {
            viewState.hintVisible(true)
            hourlyListPresenter.hourlyWeather.clear()
            hourlyListPresenter.hourlyWeather.addAll(weather.hourly)
            hourlyListPresenter.timeZone = App.settings.timeZone
            hourlyListPresenter.percentRain = App.settings.percentRain
            viewState.updateHourlyList()
        } else {
            viewState.hintVisible(false)
        }
        if (weather.daily != null) {
            dailyListPresenter.dailyWeather.clear()
            dailyListPresenter.dailyWeather.addAll(weather.daily)
            dailyListPresenter.timeZone = App.settings.timeZone
            dailyListPresenter.percentRain = App.settings.percentRain
            viewState.updateDailyList()

            dataToChart(weather.daily)
        }
        viewState.hideSwipeRefresh()
    }

    private fun dataToChart(daily: List<DailyRestModel>) {
        val dailyTemp = mutableListOf<Entry>()
        val dailyWind = mutableListOf<Entry>()
        val dailyRain = mutableListOf<BarEntry>()
        val xAxisText = mutableListOf("")
        var num = 1f
        val data = CombinedData()
        var lAxisMinMax = Pair(0f, 0f)
        var rAxisMinMax = Pair(0f, 12f)

        daily.forEach { day ->
            val xLabel = day.dt.toStrTime("d.MM", App.settings.timeZone)
            xAxisText.add(xLabel)

            if (App.settings.swWind) {
                dailyWind.add(Entry(num, day.wind_speed.round1()))
                lAxisMinMax = getMinMaxPair(lAxisMinMax, day.wind_speed.round1())
            }

            if (App.settings.swRain) {
                dailyRain.add(BarEntry(num, day.rain.round1()))
            }
            rAxisMinMax =
                getMinMaxPair(rAxisMinMax, day.rain.round1())
            if (App.settings.swTemp) {
                with(day.temp!!) {
                    dailyTemp.add(Entry(num++, this.day.round1()))
                    xAxisText.add(" ")
                    dailyTemp.add(Entry(num++, night.round1()))
                    if (lAxisMinMax == Pair(0f, 0f)) {
                        lAxisMinMax = Pair(night.round1(), night.round1())
                    } else {
                        lAxisMinMax = getMinMaxPair(lAxisMinMax, this.day.round1())
                        lAxisMinMax = getMinMaxPair(lAxisMinMax, night.round1())
                    }
                }
            }
            if (App.settings.swWind || (App.settings.swRain && !App.settings.swTemp))
                num++


        }

        rAxisMinMax = Pair(rAxisMinMax.first, rAxisMinMax.second + 3)


//        if (lAxisMinMax.first >= 0)
        lAxisMinMax = if (App.settings.swWind) {
            Pair(lAxisMinMax.first, lAxisMinMax.second + 3)
        } else {
            if (!App.settings.swTemp) {
                Pair(0f, 0f)
            } else {
                if (lAxisMinMax.first > 10) {
                    Pair(lAxisMinMax.first - 10, lAxisMinMax.second + 3)
                } else {
                    Pair(lAxisMinMax.first - 3, lAxisMinMax.second + 3)
                }
            }
        }


        val colorRed = Color.rgb(250, 0, 0)
        val colorWind = Color.rgb(63, 81, 181)
        val colorRain = Color.rgb(0, 181, 212)

        val setTemp = LineDataSet(dailyTemp, "Daily temp").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setColor(colorRed)
            setLineWidth(2.5f)
            setCircleColor(colorRed)
            setCircleHoleColor(colorRed)
            setCircleRadius(3f)
            setMode(LineDataSet.Mode.HORIZONTAL_BEZIER)
            setDrawValues(true)
            setValueTextSize(11f)
            setValueTextColor(colorRed)
            valueFormatter = DefaultValueFormatter(1)
        }

        val setWind = LineDataSet(dailyWind, "Daily wind").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setColor(colorWind)
            setLineWidth(2.5f)
            setCircleColor(colorWind)
            setCircleHoleColor(colorWind)
            setCircleRadius(3f)
            setMode(LineDataSet.Mode.HORIZONTAL_BEZIER)
            setDrawValues(true)
            setValueTextSize(11f)
            setValueTextColor(colorWind)
            valueFormatter = DefaultValueFormatter(1)
        }

        val setRain = BarDataSet(dailyRain, "Daily rain").apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            setColor(colorRain)
//                setLineWidth(2.5f)
//                setCircleColor(colorBlue)
//                setCircleRadius(5f)
//                setMode(LineDataSet.Mode.LINEAR)
            setDrawValues(true)
            setValueTextSize(11f)
            setValueTextColor(colorRain)
            this.setDrawValues(false)
            valueFormatter = DefaultValueFormatter(1)

        }

        val lineDataSets = ArrayList<ILineDataSet>()
        if (App.settings.swTemp) lineDataSets.add(setTemp)
        if (App.settings.swWind) lineDataSets.add(setWind)
        val lineData = LineData(lineDataSets)

        val barDataSets = ArrayList<IBarDataSet>()
        if (App.settings.swRain) barDataSets.add(setRain)
        val barData = BarData(barDataSets)

        data.setData(lineData)
        data.setData(barData)

        viewState.setChart(data, xAxisText, lAxisMinMax, rAxisMinMax)
    }

    private fun getMinMaxPair(pair: Pair<Float, Float>, num: Float): Pair<Float, Float> =
        Pair(Math.min(pair.first, num), Math.max(pair.second, num))

    fun showGraph(): Boolean {
        return App.settings.swRain || App.settings.swTemp || App.settings.swWind
    }

    private fun currentWeatherUpdate() {
        var temp: String
        var hum: String
        var sunrise = ""
        var sunset = ""
        var wind: String
        var press: String
        var update: String
        var weatherIcoId: Int
        var background: Int?
        var toolbarColor: Int?
        with(App.instance.resources) {
            temp =
                "${getString(R.string.wi_thermometer)} %.1f \u00b0C".format(currentWeather.temp.round1())
            hum = "${getString(R.string.wi_humidity)} ${currentWeather.humidity} %"
            press =
                "${getString(R.string.wi_barometer)} ${(currentWeather.pressure / 1.333).roundToInt()} " +
                        App.instance.getString(R.string.pressure_mm_Hg)
            wind =
                "${getString(R.string.wi_strong_wind)} %s %s ".format(
                    Tools().getWindDirection(currentWeather.wind_deg),
                    currentWeather.wind_speed.roundToInt()
                ) + App.instance.getString(R.string.wind_m_s)

            if (currentWeather.sunrise > 0) {
                sunrise =
                    getString(R.string.wi_sunrise) + " " + currentWeather.sunrise.toStrTime(
                        Tools().PATTERN_HH_MM,
                        App.settings.timeZone
                    )
            }

            if (currentWeather.sunset > 0) {
                sunset = getString(R.string.wi_sunset) + " " +
                        currentWeather.sunset.toStrTime(
                            Tools().PATTERN_HH_MM,
                            App.settings.timeZone
                        )
            }

            update =
                "${App.instance.getString(R.string.weather_updated)} " + currentWeather.dt.toStrTime(
                    Tools().PATTERN_FULL_DATE_UPD,
                    GregorianCalendar().timeZone.rawOffset / 1000L
                )

            weatherIcoId = Tools().getIconId(currentWeather.weather?.get(0)?.icon)
            background = Tools().getBackgroundGradient(currentWeather.weather?.get(0)?.icon)
            toolbarColor = Tools().getScrimColor(currentWeather.weather?.get(0)?.icon)
        }



        if (!App.settings.gpsKey) viewState.setCurrentCityName(App.settings.city)
        viewState.setUpdate(update)
        viewState.setCurrentTemp(temp)
        viewState.setCurrentHum(hum)
        viewState.setCurrentPress(press)
        viewState.setCurrentSunrise(sunrise)
        viewState.setCurrentSunset(sunset)
        viewState.setCurrentWind(wind)
        viewState.setCurrentWeatherIco(weatherIcoId)
        viewState.setCurrentBackground(background)
        viewState.setCurrentToolbarColor(toolbarColor)
    }

    fun backClick(): Boolean {
        Log.d("my", "weather back")
        router.exit()
        return true
    }

    fun navigateSettings() {
        router.navigateTo(screens.settings())
    }
}
