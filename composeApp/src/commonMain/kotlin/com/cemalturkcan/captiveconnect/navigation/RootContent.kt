package com.cemalturkcan.captiveconnect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.cemalturkcan.captiveconnect.data.AppPreferencesStore
import com.cemalturkcan.captiveconnect.data.ConnectivityChecker
import com.cemalturkcan.captiveconnect.data.CredentialsStore
import com.cemalturkcan.captiveconnect.data.NetworkBinder
import com.cemalturkcan.captiveconnect.data.NetworkMonitor
import com.cemalturkcan.captiveconnect.domain.portal.IbbWifiPortal
import com.cemalturkcan.captiveconnect.presentation.ConnectViewModel
import com.cemalturkcan.captiveconnect.ui.components.SettingsScreen
import com.cemalturkcan.captiveconnect.ui.screen.ConnectScreen

@Composable
fun RootContent(
    component: RootComponent,
    credentialsStore: CredentialsStore,
    preferencesStore: AppPreferencesStore,
    networkBinder: NetworkBinder,
    networkMonitor: NetworkMonitor,
    versionName: String,
) {
    val portals = remember { listOf(IbbWifiPortal()) }
    val connectivityChecker = remember { ConnectivityChecker() }
    val preferences by preferencesStore.preferencesState.collectAsState()

    val viewModel = viewModel {
        ConnectViewModel(
            credentialsStore = credentialsStore,
            portals = portals,
            connectivityChecker = connectivityChecker,
            preferencesStore = preferencesStore,
            networkBinder = networkBinder,
            networkMonitor = networkMonitor,
        )
    }

    Children(
        stack = component.childStack,
        animation = stackAnimation(slide()),
        modifier = Modifier,
    ) { child ->
        when (child.instance) {
            is RootComponent.Child.Connect -> {
                ConnectScreen(
                    viewModel = viewModel,
                    onOpenSettings = component::onOpenSettings,
                )
            }
            is RootComponent.Child.Settings -> {
                SettingsScreen(
                    selectedLanguage = preferences.language,
                    onLanguageSelected = viewModel::setLanguage,
                    onBack = component::onBack,
                    versionName = versionName,
                )
            }
        }
    }
}
