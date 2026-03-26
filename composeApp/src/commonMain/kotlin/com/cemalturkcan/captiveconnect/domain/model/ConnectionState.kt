package com.cemalturkcan.captiveconnect.domain.model

sealed class ConnectionState {

    data object Idle : ConnectionState()

    data object Checking : ConnectionState()

    data object AlreadyOnline : ConnectionState()

    data object LoggingIn : ConnectionState()

    data object Verifying : ConnectionState()

    data object Connected : ConnectionState()

    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.NETWORK,
        val debugLog: String = "",
    ) : ConnectionState()
}

enum class ErrorType {
    NETWORK,
    UNSUPPORTED_PORTAL,
    LOGIN_FAILED,
    VERIFICATION_FAILED,
}
