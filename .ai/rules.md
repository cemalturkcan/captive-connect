# Rules

## Code Quality

- No comments in code
- No TODOs or FIXMEs
- No dead code or unused imports
- No magic strings or numbers — use named constants or string resources
- One public `@Composable` function per file (named same as file); private helper composables allowed
- All UI values (dp, sp, colors, durations) via token constants in `UPPER_SNAKE_CASE`
- Use `val` and immutability everywhere possible
- Never use `!!` — use safe calls, `let`, or `requireNotNull` with message
- Full words for all identifiers — no abbreviations
- All interactive elements must have `contentDescription` for accessibility

## Architecture

- `<300` lines per file
- `<40` lines per function
- State hoisting: stateless composables receive state + callbacks
- KMP platform-neutral `commonMain` — no Android/iOS imports
- `expect`/`actual` functions preferred over `expect`/`actual` objects
- Frontend-first approach — UI drives architecture decisions
- Interface + DefaultImpl pattern for stores and repositories

## Security

- Credentials encrypted at rest (EncryptedSharedPreferences / Keychain)
- Portal URLs validated against trusted host allowlist before credential submission
- Host matching: `host == trusted || host.endsWith(".$trusted")`
- Credentials persisted on input change for immediate recall (encrypted at rest)
- Dual storage: secure (credentials) and plain (preferences) with separate implementations

## Conventions

- Localization for all user-facing copy via Compose Resources string accessors
- Data classes use `.copy()` for mutations
- Store pattern: private `MutableStateFlow` + public `StateFlow` via interface
- Sealed classes for state machines with exhaustive `when`
- Token file per category in `ui/tokens/`
- `collectAsState()` for observing StateFlow in composables
- HttpTimeout configured on all Ktor clients
- Cookie merging deduplicates by cookie name
- Portal-aware credential keys: `cred_{portalId}_{field}`
- User-Agent header on all HTTP clients (`Mozilla/5.0`)
