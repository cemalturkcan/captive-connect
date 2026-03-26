package com.cemalturkcan.captiveconnect.data

import android.content.Context

private const val PREFERENCES_NAME = "captive_connect_prefs"

class AndroidPreferencesStorage(context: Context) : KeyValueStorage {

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun getString(key: String): String? =
        preferences.getString(key, null)

    override fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }
}
