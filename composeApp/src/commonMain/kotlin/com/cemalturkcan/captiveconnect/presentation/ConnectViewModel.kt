package com.cemalturkcan.captiveconnect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemalturkcan.captiveconnect.data.AppPreferencesStore
import com.cemalturkcan.captiveconnect.data.ConnectivityChecker
import com.cemalturkcan.captiveconnect.data.CredentialsStore
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.domain.model.ConnectUiState
import com.cemalturkcan.captiveconnect.domain.model.Credentials
import com.cemalturkcan.captiveconnect.domain.model.ErrorType
import com.cemalturkcan.captiveconnect.domain.portal.CaptivePortal
import com.cemalturkcan.captiveconnect.domain.portal.DetectionResult
import com.cemalturkcan.captiveconnect.domain.portal.LoginResult
import com.cemalturkcan.captiveconnect.localization.AppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VERIFICATION_DELAY = 3000L

class ConnectViewModel(
    private val credentialsStore: CredentialsStore,
    private val portals: List<CaptivePortal>,
    private val connectivityChecker: ConnectivityChecker,
    private val preferencesStore: AppPreferencesStore,
) : ViewModel() {

    private val mutableState = MutableStateFlow(ConnectUiState())
    val state: StateFlow<ConnectUiState> = mutableState.asStateFlow()

    val preferencesState = preferencesStore.preferencesState

    init {
        loadSavedCredentials()
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

        val credentials = Credentials(
            phoneNumber = current.phoneNumber,
            password = current.password,
            countryCode = current.countryCode,
        )

        viewModelScope.launch {
            mutableState.update { it.copy(connectionState = ConnectionState.Checking) }

            when (val detection = connectivityChecker.check()) {
                is DetectionResult.Online -> {
                    mutableState.update {
                        it.copy(connectionState = ConnectionState.AlreadyOnline)
                    }
                }
                is DetectionResult.PortalFound -> {
                    handlePortalFound(detection.entryUrl, credentials)
                }
                is DetectionResult.Unknown -> {
                    mutableState.update {
                        it.copy(
                            connectionState = ConnectionState.Error(
                                message = detection.reason,
                                type = ErrorType.NETWORK,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        mutableState.update { it.copy(connectionState = ConnectionState.Idle) }
    }

    private suspend fun handlePortalFound(
        entryUrl: String,
        credentials: Credentials,
    ) {
        val portal = portals.firstOrNull { it.canHandle(entryUrl) }
        if (portal == null) {
            mutableState.update {
                it.copy(
                    connectionState = ConnectionState.Error(
                        message = "unsupported portal",
                        type = ErrorType.UNSUPPORTED_PORTAL,
                    ),
                )
            }
            return
        }

        mutableState.update {
            it.copy(connectionState = ConnectionState.LoggingIn)
        }

        when (val result = portal.login(entryUrl, credentials)) {
            is LoginResult.Success -> verifyConnection()
            is LoginResult.Failure -> {
                mutableState.update {
                    it.copy(
                        connectionState = ConnectionState.Error(
                            message = result.reason,
                            type = ErrorType.LOGIN_FAILED,
                        ),
                    )
                }
            }
        }
    }

    private suspend fun verifyConnection() {
        mutableState.update {
            it.copy(connectionState = ConnectionState.Verifying)
        }
        delay(VERIFICATION_DELAY)
        val verification = connectivityChecker.check()
        if (verification is DetectionResult.Online) {
            mutableState.update {
                it.copy(connectionState = ConnectionState.Connected)
            }
        } else {
            mutableState.update {
                it.copy(
                    connectionState = ConnectionState.Error(
                        message = "login succeeded but still offline",
                        type = ErrorType.VERIFICATION_FAILED,
                    ),
                )
            }
        }
    }

    private fun isInProgress(state: ConnectionState): Boolean =
        state is ConnectionState.Checking
            || state is ConnectionState.LoggingIn
            || state is ConnectionState.Verifying

    private fun persistCredentials() {
        val current = mutableState.value
        credentialsStore.save(
            Credentials(
                phoneNumber = current.phoneNumber,
                password = current.password,
                countryCode = current.countryCode,
            ),
        )
    }

    private fun loadSavedCredentials() {
        val saved = credentialsStore.load() ?: return
        mutableState.update {
            it.copy(
                phoneNumber = saved.phoneNumber,
                password = saved.password,
                countryCode = saved.countryCode,
            )
        }
    }
}
