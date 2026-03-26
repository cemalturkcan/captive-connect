package com.cemalturkcan.captiveconnect.data

import com.cemalturkcan.captiveconnect.domain.model.Credentials

private const val KEY_PHONE_NUMBER = "phone_number"
private const val KEY_PASSWORD = "password"
private const val KEY_COUNTRY_CODE = "country_code"
private const val DEFAULT_COUNTRY_CODE = "90"
private const val LEGACY_PREFIX = "credentials"
private const val CRED_PREFIX = "cred"

interface CredentialsStore {
    fun save(portalId: String, credentials: Credentials)
    fun load(portalId: String): Credentials?
    fun clear(portalId: String)
}

interface KeyValueStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

class DefaultCredentialsStore(
    private val storage: KeyValueStorage,
) : CredentialsStore {

    init {
        migrateLegacyKeys()
    }

    override fun save(portalId: String, credentials: Credentials) {
        val prefix = "${CRED_PREFIX}_${portalId}"
        storage.putString("${prefix}_$KEY_PHONE_NUMBER", credentials.phoneNumber)
        storage.putString("${prefix}_$KEY_PASSWORD", credentials.password)
        storage.putString("${prefix}_$KEY_COUNTRY_CODE", credentials.countryCode)
    }

    override fun load(portalId: String): Credentials? {
        val prefix = "${CRED_PREFIX}_${portalId}"
        val phone = storage.getString("${prefix}_$KEY_PHONE_NUMBER") ?: return null
        val password = storage.getString("${prefix}_$KEY_PASSWORD") ?: return null
        val countryCode = storage.getString("${prefix}_$KEY_COUNTRY_CODE")
            ?: DEFAULT_COUNTRY_CODE
        return Credentials(
            phoneNumber = phone,
            password = password,
            countryCode = countryCode,
        )
    }

    override fun clear(portalId: String) {
        val prefix = "${CRED_PREFIX}_${portalId}"
        storage.remove("${prefix}_$KEY_PHONE_NUMBER")
        storage.remove("${prefix}_$KEY_PASSWORD")
        storage.remove("${prefix}_$KEY_COUNTRY_CODE")
    }

    private fun migrateLegacyKeys() {
        val legacyPhone = storage.getString("${LEGACY_PREFIX}_$KEY_PHONE_NUMBER")
            ?: return
        val legacyPassword = storage.getString("${LEGACY_PREFIX}_$KEY_PASSWORD")
            ?: return
        val legacyCode = storage.getString("${LEGACY_PREFIX}_$KEY_COUNTRY_CODE")
            ?: DEFAULT_COUNTRY_CODE
        val ibbPrefix = "${CRED_PREFIX}_ibb_wifi"
        storage.putString("${ibbPrefix}_$KEY_PHONE_NUMBER", legacyPhone)
        storage.putString("${ibbPrefix}_$KEY_PASSWORD", legacyPassword)
        storage.putString("${ibbPrefix}_$KEY_COUNTRY_CODE", legacyCode)
        storage.remove("${LEGACY_PREFIX}_$KEY_PHONE_NUMBER")
        storage.remove("${LEGACY_PREFIX}_$KEY_PASSWORD")
        storage.remove("${LEGACY_PREFIX}_$KEY_COUNTRY_CODE")
    }
}
