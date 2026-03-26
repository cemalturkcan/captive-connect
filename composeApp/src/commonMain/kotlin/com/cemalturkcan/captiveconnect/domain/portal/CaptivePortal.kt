package com.cemalturkcan.captiveconnect.domain.portal

import com.cemalturkcan.captiveconnect.domain.model.Credentials

sealed class DetectionResult {
    data object Online : DetectionResult()
    data class PortalFound(val entryUrl: String) : DetectionResult()
    data class Unknown(val reason: String) : DetectionResult()
}

sealed class LoginResult {
    data class Success(val debugLog: String = "") : LoginResult()
    data class Failure(val reason: String, val debugLog: String = "") : LoginResult()
}

interface CaptivePortal {
    val id: String
    val name: String
    val defaultEntryUrl: String
    fun canHandle(entryUrl: String): Boolean
    suspend fun login(entryUrl: String, credentials: Credentials): LoginResult
}
