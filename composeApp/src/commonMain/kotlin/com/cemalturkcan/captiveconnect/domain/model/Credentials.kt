package com.cemalturkcan.captiveconnect.domain.model

data class Credentials(
    val phoneNumber: String,
    val password: String,
    val countryCode: String,
)
