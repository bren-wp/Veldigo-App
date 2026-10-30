# VELDIGO Wallet — Screen 26: Security Setup / PIN & Biometrics

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Visual reference

The user's saved `Veldigo Crypto Wallet UI Showcase.png` contains illustrated **panel 10 — Set PIN** and **panel 11 — Biometrics**. The incremental wallet-only implementation sequence numbers this paired flow **screen 26**, following screen 25 recovery-phrase backup and confirmation.

Illustrated reference traits: midnight navy background, mint line-art lock/fingerprint, six PIN indicator dots, dark circular number keys, concise headings, a mint-accented primary biometric action, and consistent back-navigation controls. These are concept phone mockups embedded in a reduced multi-phone image; no full-resolution density- and inset-calibrated reference is available. Consequently all code measurements below are implementation targets, not proven pixel measurements.

## Implemented

1. `SecuritySetupPage.kt`:
   - Existing native security chooser updated to shared `VeldigoHomePalette` (`#071421`, `#112337`, `#41E6C7`, `#B5C6D7`, `#234254`), 17dp rounded actionable cards and a 60dp header.
   - Removed the visible **Password** action, which previously opened a notice but did not implement password support. Only working **Biometric** and **PIN Code** routes remain.
   - Biometric selection uses existing `BiometricSetupGate.preflight` to check device lock, existing PIN and supported OS biometrics. When PIN is missing, the flow opens the real PIN setup instead of pretending biometrics were enrolled; missing device lock/biometrics shows existing explanation.
   - Rechecks device/PIN/biometric prerequisites on lifecycle `ON_RESUME` when returning from Android settings or PIN creation.
   - Protects the page from screenshots/recent app thumbnails with `HSPage(screenshotEnabled=false)`, matching the existing PIN and fingerprint pages.
2. `PinSetView.kt`:
   - Native Compose navy/mint PIN view with 60dp header, local Mint lock icon, six dots, real numeric keyboard, backspace, haptic feedback, and visible validation error state.
   - Responsive keypad geometry: 58dp or 66dp keys and 9dp/12dp spacing based on available screen height; scrollable narrow layout.
   - It still calls the existing `PinSetViewModel.onKeyClick`, `onDelete` and the original `PinComponent` state machine. The PIN is never displayed in plaintext, put into Compose saved-state, sent over network or inserted into navigation arguments.
   - `LaunchedEffect(state.finished)` delivers successful navigation after the confirmed PIN was persisted rather than during Compose rendering.
3. `PinSetViewModel.kt`:
   - `PinSetInputRules` rejects non-single-digit values, excess digits and further input after completion; preserves the existing six-digit state machine, double entry and unique-PIN checks.
   - Clears transient plaintext string references on completed setup and when the ViewModel is destroyed/cancelled. Java/Kotlin immutable Strings cannot be guaranteed to be cryptographically zeroized in RAM; this is best-effort release, not full memory erasure.
4. `EnableFingerprintPage.kt`:
   - Matches shared navy/mint styling, card/primary-button radius and responsive sizing.
   - Keeps the original **Android `BiometricPrompt`**. A successful Android result is still required, then `BiometricSetupGate.mayEnableAfterResult` rechecks all prerequisites; cancellation/error never enables biometrics.
   - Existing device capability policy currently uses `BIOMETRIC_WEAK` in `SystemInfoManager` and prompt; strong-only policy and AndroidKeyStore key authorization need an independent security review before production.

## Tests/CI

- `app/src/test/.../SecuritySetupRouteTest.kt` now asserts `SecuritySetupPage`, `SetPinPage` and `EnableFingerprintPage` are screenshot-protected and only serialize navigation intent, not plaintext PIN or biometric credentials.
- Existing `BiometricSetupGateTest` covers secure-device, PIN, sensor and successful authentication gating.
- New `PinSetInputRulesTest` exercises all decimal digits, invalid numbers, six-character cap and completed state.
- `.github/workflows/android-verify.yml` requires the new PIN tests before debug artifact publication.

## Required before 1:1 or real-funds release

| Verification | Current status |
|---|---|
| Changes, safety checks and regression sources committed to same open PR | Yes |
| PIN still stored through existing production component | Source inspected, **device test pending** |
| Biometric enrollment requires actual Android challenge | Source inspected, **device test pending** |
| All buttons wired to real flows; no fictitious password feature | Verified in source |
| Screen-density, system inset and font-scale calibration | Not performed |
| Pixel-by-pixel screenshot overlay vs real emulator render | **Not performed; 1:1 unverified** |
| CI compile, unit tests and debug APK/AAB | **Latest run pending or in progress; inspect Actions** |
| Real PIN retry, race, lockout, duress, passkey and app-background cases | Not verified |
| Real Android fingerprint/face hardware tests and interrupted prompt | Not verified |
| Cryptography, KDF, secure-memory, keystore and biometric strength security audit | Not performed |

The PR **must remain draft/unmerged**. Never claim production key custody or use engineering test artifacts with real funds.

## Next implementation screen

**Screen 27 — Settings / Security overview and wallet preferences** in the wallet-only functional sequence (the illustrated mockup labels **panel 23 — Settings**). Continue in the same PR #7; do not generate chat images.
