package com.mas.weather_kotlin.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.mas.weather_kotlin.R
import com.mas.weather_kotlin.databinding.FragmentSettingsBinding
import com.mas.weather_kotlin.mvp.model.entity.CitiesRequestModel
import com.mas.weather_kotlin.mvp.presenter.SettingsPresenter
import com.mas.weather_kotlin.mvp.view.SettingsView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


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
        val adapter = ArrayAdapter(requireContext(), R.layout.city_list_item, items)
        vb?.spinner?.adapter = adapter

        vb?.spinner?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View,
                position: Int,
                selectedId: Long
            ) {
                presenter.cityToSettings(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        vb?.bSave?.setOnClickListener {
            presenter.goToWeather()
        }

        vb?.tilCity?.setEndIconOnClickListener {
            presenter.loadData(vb?.tiCity?.text.toString().trim())
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun backPressed() = presenter.backClick()

    override fun setSpinnerPosition(position: Int) {
        vb?.spinner?.setSelection(position)
    }

    override fun setSpinnerAdapter(cities: MutableList<CitiesRequestModel>) {
        val items = mutableListOf<String>()
        cities.forEach {
            val city: String =
                if (it.local_names?.ru == "") it.local_names.featureName else it.local_names?.ru.toString()
            items.add("$city (${it.country})")
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.city_list_item, items)
        vb?.spinner?.adapter = adapter
    }

    override fun setCity(city: String) {
        vb?.tiCity?.setText(city)
    }


}