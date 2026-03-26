package com.cemalturkcan.captiveconnect

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cemalturkcan.captiveconnect.data.AppPreferencesStore
import com.cemalturkcan.captiveconnect.data.CredentialsStore
import com.cemalturkcan.captiveconnect.data.NetworkBinder
import com.cemalturkcan.captiveconnect.data.NetworkMonitor
import com.cemalturkcan.captiveconnect.localization.AppEnvironment
import com.cemalturkcan.captiveconnect.navigation.RootComponent
import com.cemalturkcan.captiveconnect.navigation.RootContent
import com.cemalturkcan.captiveconnect.ui.theme.AppTheme
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BACKGROUND

@Composable
fun App(
    rootComponent: RootComponent,
    credentialsStore: CredentialsStore,
    preferencesStore: AppPreferencesStore,
    networkBinder: NetworkBinder,
    networkMonitor: NetworkMonitor,
    versionName: String,
) {
    val preferences by preferencesStore.preferencesState.collectAsState()

    AppEnvironment(languageTag = preferences.language.tag) {
        AppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = COLOR_BACKGROUND,
            ) {
                RootContent(
                    component = rootComponent,
                    credentialsStore = credentialsStore,
                    preferencesStore = preferencesStore,
                    networkBinder = networkBinder,
                    networkMonitor = networkMonitor,
                    versionName = versionName,
                )
            }
        }
    }
}
