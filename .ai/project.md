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
│   ├── components/ (WifiAnimation, StatusIndicator, SettingsScreen, LanguagePicker, PortalPicker)
│   └── screen/     (ConnectScreen, ConnectScreenComponents)
├── domain/
│   ├── model/      (ConnectionState, ErrorType, Credentials, ConnectUiState, PortalInfo)
│   └── portal/     (CaptivePortal interface, DetectionResult, LoginResult, IbbWifiPortal, IbbWifiPortalModels, IbbWifiPortalUtils)
├── data/           (ConnectivityChecker, CredentialsStore, DefaultCredentialsStore, KeyValueStorage, AppPreferencesStore, DefaultAppPreferencesStore, AppPreferences, NetworkBinder)
├── presentation/   (ConnectViewModel)
├── navigation/     (RootComponent, RootContent)
└── localization/   (AppLanguage, AppEnvironment, LanguageTags, ProvideAppLocale, SystemLanguageTagReader)

androidMain/
├── AndroidKeyValueStorage (EncryptedSharedPreferences — credentials)
├── AndroidPreferencesStorage (SharedPreferences — app preferences)
├── AndroidNetworkBinder (bindProcessToNetwork — WiFi traffic routing)
├── AppLocaleContext
├── ProvideAppLocale.android
└── SystemLanguageTagReader.android

iosMain/
├── IosKeyValueStorage (Keychain — credentials)
├── IosPreferencesStorage (NSUserDefaults — app preferences)
├── IosNetworkBinder (no-op)
├── MainViewController
├── ProvideAppLocale.ios
└── SystemLanguageTagReader.ios
```

## Build Commands
- `./gradlew :androidApp:assembleDebug` — debug APK
- `./gradlew :androidApp:assembleRelease` — release APK
- `./gradlew :composeApp:allTests` — run tests
