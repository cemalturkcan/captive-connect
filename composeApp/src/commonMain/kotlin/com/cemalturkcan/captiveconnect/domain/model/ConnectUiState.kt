package com.cemalturkcan.captiveconnect.domain.model

private const val DEFAULT_COUNTRY_CODE = "90"

data class PortalInfo(
    val id: String,
    val name: String,
)

data class ConnectUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val countryCode: String = DEFAULT_COUNTRY_CODE,
    val connectionState: ConnectionState = ConnectionState.Idle,
    val selectedPortalId: String = "",
    val availablePortals: List<PortalInfo> = emptyList(),
)
