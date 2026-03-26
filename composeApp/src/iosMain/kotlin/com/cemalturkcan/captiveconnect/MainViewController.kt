package com.cemalturkcan.captiveconnect

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.cemalturkcan.captiveconnect.data.DefaultAppPreferencesStore
import com.cemalturkcan.captiveconnect.data.DefaultCredentialsStore
import com.cemalturkcan.captiveconnect.data.IosKeyValueStorage
import com.cemalturkcan.captiveconnect.data.IosPreferencesStorage
import com.cemalturkcan.captiveconnect.navigation.RootComponent
import platform.Foundation.NSBundle

fun mainViewController() = ComposeUIViewController {
    val secureStorage = IosKeyValueStorage()
    val prefsStorage = IosPreferencesStorage()
    val credentialsStore = DefaultCredentialsStore(secureStorage)
    val preferencesStore = DefaultAppPreferencesStore(prefsStorage)
    val versionName = NSBundle.mainBundle
        .objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""

    val rootComponent = RootComponent(
        componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
    )

    App(
        rootComponent = rootComponent,
        credentialsStore = credentialsStore,
        preferencesStore = preferencesStore,
        versionName = versionName,
    )
}
