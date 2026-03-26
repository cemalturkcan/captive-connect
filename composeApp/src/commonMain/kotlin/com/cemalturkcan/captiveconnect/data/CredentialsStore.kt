package com.cemalturkcan.captiveconnect.data

import com.cemalturkcan.captiveconnect.domain.model.Credentials

private const val KEY_PHONE_NUMBER = "credentials_phone_number"
private const val KEY_PASSWORD = "credentials_password"
private const val KEY_COUNTRY_CODE = "credentials_country_code"
private const val DEFAULT_COUNTRY_CODE = "90"

interface CredentialsStore {
    fun save(credentials: Credentials)
    fun load(): Credentials?
    fun clear()
}

interface KeyValueStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

class DefaultCredentialsStore(
    private val storage: KeyValueStorage,
) : CredentialsStore {

    override fun save(credentials: Credentials) {
        storage.putString(KEY_PHONE_NUMBER, credentials.phoneNumber)
        storage.putString(KEY_PASSWORD, credentials.password)
        storage.putString(KEY_COUNTRY_CODE, credentials.countryCode)
    }

    override fun load(): Credentials? {
        val phone = storage.getString(KEY_PHONE_NUMBER) ?: return null
        val password = storage.getString(KEY_PASSWORD) ?: return null
        val countryCode = storage.getString(KEY_COUNTRY_CODE) ?: DEFAULT_COUNTRY_CODE
        return Credentials(
            phoneNumber = phone,
            password = password,
            countryCode = countryCode,
        )
    }

    override fun clear() {
        storage.remove(KEY_PHONE_NUMBER)
        storage.remove(KEY_PASSWORD)
        storage.remove(KEY_COUNTRY_CODE)
    }
}
