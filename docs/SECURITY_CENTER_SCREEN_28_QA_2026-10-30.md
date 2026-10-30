# VELDIGO Wallet — Screen 28: Security Center

Date: 2026-10-30
Existing draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Visual reference and interpretation

This is **functional wallet-only screen 28**, following PIN/Biometrics setup (screen 26) and Settings overview (screen 27). Previously provided VELDIGO UI Showcase panel 23 (Settings), panels 10–11 (PIN/Biometrics), and shared palette are the available visual references. The source is a flattened concept collage; there is **no separate native-resolution Security Center screen** available for a calibrated per-pixel overlay.

The UI uses Midnight Navy `#071421`, raised navy `#112337`, mint `#41E6C7`, blue-grey `#B5C6D7` and subtle borders `#234254`. Implementation targets: 60dp minimum top/back bar, 16dp horizontal card margins, 20dp summary and 16dp section card corners, 60dp minimum touch rows, native switches, grouped sections and responsive long-label wrapping. These measurements are design goals, **not measured pixel equality**.

## Implemented on the existing wallet code

**`SecuritySettingsPage.kt`**
- New native Compose Security Center with screen capture protection: `HSPage(screenshotEnabled=false)`. The route remains inaccessible while wallet is locked.
- Source-backed summary: actual configured app PIN and effective biometric enablement, **not** a fabricated security percentage/grade.
- **App access**: Set/Edit six-digit PIN, biometric enable/disable, Auto-Lock interval only when PIN is present. PIN edits use `navigation.authorizedAction` and the original `EditPinPage`; setup uses `SetPinPage`.
- Enabling biometrics uses `navigation.ensurePinSet` to enter the existing `EnableFingerprintPage` with genuine Android `BiometricPrompt`. Disabling biometrics additionally requires PIN authorization.
- **Privacy protection**: auto-hide balances and hide suspicious transactions via the original `BalanceHiddenManager` and `SpamManager`. Switch rows do not also respond to a duplicate parent-row click.
- **Advanced security**: real duress/emergency PIN setup or edit, with the original `SetDuressPinIntroPage`, `EditDuressPinPage` and PIN authorization. Removing an existing duress PIN requires explicit confirmation **and** existing authorization.
- **Access management**: disabling PIN is visually separated as an exceptional action. The user confirms consequences in a dialog, then the original authorization gate invokes `disablePin()`. This also disables biometric preference.
- Preserved `SecurityCenterCell` public API used by Privacy settings; it is not removed as dead code.

**`SecuritySettingsViewModel.kt` and factory**
- Only wallet-local PIN/duress/biometric, balance-privacy, suspicious-transaction and auto-lock states are read. Paid subscription, Defense System and third-party connection state are no longer initialized/collected in this Security Center.
- State is reread on lifecycle resume, after returning from actual PIN/biometric/OS settings.
- `SecurityCenterStateRules.biometricActive(pinSet, biometricSettingEnabled)` rejects stale biometric-active indicators when the wallet PIN was removed. Never declare biometric protection solely from a persisted boolean flag.
- Disabling PIN clears the biometric enabled preference; temporary UI transitions do not assert that a new PIN or biometric enrollment has succeeded before the actual component reports it.

## Regression gates

| Gate | Status |
|---|---|
| Source changes in existing open/draft PR #7 | Yes |
| Actual PIN, biometric, auto-lock, privacy and duress settings preserved | Source reviewed |
| PIN/biometric/duress changes routed through existing real authorization | Source reviewed |
| Confirmation before disabling PIN or removing duress PIN | Implemented |
| `SecurityCenterStateRulesTest`: effective biometrics, auto-lock, missing PIN | Added to CI |
| `SecurityCenterRouteTest`: secure navigation and credential-free serialization | Added to CI |
| CI checks no unused paid/connection feature symbols in Security Center | Added |
| Completed Android compilation and unit-test suite for final commit | **Not established** |
| Real Android PIN, duress, biometric sensor and auto-lock end-to-end tests | **Not performed** |
| Android narrow devices, tablets, 1.3–2.0 font scale, TalkBack, dark/light contrast | **Not performed** |
| Full-resolution emulator screenshot and calibrated pixel-diff | **Not performed** |
| **Pixel-perfect 1:1** | **NOT verified** |
| Security review of PIN database, hardware-backed keystore, biometric strength, lockout and anti-rollback | **Pending** |

## Security follow-ups

1. Validate all set/edit/delete PIN paths on a real phone with actual OS authorization, including when a sensor is removed, device lock is disabled, or user changes the device credential.
2. Verify that disabling the main PIN interacts correctly with any existing duress PIN and lockout records. This UI does not modify the low-level `PinManager` contract.
3. Android `SystemInfoManager` currently uses `BIOMETRIC_WEAK`. An independent threat-model review is needed before claiming hardware-backed strong biometric security.
4. Verify `FLAG_SECURE` prevents key/credential screenshots across transitions. Use synthetic/test-only state for visual QA; never disable protection on a live wallet containing real seeds.
5. Inspect backup, key derivation, secure persistent storage and recovery with known vectors and supported chains before any production or real-funds claim.

## Next screen

**29 — Privacy Settings** (local UI stats, Tor/network privacy and supported network options) in the same draft PR. As in previous screens, do not generate images in chat, merge or release production artifacts until validation is complete.
