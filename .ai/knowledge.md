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

## Secure Storage

- Android: `EncryptedSharedPreferences` with `MasterKeys.AES256_GCM_SPEC`
- iOS: Keychain via `SecItemAdd`/`SecItemCopyMatching`/`SecItemDelete` with `kSecClassGenericPassword`
- Service identifier: `com.cemalturkcan.captiveconnect`

## expect/actual

Prefer `expect fun` over `expect object`. Create platform implementations in
`androidMain` and `iosMain` matching exact signatures.
Examples: `ProvideAppLocale`, `readSystemLanguageTag()`, `KeyValueStorage`.

## Portal Architecture

`CaptivePortal` interface with `name`, `canHandle(entryUrl)`, and `login(entryUrl, credentials)` methods.
Each WiFi portal (IBB, etc.) implements this interface.

Detection is handled separately by `ConnectivityChecker.check()`, which returns `DetectionResult`:

```kotlin
sealed class DetectionResult {
    data object Online : DetectionResult()
    data class PortalFound(val entryUrl: String) : DetectionResult()
    data class Unknown(val reason: String) : DetectionResult()
}

sealed class LoginResult {
    data object Success : LoginResult()
    data class Failure(val reason: String) : LoginResult()
}
```

Portal detection uses connectivity check redirect URL matching with strict host validation.
Trusted hosts defined in `TRUSTED_PORTAL_HOSTS` / `TRUSTED_HOSTS` sets with `.endsWith()` matching.
`canHandle()` validates URL host against allowlist before accepting portal.

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

`ConnectionState.Error` carries both a developer `message` and a `type: ErrorType`.
`StatusIndicator` maps `ErrorType` to localized user-facing strings.

## ViewModel Pattern

`ConnectViewModel` extends `ViewModel()`, owns `ConnectUiState` via `MutableStateFlow`.
Dependencies: `ConnectivityChecker`, `CredentialsStore`, `AppPreferencesStore`, portal list.
Exposes public functions for UI actions (connect, save credentials, update preferences).

Key behaviors:

- In-progress guard: `connect()` rejects calls while Checking/LoggingIn/Verifying
- Country code validation: blank country code rejected along with phone/password
- Credentials persisted on every input change for immediate recall (encrypted at rest via EncryptedSharedPreferences / Keychain)

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
- Manual cookie management with deduplication by cookie name (`mergeCookies` uses `linkedMapOf`)
- `followRedirects = false` for portal detection
- Manual JSON body construction via kotlinx.serialization
- Trusted host validation before following redirects or posting credentials

## Compose Resources

Generated accessor package: `captiveconnect.composeapp.generated.resources`

## ConnectScreen Decomposition

`FormArea` delegates to smaller composables:

- `PhoneSection` → `SectionLabel` + `CountryCodeField` + `PhoneField`
- `PasswordSection` → `SectionLabel` + `AppTextField`
  All private helpers, keeping each function under 40 lines.
  Retry button calls `connect()` directly (not `resetState()`), ViewModel handles re-entry.
