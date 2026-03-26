package com.cemalturkcan.captiveconnect.data

class IosNetworkBinder : NetworkBinder {
    override fun bindToWifi(): Boolean = true
    override fun unbind() {}
}
