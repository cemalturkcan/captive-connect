package com.cemalturkcan.captiveconnect.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.nw_interface_type_wifi
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_uses_interface_type
import platform.darwin.dispatch_get_main_queue

class IosNetworkMonitor : NetworkMonitor {

    private val _wifiConnected = MutableStateFlow(false)
    override val wifiConnected: StateFlow<Boolean> = _wifiConnected.asStateFlow()

    private var monitor: nw_path_monitor_t = null

    override fun startObserving() {
        val m = nw_path_monitor_create().also { monitor = it }
        nw_path_monitor_set_queue(m, dispatch_get_main_queue())
        nw_path_monitor_set_update_handler(m) { path ->
            if (path != null) {
                val satisfied = nw_path_get_status(path) == nw_path_status_satisfied
                val wifi = nw_path_uses_interface_type(path, nw_interface_type_wifi)
                _wifiConnected.value = satisfied && wifi
            }
        }
        nw_path_monitor_start(m)
    }

    override fun stopObserving() {
        val m = monitor
        if (m != null) nw_path_monitor_cancel(m)
        monitor = null
    }
}
