package com.cemalturkcan.captiveconnect.domain.portal

import com.cemalturkcan.captiveconnect.domain.model.Credentials

sealed class DetectionResult {
    data object Online : DetectionResult()
    data class PortalFound(val entryUrl: String) : DetectionResult()
    data class Unknown(val reason: String) : DetectionResult()
}

sealed class LoginResult {
    data object Success : LoginResult()
    data class Failure(val reason: String) : LoginResult()
}

interface CaptivePortal {
    val name: String
    fun canHandle(entryUrl: String): Boolean
    suspend fun login(entryUrl: String, credentials: Credentials): LoginResult
}
