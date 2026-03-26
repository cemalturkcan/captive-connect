package com.cemalturkcan.captiveconnect.domain.portal

import kotlinx.serialization.Serializable

@Serializable
internal data class LandingCheckBody(
    val PhoneNumber: String,
    val CountryCode: String,
    val FlagCode: String,
)

@Serializable
internal data class LandingCheckResponse(
    val url: String = "",
    val isSuccess: Boolean = false,
)

@Serializable
internal data class LoginBody(
    val Password: String,
    val KVKK: Boolean,
)

@Serializable
internal data class LoginResponse(
    val url: String = "",
    val isSuccess: Boolean = false,
)

internal data class LoginState(
    val csrf: String,
    val userId: String,
    val cookies: String,
)

internal data class PageResult(val body: String, val cookies: String)
internal data class LandingResult(val loginUrl: String?, val cookies: String)
