package com.cemalturkcan.captiveconnect.localization

import android.content.res.Resources

actual fun readSystemLanguageTag(): String? {
    return Resources.getSystem().configuration.locales.get(0).toLanguageTag()
}
