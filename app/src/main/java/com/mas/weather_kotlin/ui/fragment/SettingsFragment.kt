package com.mas.weather_kotlin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentSettingsBinding
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.presenter.SettingsPresenter
import com.mas.weather_kotlin.mvp.view.SettingsView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
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

        val items = App.instance.resources.getStringArray(R.array.cityRu)
        val adapter = ArrayAdapter(requireContext(), R.layout.city_list_item, items).also {
//            vb?.spinner?.adapter = it
            vb?.mySpinnerDropdown?.setAdapter(it)
//            vb?.mySpinnerDropdown?.setText(it.getItem(0).toString(), false)
        }


//        vb?.mySpinnerDropdown?.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                itemSelected: View,
//                position: Int,
//                selectedId: Long
//            ) {
//                presenter.cityToSettings(position)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        vb?.mySpinnerDropdown?.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                presenter.cityToSettings(position)
            }
        }

        vb?.bSave?.setOnClickListener {
            presenter.goToWeather()
        }

//        vb?.tilCity?.setEndIconOnClickListener {
//            presenter.loadCityList(vb?.tiCity?.text.toString().trim())
//        }
        var timer = Timer()
        vb?.mySpinnerDropdown?.doAfterTextChanged {
            vb?.swGps?.isChecked = false
            timer.cancel()
            timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        presenter.loadCityList(it.toString().trim())
                    }
                },
                1000
            )
        }

        vb?.swGps?.setOnCheckedChangeListener { _, isChecked ->
            presenter.gpsSettingsChange(isChecked)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun backPressed() = presenter.backClick()

    override fun setSpinnerPosition(position: Int) {
//       vb?.spinner?.setSelection(position)
    }

    override fun setSpinnerAdapter(cities: MutableList<CitiesRequestModel>) {
        val items = mutableListOf<String>()
        cities.forEach {
            val city: String =
                if (it.local_names?.ru == "") it.local_names.featureName else it.local_names?.ru.toString()
            items.add("$city (${it.country})")
        }
        ArrayAdapter(requireContext(), R.layout.city_list_item, items).also {
//            vb?.spinner?.adapter = it
            vb?.mySpinnerDropdown?.setAdapter(it)
//            vb?.mySpinnerDropdown?.setText(it.getItem(0).toString(),false)
            vb?.mySpinnerDropdown?.showDropDown()
        }
    }

    override fun setCity(city: String) {
        vb?.mySpinnerDropdown?.setText(city, false)
    }

    override fun setGPSSwitch(gpsKey: Boolean) {
        vb?.swGps?.isChecked = gpsKey
    }

    override fun setGPSText(location: String) {
        vb?.tvLatLon?.text = location
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}