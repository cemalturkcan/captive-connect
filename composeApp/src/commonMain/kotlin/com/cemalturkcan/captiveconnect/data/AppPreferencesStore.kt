package com.cemalturkcan.captiveconnect.data

import com.cemalturkcan.captiveconnect.localization.AppLanguage
import com.cemalturkcan.captiveconnect.localization.normalizeToStorageLanguageTag
import com.cemalturkcan.captiveconnect.localization.readSystemLanguageTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_LANGUAGE_TAG = "language_tag"
private const val KEY_SELECTED_PORTAL = "selected_portal_id"

data class AppPreferences(
    val language: AppLanguage = AppLanguage.English,
    val selectedPortalId: String = "",
)

interface AppPreferencesStore {
    val preferencesState: StateFlow<AppPreferences>
    fun setLanguage(language: AppLanguage)
    fun setSelectedPortalId(portalId: String)
}

class DefaultAppPreferencesStore(
    private val storage: KeyValueStorage,
) : AppPreferencesStore {

    private val mutablePreferences = MutableStateFlow(loadInitialPreferences())

    override val preferencesState: StateFlow<AppPreferences> =
        mutablePreferences.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        val storageTag = normalizeToStorageLanguageTag(language.tag)
        if (storageTag != null) {
            storage.putString(KEY_LANGUAGE_TAG, storageTag)
        }
        mutablePreferences.value = mutablePreferences.value.copy(language = language)
    }

    override fun setSelectedPortalId(portalId: String) {
        storage.putString(KEY_SELECTED_PORTAL, portalId)
        mutablePreferences.value =
            mutablePreferences.value.copy(selectedPortalId = portalId)
    }

    private fun loadInitialPreferences(): AppPreferences {
        val storedTag = storage.getString(KEY_LANGUAGE_TAG)
        val language = AppLanguage.fromTag(storedTag)
            ?: AppLanguage.fromTag(readSystemLanguageTag())
            ?: AppLanguage.English
        val selectedPortal = storage.getString(KEY_SELECTED_PORTAL) ?: ""
        return AppPreferences(language = language, selectedPortalId = selectedPortal)
    }
}
