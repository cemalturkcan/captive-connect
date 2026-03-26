package com.cemalturkcan.captiveconnect.data

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val wifiConnected: StateFlow<Boolean>
    fun startObserving()
    fun stopObserving()
}
