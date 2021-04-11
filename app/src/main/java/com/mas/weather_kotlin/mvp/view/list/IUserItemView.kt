package com.mas.weather_kotlin.mvp.view.list

interface IUserItemView : IItemView {
    fun setLogin(text: String)
    fun loadAvatar(url: String)
}