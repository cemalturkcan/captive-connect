package com.cemalturkcan.captiveconnect

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.cemalturkcan.captiveconnect.data.AndroidKeyValueStorage
import com.cemalturkcan.captiveconnect.data.AndroidNetworkBinder
import com.cemalturkcan.captiveconnect.data.AndroidNetworkMonitor
import com.cemalturkcan.captiveconnect.data.AndroidPreferencesStorage
import com.cemalturkcan.captiveconnect.data.DefaultAppPreferencesStore
import com.cemalturkcan.captiveconnect.data.DefaultCredentialsStore
import com.cemalturkcan.captiveconnect.localization.createAppLocaleContext
import com.cemalturkcan.captiveconnect.navigation.RootComponent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val secureStorage = AndroidKeyValueStorage(applicationContext)
        val prefsStorage = AndroidPreferencesStorage(applicationContext)
        val credentialsStore = DefaultCredentialsStore(secureStorage)
        val preferencesStore = DefaultAppPreferencesStore(prefsStorage)
        val networkBinder = AndroidNetworkBinder(applicationContext)
        val networkMonitor = AndroidNetworkMonitor(applicationContext)

        val versionName = packageManager
            .getPackageInfo(packageName, 0).versionName.orEmpty()

        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
        )

        setContent {
            App(
                rootComponent = rootComponent,
                credentialsStore = credentialsStore,
                preferencesStore = preferencesStore,
                networkBinder = networkBinder,
                networkMonitor = networkMonitor,
                versionName = versionName,
            )
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(createAppLocaleContext(newBase))
    }
}
