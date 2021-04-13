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
import com.mas.weather_kotlin.mvp.presenter.SettingsPresenter
import com.mas.weather_kotlin.mvp.view.SettingsView
import com.mas.weather_kotlin.ui.App
import com.mas.weather_kotlin.ui.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class SettingsFragment : MvpAppCompatFragment(), SettingsView, BackButtonListener {

    companion object {
        //        private const val REPO_ARG = "repo"
        fun newInstance() = SettingsFragment()
//            .apply {
//            arguments = Bundle().apply {
//                putParcelable(REPO_ARG, repo)
//            }
//        }
    }

    private val presenter by moxyPresenter {
//        val repo = arguments?.getParcelable<GithubUserRepository>(REPO_ARG) as GithubUserRepository
//        RepoInfoPresenter(repo).apply {
        SettingsPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    private var vb: FragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

                val lat = App.instance.resources.getStringArray(R.array.cityLat)
                val lon = App.instance.resources.getStringArray(R.array.cityLon)
                vb?.cityLatLon?.text =
                    "${lat[position]} - ${lon[position]}"                            //del
                presenter.settings.city = items[position]
                presenter.settings.lat = lat[position]
                presenter.settings.lon = lon[position]
                presenter.settings.position = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        vb?.bSave?.setOnClickListener {
            presenter.goToWeather()
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

//    override fun setName(name: String) {
//        vb?.tvRepoName?.text = name
//    }
//
//    override fun setDescription(description: String) {
//        vb?.tvDescription?.text = description
//    }
//
//    override fun setUrl(url: String) {
//        vb?.tvHtmlUrl?.text = url
//    }
//
//    override fun setForkCount(forkCount: String) {
//        vb?.tvForkCount?.text = forkCount
//    }

}