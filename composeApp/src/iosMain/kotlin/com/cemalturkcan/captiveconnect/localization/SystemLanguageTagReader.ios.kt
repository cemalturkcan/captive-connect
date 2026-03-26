package com.cemalturkcan.captiveconnect.localization

import platform.Foundation.NSUserDefaults

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"

actual fun readSystemLanguageTag(): String? {
    return NSUserDefaults.standardUserDefaults
        .stringArrayForKey(APPLE_LANGUAGES_KEY)
        ?.firstOrNull()
        ?.toString()
}
