# Stack

## Core
- **Kotlin Multiplatform** — shared code for Android + iOS
- **Compose Multiplatform** — shared UI
- **Coroutines + Flow** — async, reactive state

## Networking
- **Ktor Client 3.x** — HTTP client (OkHttp engine on Android, Darwin on iOS)
- **Ktor HttpTimeout** — explicit request/connect timeouts on all clients
- **kotlinx.serialization** — JSON parsing

## UI
- **Material3** — design system foundation
- **Compose Canvas** — custom WiFi wave animation
- **Dark theme** — electric cyan (#00D4FF) accent on dark background

## State Management
- **lifecycle-viewmodel-compose** — ViewModel scoping
- **lifecycle-runtime-compose** — lifecycle-aware runtime
- **StateFlow + `collectAsState()`** — reactive state in composables

## Localization
- **AppLanguage** enum (English, Turkish) with `expect`/`actual` locale binding
- **Compose Resources** — string localization via generated accessors
- **ProvideAppLocale** — `expect`/`actual` composable for platform locale override
- **SystemLanguageTagReader** — `expect fun readSystemLanguageTag()` for system language detection

## Storage
- **KeyValueStorage** — interface abstraction over platform-secure storage
- **EncryptedSharedPreferences** (Android) via `androidx.security:security-crypto`
- **iOS Keychain** (iOS) via Security framework (`SecItemAdd`/`SecItemCopyMatching`/`SecItemDelete`)
- **CredentialsStore** — interface + DefaultCredentialsStore for user credentials
- **AppPreferencesStore** — interface + DefaultAppPreferencesStore for app settings (language, etc.)

## Security
- **Credentials encrypted at rest** on both platforms
- **Trusted host validation** — portal URLs verified against allowlisted hosts before credential submission
- **No cleartext credential flow** — credentials saved only after successful login verification

## Build
- **Gradle** with version catalog (libs.versions.toml)
- **AGP 8.12.3**
- **Configuration cache** enabled
