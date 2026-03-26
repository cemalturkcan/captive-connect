package com.cemalturkcan.captiveconnect.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class AndroidNetworkBinder(context: Context) : NetworkBinder {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Suppress("DEPRECATION")
    override fun bindToWifi(): Boolean {
        val wifiNetwork = connectivityManager.allNetworks.firstOrNull { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } ?: return false
        return connectivityManager.bindProcessToNetwork(wifiNetwork)
    }

    override fun unbind() {
        connectivityManager.bindProcessToNetwork(null)
    }
}
