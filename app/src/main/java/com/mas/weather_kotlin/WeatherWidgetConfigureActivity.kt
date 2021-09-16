package com.mas.weather_kotlin

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mas.weather_kotlin.databinding.WeatherWidgetConfigureBinding
import com.mas.weather_kotlin.mvp.model.PREF_PREFIX_KEY
import com.mas.weather_kotlin.mvp.model.PREF_WIDGET_DAILY
import com.mas.weather_kotlin.mvp.model.PREF_WIDGET_HOURLY
import com.mas.weather_kotlin.ui.App


//@Inject
//lateinit var widgetData: WidgetData

/**
 * The configuration screen for the [WeatherWidget] AppWidget.
 */
class WeatherWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var onClickListener = View.OnClickListener {

        // When the button is clicked, store the string locally
        // It is the responsibility of the configuration activity to update the app widget

        loadData()
        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    private lateinit var binding: WeatherWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = WeatherWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addButton.setOnClickListener(onClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        binding.rbHourly.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                saveWidgetType(App.instance.baseContext, appWidgetId, PREF_WIDGET_HOURLY)
        }

        binding.rbDaily.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                saveWidgetType(App.instance.baseContext, appWidgetId, PREF_WIDGET_DAILY)
        }

        // widgetType.setText(loadTitlePref(this@WeatherWidgetConfigureActivity, appWidgetId))
    }

}

//private const val PREFS_NAME = "com.mas.weather_kotlin.WeatherWidget"
//private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveWidgetType(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(App.instance.packageName, Context.MODE_PRIVATE).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadWidgetType(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(App.instance.packageName, Context.MODE_PRIVATE)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    return titleValue ?: context.getString(R.string.appwidget_text)
}

internal fun deleteWidgetType(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(App.instance.packageName, Context.MODE_PRIVATE).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}