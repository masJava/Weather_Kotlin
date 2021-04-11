package com.mas.weather_kotlin.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mas.weather_kotlin.databinding.DayItemBinding
import com.mas.weather_kotlin.mvp.model.image.IImageLoader
import com.mas.weather_kotlin.mvp.presenter.list.IDailyListPresenter
import com.mas.weather_kotlin.mvp.view.list.IDailyItemView
import com.mas.weather_kotlin.ui.App

class DailyRVAdapter(
    val presenter: IDailyListPresenter,
    val imageLoader: IImageLoader<ImageView>
) :
    RecyclerView.Adapter<DailyRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DayItemBinding.inflate(
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

    inner class ViewHolder(val vb: DayItemBinding) : RecyclerView.ViewHolder(vb.root),
        IDailyItemView {
        override var pos = -1
        val weatherFont = Typeface.createFromAsset(App.instance.resources.assets, "fonts/weather.ttf")

        override fun setDay(text: String) = with(vb) {
            tvDay.typeface = weatherFont
            tvDay.text = text
        }

        override fun setTempMax(text: String) = with(vb) {
            tvTempMax.typeface = weatherFont
            tvTempMax.text = text
        }

        override fun setTempMin(text: String) = with(vb) {
            tvTempMin.typeface = weatherFont
            tvTempMin.text = text
        }

        override fun setDailyPressure(text: String) = with(vb) {
            tvDailyPressure.typeface = weatherFont
            tvDailyPressure.text = text
        }

        override fun setDailyHumidity(text: String) = with(vb) {
            tvDailyHumidity.typeface = weatherFont
            tvDailyHumidity.text = text
        }

        override fun setDailyRain(text: String) = with(vb) {
            tvDailyRain.typeface = weatherFont
            tvDailyRain.text = text
        }


        override fun loadWeatherIco(url: String) = with(vb) {
            imageLoader.load(url, ivDayImage)
        }


    }


}