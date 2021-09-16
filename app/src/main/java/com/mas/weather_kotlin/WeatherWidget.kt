package com.mas.weather_kotlin

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mas.weather_kotlin.mvp.model.*
import com.mas.weather_kotlin.mvp.model.api.IDataSource
import com.mas.weather_kotlin.mvp.model.entity.WeatherRequestRestModel
import com.mas.weather_kotlin.mvp.repo.RetrofitWeather
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.network.AndroidNetworkStatus
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [WeatherWidgetConfigureActivity]
 */


class WeatherWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val widgetType = loadWidgetType(context, appWidgetId)
            if (widgetType.equals(PREF_WIDGET_DAILY) || widgetType.equals(PREF_WIDGET_HOURLY)) {
                loadData()
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteWidgetType(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
//        if (intent?.action==AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
//            loadData()
//        }
    }

}


internal fun loadData() {
    Log.d("my", "LoadData widget")

    val networkStatus = AndroidNetworkStatus(App.instance.applicationContext)

    val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(IDataSource::class.java)

    val weather = RetrofitWeather(api, networkStatus)
    val uiScheduler = Schedulers.io()
    weather.getJsonStr(App.settings.lat, App.settings.lon)
        .observeOn(uiScheduler)
        .subscribe(
            { jsonWeather ->
                App.settings.jsonTxt = jsonWeather.toString()
                Tools().parseJson(App.settings.jsonTxt)
                    .observeOn(uiScheduler)
                    .subscribe(
                        {
                            Log.d("my", "LoadData widget success")
                            weatherToWidget(it)
                        },
                        { t -> Log.d("my", t.message.toString() + " parseJson") })
//                    Log.d("my", jsonWeather.toString())
            },
            { t -> Log.d("my", t.message.toString() + " jsonStr") }
        )
}


internal fun weatherToWidget(weather: WeatherRequestRestModel) {
//           TODO  обновление виджета
    val context = App.instance.baseContext
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val widgetData = Tools().convertDataToWidget(weather)
    val widgetIds =
        appWidgetManager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java))
    for (id in widgetIds) {
        val widgetType = loadWidgetType(context, id)
        val widgetView = RemoteViews(context.packageName, R.layout.weather_widget)

        with(widgetView) {
            setTextViewText(R.id.tv_currentTemp, widgetData.current.temp)
            setTextViewText(R.id.tv_city, App.settings.city)
            setTextViewText(
                R.id.tv_update,
                weather.current.dt.toStrTime(PATTERN_HH_MM, App.settings.timeZone)
            )
            setImageViewResource(R.id.iv_currentImg, widgetData.current.weatherIcoId)
        }

        if (widgetType.equals(PREF_WIDGET_HOURLY)) {
            with(widgetView) {
                var num = 0
                setTextViewText(R.id.tv_firstTime, widgetData.hourly[num].dt)
                setTextViewText(R.id.tv_firstTemp, widgetData.hourly[num].temp)
                setImageViewResource(R.id.iv_firstImg, widgetData.hourly[num].weatherIcoId)
                num++
                setTextViewText(R.id.tv_secondTime, widgetData.hourly[num].dt)
                setTextViewText(R.id.tv_secondTemp, widgetData.hourly[num].temp)
                setImageViewResource(R.id.iv_secondImg, widgetData.hourly[num].weatherIcoId)
                num++
                setTextViewText(R.id.tv_thirdTime, widgetData.hourly[num].dt)
                setTextViewText(R.id.tv_thirdTemp, widgetData.hourly[num].temp)
                setImageViewResource(R.id.iv_thirdImg, widgetData.hourly[num].weatherIcoId)
            }
        }

        if (loadWidgetType(context, id).equals(PREF_WIDGET_DAILY)) {
            with(widgetView) {
                var num = 0
                setTextViewText(R.id.tv_firstTime, widgetData.daily[num].dt)
                setTextViewText(R.id.tv_firstTemp, widgetData.daily[num].temp)
                setImageViewResource(R.id.iv_firstImg, widgetData.daily[num].weatherIcoId)
                num++
                setTextViewText(R.id.tv_secondTime, widgetData.daily[num].dt)
                setTextViewText(R.id.tv_secondTemp, widgetData.daily[num].temp)
                setImageViewResource(R.id.iv_secondImg, widgetData.daily[num].weatherIcoId)
                num++
                setTextViewText(R.id.tv_thirdTime, widgetData.daily[num].dt)
                setTextViewText(R.id.tv_thirdTemp, widgetData.daily[num].temp)
                setImageViewResource(R.id.iv_thirdImg, widgetData.daily[num].weatherIcoId)
            }
        }
        appWidgetManager.updateAppWidget(id, widgetView)
    }
}