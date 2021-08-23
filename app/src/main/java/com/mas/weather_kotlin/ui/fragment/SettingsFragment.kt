package com.mas.weather_kotlin.ui.fragment

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentSettingsBinding
import com.mas.weather_kotlin.mvp.model.entity.SettingsModel
import com.mas.weather_kotlin.mvp.presenter.SettingsPresenter
import com.mas.weather_kotlin.mvp.view.SettingsView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import io.reactivex.rxjava3.core.Single
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*


class SettingsFragment : MvpAppCompatFragment(), SettingsView, BackButtonListener {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val presenter by moxyPresenter {
        SettingsPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    private var vb: FragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = FragmentSettingsBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

//        val items = App.instance.resources.getStringArray(R.array.cityRu)
//        ArrayAdapter(requireContext(), R.layout.city_list_item, items).also {
//            vb?.mySpinnerDropdown?.setAdapter(it)
//        }

        //TODO
//переделать сохранение
//        vb?.citySpinner?.onItemClickListener = object : AdapterView.OnItemClickListener {
//            override fun onItemClick(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                presenter.cityToSettings(position)
//            }
//        }

        vb?.swGps?.setOnCheckedChangeListener { _, isChecked ->
            presenter.gpsSettingsChange(isChecked)
        }

        vb?.swPercent?.setOnCheckedChangeListener { _, isChecked ->
            presenter.percentSettingsChange(isChecked)
        }

        vb?.swGraphWind?.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) setGraphSw(!isChecked, isChecked)
            presenter.settings.swWind = isChecked
        }
        vb?.swGraphTemp?.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) setGraphSw(isChecked, !isChecked)
            presenter.settings.swTemp = isChecked
        }

        vb?.swGraphRain?.setOnCheckedChangeListener() { _, isChecked ->
            presenter.settings.swRain = isChecked
        }

        return view
    }

    fun setGraphSw(swTemp: Boolean, swWind: Boolean) {
        vb?.swGraphTemp?.isChecked = swTemp
        vb?.swGraphWind?.isChecked = swWind
    }

    override fun setCitySpinnerTextChangedListener(gpsKey: Boolean) {
        if (gpsKey) {
            vb?.citySpinner?.doAfterTextChanged { null }
        } else {
            var timer = Timer()
            setCityReportLogo(false)
            vb?.citySpinner?.doAfterTextChanged {
                if (vb?.swGps?.isChecked == false && vb?.citySpinner?.hasFocus() == true) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                presenter.loadCityList(it.toString().trim())

                                //google geo
//                                geo(it.toString().trim(),5)
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(
//                                    {geoList ->
//                                        var googleGeo = ""
//                                        if (!geoList.isEmpty())
//                                        with(geoList.get(0)){
//                                            googleGeo += "$locality "
//                                            if (!countryCode.isNullOrEmpty())
//                                                googleGeo+="($countryCode) "
//                                            googleGeo+= "lat=$latitude, lon=$longitude"
//                                        }
//                                        vb?.geoText?.text = googleGeo
//                                        Log.d("my",geoList.toString())
//                                    },{
//                                        Log.d("myGeo",it.message.toString())
//                                    }
//                                )
                            }
                        },
                        1000
                    )
                }
            }
        }
    }

    private fun geo(city: String, maxResult: Int) =
        Single.fromCallable {
            Geocoder(context).getFromLocationName(city, maxResult)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun backPressed() = presenter.backClick()

    override fun setCity(city: String) {
        vb?.citySpinner?.setText(city)
    }

    override fun setCityReportLogo(report: Boolean) {
        if (report) {
            vb?.tiMainCity?.endIconDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
//            vb?.tiMainCity?.endIconDrawable = resources?.getDrawable(R.drawable.ic_baseline_check_24)
            vb?.tiMainCity?.endIconContentDescription = "OK"
        } else {
            vb?.tiMainCity?.endIconDrawable =
                resources?.getDrawable(R.drawable.ic_baseline_report_24)
            vb?.tiMainCity?.endIconContentDescription = "FAIL"
        }
    }

    override fun setSwitch(settings: SettingsModel) {
        vb?.swGps?.isChecked = settings.gpsKey
        vb?.swPercent?.isChecked = settings.percentRain
        vb?.swGraphRain?.isChecked = settings.swRain
        vb?.swGraphTemp?.isChecked = settings.swTemp
        vb?.swGraphWind?.isChecked = settings.swWind
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun setTiCityEnable(enableKey: Boolean) {
        vb?.tiMainCity?.isEnabled = enableKey
    }
}