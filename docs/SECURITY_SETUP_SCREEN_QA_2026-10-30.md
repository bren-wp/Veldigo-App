# Veldigo Wallet — Screen 8: Secure Your Wallet

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Existing branch: `feat/onboarding-02-security-mint`

## Exact reference input
User-provided board: `Veldigo Crypto Wallet UI Mockup.png`, 1536 × 1024 px. Screen **8. Security Setup** is the leftmost handset of the second row, approximately x=0–214 and y=390–715 of the composite. It is **not a same-density screenshot** from a running Android app.

Visible design:
- Nearly black/midnight-navy gradient, white Android status icons.
- Header back arrow at left; mint V + Veldigo brand centered.
- White left-aligned heading `Secure Your Wallet`, with muted two-line explanation `Choose how you want to unlock Veldigo.`
- Three stacked dark outlined rounded tiles: mint square icon container, title, gray subtitle and white right-chevron:
  - `Biometric (Recommended)` / `Use fingerprint or face unlock`
  - `PIN Code` / `Set a 4-6 digit PIN`
  - `Password` / `Use a custom password`
- There is no continue CTA or freeform credential entry on this reference screen.

## Implementation
- New `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/settings/security/setup/SecuritySetupPage.kt`:
  - Native Compose status/navigation insets, brand header, responsive scroll, 94dp cards and local Canvas fingerprint/PIN/lock icons, no WebView or external graphics.
  - `SecuritySetupPage` is `@Serializable` and is not accessible when the app is PIN locked.
  - Biometric opens the **real existing** `SecuritySettingsPage`, whose switch uses the `ensurePinSet` gate. Unsupported devices receive a clear message, rather than a fake supported state.
  - PIN opens existing protected `SetPinPage`. If already set, opens the security settings with existing PIN-edit authorization.
  - Password shows a truthful unsupported-feature dialog. No password backend is created or represented as implemented.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/manageaccount/backupconfirmkey/BackupConfirmKeyPage.kt`: after **actual** complete mnemonic confirmation and `isBackedUp`, remove recovery pages, then open setup only when no PIN exists. The existing verification engine remains the sole backup gate.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable reference and capability strings.
- `app/src/test/java/io/horizontalsystems/bankwallet/nav3/SecuritySetupRouteTest.kt`: test safe serialization, PIN lock gate and actual PIN length.
- `.github/workflows/android-verify.yml`: run the additional JUnit test in Android CI.

## Intentional security/correctness differences
- The mockup describes a 4–6 digit PIN, but `PinModule.PIN_COUNT` is **6**. The implementation says `Set a 6-digit PIN`, rather than misleading the user.
- `Password` is **not implemented** in the repository; the subtitle says `Custom password (not supported)` and the action explains why. Do not replace the existing encrypted key storage with a fake local password field.
- On a device lacking supported biometrics, the biometric entry states it is unavailable. The existing settings screen is used until the dedicated **Screen 9: Enable Fingerprint** receives its own validated design and secure workflow.
- PIN is optional to complete this UI pass. This change does not imply the account is production-safe or guarantee every user enabled device protection.

## Acceptance
| Gate | Status |
|---|---|
| Reference screen 8 inspected | YES |
| Screen 8 created as real Compose content | SOURCE COMMITTED |
| Existing PIN setup and biometric settings reused | YES, source reviewed |
| Routing from successful backup confirmation | YES, source reviewed |
| Password not misrepresented | YES |
| Actual PIN length matches source implementation | YES (6) |
| Unit test added to mandatory CI | YES; results pending |
| Android Gradle build latest commit | PENDING until latest CI completes |
| Real-device setup, PIN, biometric lifecycle QA | NOT RUN |
| Same-resolution screenshot overlay / pixel-difference | NOT RUN |
| Source/implemented screen pixel-perfect 1:1 | **NOT VERIFIED** |
| Production wallet security audit and real-money release | **NOT APPROVED** |

## Next screen
**Screen 9: Enable Fingerprint**. Its button must initiate actual Android BiometricPrompt eligibility and successful authentication before enabling the setting; avoid enrolling or enabling biometrics without verifying the OS device capability. Continue in this existing PR until requested otherwise.
