package com.cemalturkcan.captiveconnect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemalturkcan.captiveconnect.data.AppPreferencesStore
import com.cemalturkcan.captiveconnect.data.ConnectivityChecker
import com.cemalturkcan.captiveconnect.data.CredentialsStore
import com.cemalturkcan.captiveconnect.data.NetworkBinder
import com.cemalturkcan.captiveconnect.data.NetworkMonitor
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.domain.model.ConnectUiState
import com.cemalturkcan.captiveconnect.domain.model.Credentials
import com.cemalturkcan.captiveconnect.domain.model.ErrorType
import com.cemalturkcan.captiveconnect.domain.model.PortalInfo
import com.cemalturkcan.captiveconnect.domain.portal.CaptivePortal
import com.cemalturkcan.captiveconnect.domain.portal.DetectionResult
import com.cemalturkcan.captiveconnect.domain.portal.LoginResult
import com.cemalturkcan.captiveconnect.localization.AppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VERIFICATION_DELAY = 3000L
private const val WIFI_SETTLE_DELAY = 1500L

class ConnectViewModel(
    private val credentialsStore: CredentialsStore,
    private val portals: List<CaptivePortal>,
    private val connectivityChecker: ConnectivityChecker,
    private val preferencesStore: AppPreferencesStore,
    private val networkBinder: NetworkBinder,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val mutableState = MutableStateFlow(ConnectUiState())
    val state: StateFlow<ConnectUiState> = mutableState.asStateFlow()

    val preferencesState = preferencesStore.preferencesState

    init {
        initializePortalState()
        observeNetworkChanges()
        networkMonitor.startObserving()
    }

    override fun onCleared() {
        networkMonitor.stopObserving()
    }

    fun selectPortal(portalId: String) {
        preferencesStore.setSelectedPortalId(portalId)
        val saved = credentialsStore.load(portalId)
        mutableState.update {
            it.copy(
                selectedPortalId = portalId,
                phoneNumber = saved?.phoneNumber ?: "",
                password = saved?.password ?: "",
                countryCode = saved?.countryCode ?: it.countryCode,
                connectionState = ConnectionState.Idle,
            )
        }
    }

    fun updatePhoneNumber(value: String) {
        mutableState.update { it.copy(phoneNumber = value) }
        persistCredentials()
    }

    fun updatePassword(value: String) {
        mutableState.update { it.copy(password = value) }
        persistCredentials()
    }

    fun updateCountryCode(value: String) {
        mutableState.update { it.copy(countryCode = value) }
        persistCredentials()
    }

    fun setLanguage(language: AppLanguage) {
        preferencesStore.setLanguage(language)
    }

    fun connect() {
        val current = mutableState.value
        if (isInProgress(current.connectionState)) return
        if (current.phoneNumber.isBlank() || current.password.isBlank()) return
        if (current.countryCode.isBlank()) return
        val selectedPortal = portals.firstOrNull { it.id == current.selectedPortalId }
            ?: return
        val credentials = Credentials(
            phoneNumber = current.phoneNumber,
            password = current.password,
            countryCode = current.countryCode,
        )
        viewModelScope.launch {
            val bound = networkBinder.bindToWifi()
            val log = buildConnectionLog(current, selectedPortal.name)
            log.appendLine("WiFi bound: $bound")
            try {
                mutableState.update { it.copy(connectionState = ConnectionState.Checking) }
                handleDetection(selectedPortal, credentials, log)
            } finally {
                networkBinder.unbind()
            }
        }
    }

    fun resetState() {
        mutableState.update { it.copy(connectionState = ConnectionState.Idle) }
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            networkMonitor.wifiConnected
                .drop(1)
                .distinctUntilChanged()
                .collect { connected ->
                    if (connected) {
                        delay(WIFI_SETTLE_DELAY)
                        autoConnectIfReady()
                    }
                }
        }
    }

    private suspend fun handleDetection(
        portal: CaptivePortal, credentials: Credentials, log: StringBuilder,
    ) {
        when (val detection = connectivityChecker.check()) {
            is DetectionResult.Online -> {
                mutableState.update {
                    it.copy(connectionState = ConnectionState.AlreadyOnline)
                }
            }
            is DetectionResult.PortalFound -> {
                val matched = portals.find { it.canHandle(detection.entryUrl) }
                val effective = matched ?: portal
                log.appendLine("Detected: ${detection.entryUrl} → ${effective.name}")
                if (matched != null && matched.id != portal.id) {
                    selectPortalSilently(matched.id)
                    val creds = credentialsStore.load(matched.id)
                    if (creds == null) {
                        mutableState.update {
                            it.copy(connectionState = ConnectionState.Idle)
                        }
                        return
                    }
                    loginWithPortal(matched, detection.entryUrl, creds, log)
                } else {
                    loginWithPortal(effective, detection.entryUrl, credentials, log)
                }
            }
            is DetectionResult.Unknown -> {
                log.appendLine("Detection: ${detection.reason}")
                log.appendLine("Fallback: ${portal.defaultEntryUrl}")
                loginWithPortal(portal, portal.defaultEntryUrl, credentials, log)
            }
        }
    }

    private suspend fun loginWithPortal(
        portal: CaptivePortal, entryUrl: String,
        credentials: Credentials, log: StringBuilder,
    ) {
        mutableState.update { it.copy(connectionState = ConnectionState.LoggingIn) }
        when (val result = portal.login(entryUrl, credentials)) {
            is LoginResult.Success -> {
                log.appendLine(result.debugLog)
                verifyConnection(log)
            }
            is LoginResult.Failure -> {
                log.appendLine(result.debugLog)
                mutableState.update {
                    it.copy(
                        connectionState = ConnectionState.Error(
                            message = result.reason,
                            type = ErrorType.LOGIN_FAILED,
                            debugLog = log.toString(),
                        ),
                    )
                }
            }
        }
    }

    private suspend fun verifyConnection(log: StringBuilder) {
        mutableState.update { it.copy(connectionState = ConnectionState.Verifying) }
        delay(VERIFICATION_DELAY)
        val verification = connectivityChecker.check()
        log.appendLine("--- Verification ---")
        if (verification is DetectionResult.Online) {
            mutableState.update { it.copy(connectionState = ConnectionState.Connected) }
        } else {
            val reason = (verification as? DetectionResult.Unknown)?.reason
            log.appendLine("Still offline: $reason")
            mutableState.update {
                it.copy(
                    connectionState = ConnectionState.Error(
                        message = "login succeeded but still offline",
                        type = ErrorType.VERIFICATION_FAILED,
                        debugLog = log.toString(),
                    ),
                )
            }
        }
    }

    private fun initializePortalState() {
        val portalInfos = portals.map { PortalInfo(id = it.id, name = it.name) }
        val initialId = resolveInitialPortalId()
        mutableState.update {
            it.copy(availablePortals = portalInfos, selectedPortalId = initialId)
        }
        loadSavedCredentials(initialId)
        autoConnectIfReady()
    }

    private fun resolveInitialPortalId(): String {
        val savedId = preferencesStore.preferencesState.value.selectedPortalId
        if (savedId.isNotEmpty() && portals.any { it.id == savedId }) return savedId
        return portals.firstOrNull()?.id ?: ""
    }

    private fun autoConnectIfReady() {
        val current = mutableState.value
        if (current.selectedPortalId.isEmpty()) return
        if (current.phoneNumber.isBlank() || current.password.isBlank()) return
        connect()
    }

    private fun selectPortalSilently(portalId: String) {
        preferencesStore.setSelectedPortalId(portalId)
        mutableState.update { it.copy(selectedPortalId = portalId) }
        loadSavedCredentials(portalId)
    }

    private fun isInProgress(state: ConnectionState): Boolean =
        state is ConnectionState.Checking
            || state is ConnectionState.LoggingIn
            || state is ConnectionState.Verifying

    private fun persistCredentials() {
        val current = mutableState.value
        if (current.selectedPortalId.isEmpty()) return
        credentialsStore.save(
            current.selectedPortalId,
            Credentials(
                phoneNumber = current.phoneNumber,
                password = current.password,
                countryCode = current.countryCode,
            ),
        )
    }

    private fun loadSavedCredentials(portalId: String) {
        if (portalId.isEmpty()) return
        val saved = credentialsStore.load(portalId) ?: return
        mutableState.update {
            it.copy(
                phoneNumber = saved.phoneNumber,
                password = saved.password,
                countryCode = saved.countryCode,
            )
        }
    }

    private fun buildConnectionLog(
        state: ConnectUiState, portalName: String,
    ): StringBuilder =
        StringBuilder().apply {
            appendLine("=== Connection Attempt ===")
            appendLine("Portal: $portalName")
            appendLine("Phone: ${maskPhone(state.phoneNumber)} | CC: ${state.countryCode}")
        }

    private fun maskPhone(phone: String): String =
        if (phone.length > 4) "${phone.take(3)}***${phone.takeLast(2)}" else "***"
}
