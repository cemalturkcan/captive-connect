# Stack

## Core
- **Kotlin Multiplatform** — shared code for Android + iOS
- **Compose Multiplatform** — shared UI
- **Coroutines + Flow** — async, reactive state

## Networking
- **Ktor Client 3.x** — HTTP client (OkHttp engine on Android, Darwin on iOS)
- **Ktor HttpTimeout** — explicit request/connect timeouts on all clients
- **kotlinx.serialization** — JSON parsing
- **User-Agent** — `Mozilla/5.0` on all portal/connectivity requests

## UI
- **Material3** — design system foundation
- **Compose Canvas** — custom WiFi wave animation
- **Dark theme** — electric cyan (#00D4FF) accent on dark background
- **PortalPicker** — horizontal pill chips for portal selection (matches LanguagePicker style)

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
- **KeyValueStorage** — interface abstraction over platform storage
- **Credentials (encrypted):** EncryptedSharedPreferences (Android) / iOS Keychain (iOS)
- **Preferences (plain):** SharedPreferences (Android) / NSUserDefaults (iOS)
- **CredentialsStore** — portal-aware interface + DefaultCredentialsStore (keys: `cred_{portalId}_{field}`)
- **AppPreferencesStore** — interface + DefaultAppPreferencesStore (language + selectedPortalId)

## Network
- **NetworkBinder** — interface for platform WiFi binding
- **AndroidNetworkBinder** — `ConnectivityManager.bindProcessToNetwork()` forces traffic through WiFi
- **IosNetworkBinder** — no-op (iOS handles captive portals differently)

## Security
- **Credentials encrypted at rest** on both platforms
- **Trusted host validation** — portal URLs verified against allowlisted hosts before credential submission
- **Credentials persisted on input change** for immediate recall (encrypted)

## Build
- **Gradle** with version catalog (libs.versions.toml)
- **AGP 8.12.3**
- **Configuration cache** enabled
