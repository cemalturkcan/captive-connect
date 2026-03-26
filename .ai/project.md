# Project

## CaptiveConnect
Single-screen captive portal auto-login app for Android + iOS.

## Module Layout
- `:composeApp` — KMP library (commonMain, androidMain, iosMain)
- `:androidApp` — Android application entry point

## Package
`com.cemalturkcan.captiveconnect`

## Source Layout (composeApp)
```
commonMain/kotlin/com/cemalturkcan/captiveconnect/
├── App.kt
├── ui/
│   ├── theme/      (AppTheme, AppColors, AppTypography, AppShapes)
│   ├── tokens/     (SpacingTokens, RadiusTokens, TypographyTokens, BorderTokens, SizeTokens)
│   ├── primitives/ (PrimaryButton, AppTextField, SettingsIconButton, BackIconButton)
│   ├── components/ (WifiAnimation, StatusIndicator, SettingsScreen, LanguagePicker)
│   └── screen/     (ConnectScreen)
├── domain/
│   ├── model/      (ConnectionState, ErrorType, Credentials, ConnectUiState)
│   └── portal/     (CaptivePortal interface, DetectionResult, LoginResult, IbbWifiPortal)
├── data/           (ConnectivityChecker, CredentialsStore, DefaultCredentialsStore, KeyValueStorage, AppPreferencesStore, DefaultAppPreferencesStore, AppPreferences)
├── presentation/   (ConnectViewModel)
└── localization/   (AppLanguage, AppEnvironment, LanguageTags, ProvideAppLocale, SystemLanguageTagReader)

androidMain/
├── AndroidKeyValueStorage (EncryptedSharedPreferences)
├── AppLocaleContext
├── ProvideAppLocale.android
└── SystemLanguageTagReader.android

iosMain/
├── IosKeyValueStorage (Keychain)
├── MainViewController
├── ProvideAppLocale.ios
└── SystemLanguageTagReader.ios
```

## Build Commands
- `./gradlew :androidApp:assembleDebug` — debug APK
- `./gradlew :androidApp:assembleRelease` — release APK
- `./gradlew :composeApp:allTests` — run tests
