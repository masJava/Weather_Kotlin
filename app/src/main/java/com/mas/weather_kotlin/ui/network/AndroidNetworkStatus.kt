package com.mas.weather_kotlin.ui.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import com.mas.weather_kotlin.mvp.model.network.INetworkStatus
import io.reactivex.rxjava3.subjects.BehaviorSubject

class AndroidNetworkStatus(context: Context) : INetworkStatus {
    private val statusSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    init {
        statusSubject.onNext(false)
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    statusSubject.onNext(true)
                    Log.d("my", "Internet Available")
                }

                override fun onUnavailable() {
                    statusSubject.onNext(false)
                    Log.d("my", "Internet Unavailable")
                }

                override fun onLost(network: Network) {
                    statusSubject.onNext(false)
                    Log.d("my", "Internet Lost")
                }
            })
    }

    override fun isOnline() = statusSubject

    override fun isOnlineSingle() = statusSubject.first(false)
}