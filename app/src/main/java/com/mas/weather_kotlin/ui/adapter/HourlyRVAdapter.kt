package com.mas.weather_kotlin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mas.weather_kotlin.databinding.HourBinding
import com.mas.weather_kotlin.mvp.model.image.IImageLoader
import com.mas.weather_kotlin.mvp.presenter.list.IHourlyListPresenter
import com.mas.weather_kotlin.mvp.view.list.IHourItemView

class HourlyRVAdapter(
    val presenter: IHourlyListPresenter,
    val imageLoader: IImageLoader<ImageView>
) :
    RecyclerView.Adapter<HourlyRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            HourBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
//                .apply {
//            itemView.setOnClickListener { presenter.itemClickListener?.invoke(this) }
//        }

    override fun getItemCount() = presenter.getCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        presenter.bindView(holder.apply { pos = position })

    inner class ViewHolder(val vb: HourBinding) : RecyclerView.ViewHolder(vb.root),
        IHourItemView {
        override var pos = -1
        override fun setHour(text: String) = with(vb) {
            tvHour.text = text
        }

        override fun setRainfall(text: String) = with(vb) {
            tvRainfall.text = text
        }

        override fun setTemp(text: String) = with(vb) {
            tvTemp.text = text
        }

        override fun loadWeatherIco(url: String) = with(vb) {
            imageLoader.load(url, ivHour)
        }


    }


}