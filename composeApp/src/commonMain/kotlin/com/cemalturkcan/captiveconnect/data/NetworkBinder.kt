package com.cemalturkcan.captiveconnect.data

interface NetworkBinder {
    fun bindToWifi(): Boolean
    fun unbind()
}
