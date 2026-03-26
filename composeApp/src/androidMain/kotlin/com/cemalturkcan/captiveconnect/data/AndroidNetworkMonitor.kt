package com.cemalturkcan.captiveconnect.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidNetworkMonitor(context: Context) : NetworkMonitor {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _wifiConnected = MutableStateFlow(isWifiActive())
    override val wifiConnected: StateFlow<Boolean> = _wifiConnected.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = refreshState()
        override fun onLost(network: Network) = refreshState()

        override fun onCapabilitiesChanged(
            network: Network, capabilities: NetworkCapabilities,
        ) = refreshState()
    }

    override fun startObserving() {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    override fun stopObserving() {
        runCatching { connectivityManager.unregisterNetworkCallback(callback) }
    }

    private fun refreshState() {
        _wifiConnected.value = isWifiActive()
    }

    private fun isWifiActive(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
