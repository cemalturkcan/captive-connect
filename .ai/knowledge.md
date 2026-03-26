# Knowledge

## Design Tokens

All UI values live in `ui/tokens/` as `UPPER_SNAKE_CASE` top-level `val` constants.
Token files: SpacingTokens, RadiusTokens, TypographyTokens, BorderTokens, SizeTokens.

## Store Pattern

Interface + DefaultImpl pattern with `KeyValueStorage` abstraction:

```kotlin
interface SomeStore {
    val state: StateFlow<SomeState>
    fun update(value: String)
}

class DefaultSomeStore(private val storage: KeyValueStorage) : SomeStore {
    private val _state = MutableStateFlow(
        SomeState(field = storage.getString(KEY, ""))
    )
    override val state: StateFlow<SomeState> = _state.asStateFlow()

    override fun update(value: String) {
        _state.update { it.copy(field = value) }
        storage.putString(KEY, value)
    }
}
```

- Private `const val` keys
- `.copy()` for mutations
- Synchronous init from storage (no CoroutineScope in init)
- `MutableStateFlow` for reactive state, direct `storage` calls for persistence

## Dual Storage Architecture

Two `KeyValueStorage` implementations per platform:
- **Secure (credentials):** `AndroidKeyValueStorage` (EncryptedSharedPreferences) / `IosKeyValueStorage` (Keychain)
- **Plain (preferences):** `AndroidPreferencesStorage` (SharedPreferences) / `IosPreferencesStorage` (NSUserDefaults)

Wired in entry points: `MainActivity`/`MainViewController` create both, pass secure to `CredentialsStore`, plain to `AppPreferencesStore`.

## Portal-Aware Credentials

`CredentialsStore` saves/loads per portal using composite keys:
```
cred_{portalId}_phone_number
cred_{portalId}_password
cred_{portalId}_country_code
```

Legacy migration: `DefaultCredentialsStore.init` calls `migrateLegacyKeys()` to move old `credentials_*` keys to `cred_ibb_wifi_*` prefix.

## expect/actual

Prefer `expect fun` over `expect object`. Create platform implementations in
`androidMain` and `iosMain` matching exact signatures.
Examples: `ProvideAppLocale`, `readSystemLanguageTag()`, `KeyValueStorage`.

## Portal Architecture

`CaptivePortal` interface with `id`, `name`, `defaultEntryUrl`, `canHandle(entryUrl)`, and `login(entryUrl, credentials)`.
Each WiFi portal (IBB, etc.) implements this interface.

Detection is handled separately by `ConnectivityChecker.check()`, which returns `DetectionResult`:

```kotlin
sealed class DetectionResult {
    data object Online : DetectionResult()
    data class PortalFound(val entryUrl: String) : DetectionResult()
    data class Unknown(val reason: String) : DetectionResult()
}

sealed class LoginResult {
    data class Success(val debugLog: String = "") : LoginResult()
    data class Failure(val reason: String, val debugLog: String = "") : LoginResult()
}
```

Portal detection uses connectivity check redirect URL matching with strict host validation.
Trusted hosts defined in `TRUSTED_PORTAL_HOSTS` / `TRUSTED_HOSTS` sets.
Host matching: `host == trusted || host.endsWith(".$trusted")` — prevents lookalike domains.
`canHandle()` validates URL host against allowlist before accepting portal.

## Portal Selection

Users select a portal from horizontal chips (PortalPicker). Per-portal credentials auto-load.
`ConnectUiState` holds `selectedPortalId` and `availablePortals: List<PortalInfo>`.
`AppPreferencesStore` persists `selectedPortalId` across sessions.

Connect flow uses selected portal directly (no auto-detection matching):
1. Bind WiFi → check connectivity
2. `PortalFound(url)` → `loginWithPortal(portal, url)`
3. `Unknown` → `loginWithPortal(portal, portal.defaultEntryUrl)` (fallback)
4. Verify → unbind

Auto-connect on launch: if saved portalId + credentials exist, `connect()` fires automatically.

## NetworkBinder

`NetworkBinder` interface (`bindToWifi(): Boolean`, `unbind()`) forces HTTP traffic through WiFi.
Android: `ConnectivityManager.bindProcessToNetwork(wifiNetwork)` using `TRANSPORT_WIFI`.
iOS: no-op (iOS handles captive portal routing natively).
`connect()` wraps entire flow in `try { ... } finally { networkBinder.unbind() }`.

## Error Modeling

`ErrorType` enum categorizes errors for user-facing messages:

```kotlin
enum class ErrorType {
    NETWORK,
    UNSUPPORTED_PORTAL,
    LOGIN_FAILED,
    VERIFICATION_FAILED,
}
```

`ConnectionState.Error` carries `message`, `type: ErrorType`, and `debugLog: String`.
`StatusIndicator` maps `ErrorType` to localized user-facing strings.
On error, a copy icon appears to copy the full debug log to clipboard.

## Debug Logging

`IbbWifiPortal` collects step-by-step debug logs via `StringBuilder` during login flow.
`ConnectViewModel` wraps portal logs with connection metadata (masked phone, portal name, timestamps).
Debug log is carried through `LoginResult.debugLog` → `ConnectionState.Error.debugLog`.
Phone numbers masked in logs via `maskPhone()`.

## ViewModel Pattern

`ConnectViewModel` extends `ViewModel()`, owns `ConnectUiState` via `MutableStateFlow`.
Dependencies: `CredentialsStore`, `portals: List<CaptivePortal>`, `ConnectivityChecker`, `AppPreferencesStore`, `NetworkBinder`.

Key behaviors:

- Init: `initializePortalState()` populates available portals, resolves saved selection, loads creds, auto-connects if ready
- `selectPortal(id)`: persists preference, loads portal-specific creds, resets state
- In-progress guard: `connect()` rejects calls while Checking/LoggingIn/Verifying
- Country code validation: blank country code rejected along with phone/password
- Credentials persisted on every input change per portal (encrypted at rest)
- `handleDetection()`: uses selected portal directly, falls back to `defaultEntryUrl` on Unknown

## Localization

`AppLanguage` enum defines supported languages (English, Turkish).
`LanguageTags` handles BCP-47 tag normalization.
`AppEnvironment` composable provides language context via `CompositionLocal`.
Platform locale override via `expect`/`actual` `ProvideAppLocale` composable.
System language detection via `expect fun readSystemLanguageTag()`.
User-facing strings via Compose Resources generated accessors.

## Ktor Usage

- `HttpClient()` auto-discovers engine from classpath
- `HttpTimeout` installed on all clients (request + connect timeouts)
- `User-Agent: Mozilla/5.0` on all portal/connectivity requests via `defaultRequest`
- Manual cookie management with deduplication by cookie name (`mergeCookies` uses `linkedMapOf`)
- `followRedirects = false` for portal detection
- Manual JSON body construction via kotlinx.serialization
- Trusted host validation before following redirects or posting credentials
- Redirect handling includes 301/302/303/307/308

## Compose Resources

Generated accessor package: `captiveconnect.composeapp.generated.resources`

## ConnectScreen Decomposition

`FormArea` delegates to smaller composables:

- `NetworkSection` → `SectionLabel` + `PortalPicker`
- `PhoneSection` → `SectionLabel` + `CountryCodeField` + `PhoneField`
- `PasswordSection` → `SectionLabel` + `AppTextField`

Helper composables split across `ConnectScreen.kt` (private) and `ConnectScreenComponents.kt` (internal).
Retry button calls `connect()` directly (not `resetState()`), ViewModel handles re-entry.
Success button calls `resetState()` for new connection flow.
