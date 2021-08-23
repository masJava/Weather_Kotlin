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
import com.google.gson.Gson
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.mvp.model.Tools
import com.mas.weather_kotlin.mvp.model.entity.GpsLocation
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
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

    @Inject
    lateinit var settings: SettingsModel

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
                        getString(R.string.wi_direction_up) + " %.1f\u00b0C".format(day.temp.max.round1())
                    minT =
                        getString(R.string.wi_direction_down) + " %.1f\u00b0C".format(day.temp.min.round1())
                }
                humidity = "${getString(R.string.wi_humidity)} ${day.humidity} %"
                pressure =
                    "${getString(R.string.wi_barometer)} ${(day.pressure / 1.333).roundToInt()} mm Hg"

                if (percentRain) {
                    rain = "${getString(R.string.wi_umbrella)} ${(day.pop * 100).roundToInt()} % "
                } else {
                    rain = "${getString(R.string.wi_umbrella)} %.2f mm".format(day.rain)
                }

            }

            view.setDay(time)
            view.setTempMax(maxT)
            view.setTempMin(minT)
            view.setDailyHumidity(humidity)
            view.setDailyPressure(pressure)
            view.setDailyRain(rain)
//            val weatherIcoId = "https://openweathermap.org/img/wn/%s@2x.png".format(day.weather?.get(0)?.icon)
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
    var timeZone = 0L

    override fun onFirstViewAttach() {
        parseJson(settings.jsonTxt)
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
        if (settings.gpsKey) {
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
                    settings.lat = location.latitude.toString()
                    settings.lon = location.longitude.toString()

                    loadCityListGPS(location.latitude, location.longitude)
                    loadData()

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
        viewState.setCurrentCityName(settings.city)
        viewState.swipeRefreshVisible()
//        google geo
        geo(lat, lon, 1)
            .observeOn(uiScheduler)
            .subscribe({
                if (it != null)
                    if (it.count() > 0) {
                        settings.city = it.get(0).locality
                        loadData()
                        viewState.setCurrentCityName(settings.city)
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
                            settings.city = with(citiesRequest[0]) {
                                if (local_names?.ru == "") local_names.featureName else local_names?.ru.toString()
                            }
                            loadData()
                            viewState.setCurrentCityName(settings.city)
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

    fun parseJson(json: String): Single<WeatherRequestRestModel?> =
        if (json.isNotBlank()) {
            Single.fromCallable { Gson().fromJson(json, WeatherRequestRestModel::class.java) }
        } else {
            Single.fromCallable { null }
        }

    private fun loadData() {
        viewState.swipeRefreshVisible()
        Log.d("my", "LoadData")
        weather.getJsonStr(settings.lat, settings.lon)
            .observeOn(uiScheduler)
            .subscribe(
                { jsonWeather ->
                    settings.jsonTxt = jsonWeather.toString()
                    parseJson(settings.jsonTxt)
                        .observeOn(uiScheduler)
                        .subscribe(
                            {
                                if (it != null) {
                                    setWeatherToView(it)
                                }
                            },
                            { t -> Log.d("my", t.message.toString()) })
//                    Log.d("my", jsonWeather.toString())
                },
                { t -> Log.d("my", t.message.toString()) }
            )

//        weather.getWeather(settings.lat, settings.lon)
//            .observeOn(uiScheduler)
//            .subscribe(
//                { weather ->
//                    if (weather != null) {
//                        setWeatherToView(weather)
//                    }
//                    Log.d("my", "weather")
//                },
//                { t -> Log.d("my", t.message.toString()) })


    }

    private fun setWeatherToView(weather: WeatherRequestRestModel) {
        timeZone = weather.timezone_offset
        if (weather.current != null) {
            currentWeather = weather.current
            currentWeatherUpdate()
        }
        if (weather.hourly != null) {
            viewState.hintVisible(true)
            hourlyListPresenter.hourlyWeather.clear()
            hourlyListPresenter.hourlyWeather.addAll(weather.hourly)
            hourlyListPresenter.timeZone = timeZone
            hourlyListPresenter.percentRain = settings.percentRain
            viewState.updateHourlyList()
        } else {
            viewState.hintVisible(false)
        }
        if (weather.daily != null) {
            dailyListPresenter.dailyWeather.clear()
            dailyListPresenter.dailyWeather.addAll(weather.daily)
            dailyListPresenter.timeZone = timeZone
            dailyListPresenter.percentRain = settings.percentRain
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
            val xLabel = day.dt.toStrTime("d.MM", timeZone)
            xAxisText.add(xLabel)
            if (settings.swTemp) {
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
            if (settings.swWind) {
                dailyWind.add(Entry(num, day.wind_speed.round1()))
                lAxisMinMax = getMinMaxPair(lAxisMinMax, day.wind_speed.round1())
            }

            if (settings.swRain) {
                dailyRain.add(BarEntry(num, day.rain.round1()))
            }
            rAxisMinMax =
                getMinMaxPair(rAxisMinMax, day.rain.round1())
            if (settings.swWind || (settings.swRain && !settings.swTemp))
                num++


        }

        rAxisMinMax = Pair(rAxisMinMax.first, rAxisMinMax.second + 3)


//        if (lAxisMinMax.first >= 0)
        lAxisMinMax = if (settings.swWind) {
            Pair(lAxisMinMax.first, lAxisMinMax.second + 3)
        } else {
            if (!settings.swTemp) {
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
        val colorBlack = Color.rgb(0, 0, 0)

        val setTemp = LineDataSet(dailyTemp, "Daily temp").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setColor(colorRed)
            setLineWidth(2.5f)
            setCircleColor(colorRed)
            setCircleRadius(5f)
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
            setCircleRadius(5f)
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
        if (settings.swTemp) lineDataSets.add(setTemp)
        if (settings.swWind) lineDataSets.add(setWind)
        val lineData = LineData(lineDataSets)

        val barDataSets = ArrayList<IBarDataSet>()
        if (settings.swRain) barDataSets.add(setRain)
        val barData = BarData(barDataSets)

        data.setData(lineData)
        data.setData(barData)

        viewState.setChart(data, xAxisText, lAxisMinMax, rAxisMinMax)
    }

    private fun getMinMaxPair(pair: Pair<Float, Float>, num: Float): Pair<Float, Float> =
        Pair(Math.min(pair.first, num), Math.max(pair.second, num))

    fun showGraph(): Boolean {
        return settings.swRain || settings.swTemp || settings.swWind
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
        with(App.instance.resources) {
            temp =
                "${getString(R.string.wi_thermometer)} %.1f \u00b0C".format(currentWeather.temp.round1())
            hum = "${getString(R.string.wi_humidity)} ${currentWeather.humidity} %"
            press =
                "${getString(R.string.wi_barometer)} ${(currentWeather.pressure / 1.333).roundToInt()} mm Hg"
            wind = "${getString(R.string.wi_strong_wind)} %s %s m/s"
                .format(
                    getWindDirection(currentWeather.wind_deg),
                    currentWeather.wind_speed.roundToInt()
                )

            if (currentWeather.sunrise > 0) {
                sunrise = getString(R.string.wi_sunrise) + " " + currentWeather.sunrise.toStrTime(
                    Tools().PATTERN_HH_MM,
                    timeZone
                )
            }

            if (currentWeather.sunset > 0) {
                sunset = getString(R.string.wi_sunset) + " " +
                        currentWeather.sunset.toStrTime(
                            Tools().PATTERN_HH_MM,
                            timeZone
                        )
            }

            update = "Updated: " + currentWeather.dt.toStrTime(
                Tools().PATTERN_FULL_DATE_UPD,
                GregorianCalendar().timeZone.rawOffset / 1000L
            )

//            weatherIcoId = "https://openweathermap.org/img/wn/%s@2x.png".format(currentWeather.weather?.get(0)?.icon)
            weatherIcoId = Tools().getIconId(currentWeather.weather?.get(0)?.icon)
        }



        if (!settings.gpsKey) viewState.setCurrentCityName(settings.city)
        viewState.setUpdate(update)
        viewState.setCurrentTemp(temp)
        viewState.setCurrentHum(hum)
        viewState.setCurrentPress(press)
        viewState.setCurrentSunrise(sunrise)
        viewState.setCurrentSunset(sunset)
        viewState.setCurrentWind(wind)
        viewState.setCurrentWeatherIco(weatherIcoId)
    }

    fun backClick(): Boolean {
        Log.d("my", "weather back")
        router.exit()
        return true
    }

    fun getWindDirection(deg: Int): String {
        var dirString: String
        with(App.instance.resources) {
            if (deg == 0 || deg == 360) {
                dirString = getString(R.string.wind_direction_north)
                dirString += " " + getString(R.string.wi_direction_down)
            } else if (deg > 0 && deg < 90) {
                dirString = getString(R.string.wind_direction_north_east)
                dirString += " " + getString(R.string.wi_direction_down_left)
            } else if (deg == 90) {
                dirString = getString(R.string.wind_direction_east)
                dirString += " " + getString(R.string.wi_direction_left)
            } else if (deg > 90 && deg < 180) {
                dirString = getString(R.string.wind_direction_south_east)
                dirString += " " + getString(R.string.wi_direction_up_left)
            } else if (deg == 180) {
                dirString = getString(R.string.wind_direction_south)
                dirString += " " + getString(R.string.wi_direction_up)
            } else if (deg > 180 && deg < 270) {
                dirString = getString(R.string.wind_direction_south_west)
                dirString += " " + getString(R.string.wi_direction_up_right)
            } else if (deg == 270) {
                dirString = getString(R.string.wind_direction_west)
                dirString += " " + getString(R.string.wi_direction_right)
            } else {
                dirString = getString(R.string.wind_direction_north_west)
                dirString += " " + getString(R.string.wi_direction_down_right)
            }
        }
        return dirString
    }

    fun navigateSettings() {
        router.navigateTo(screens.settings())
    }
}

//private fun Long.toStrTime(pattern: String?, timeZoneOffset: Long): String {
//    val calendar: Calendar = GregorianCalendar()
//    val localOffset = calendar.timeZone.rawOffset
//    val targetTimeStamp = this * 1000 - localOffset + timeZoneOffset * 1000
//    val date = Date(targetTimeStamp)
//    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//    return sdf.format(date)
//}
