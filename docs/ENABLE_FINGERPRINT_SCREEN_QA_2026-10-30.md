# Veldigo Wallet — Screen 9: Enable Fingerprint

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference and geometry

Source: user-provided `Veldigo Crypto Wallet UI Mockup.png`, 1536 × 1024 px, handset **9. Biometric**, second phone in the second row. A scaled enlarged crop was inspected **locally only**; it has not been sent to the chat.

Visible reference:
- Nearly black midnight-navy background, white status indicators, a lone upper-left back arrow.
- Large centered thin mint ring with an oversized mint fingerprint icon in its middle.
- White bold centered heading **Enable Fingerprint**.
- Muted centered copy **Use your fingerprint to quickly and securely access Veldigo.**
- Single full-width mint **Continue** button toward the bottom; no fake PIN entry, biometric result, skip or extra image.
- The reference is a low-resolution phone mockup nested in a composite, not a real Android screenshot with known dp density. No numeric 1:1 claim is possible from this artifact alone.

## Source implementation in the SAME PR #7

- `walletkit/.../settings/security/setup/EnableFingerprintPage.kt`:
  - New Compose-native screen with responsive insets, back navigation, large 175dp custom Canvas fingerprint/outer mint circle, actual reference wording and 54dp mint button.
  - `@Serializable` route carries **only a non-sensitive Boolean** (`finishOnSuccess`); `HSPage(screenshotEnabled=false)` and no saved biometric tokens.
  - `Continue` performs live PIN, device lock and biometric enrollment support checks. No need to handle or retain biometric samples, passwords or key material in the app.
  - AndroidX `BiometricPrompt` is launched **only** on a user click using `BIOMETRIC_WEAK`, matching the existing wallet's current supported-biometric policy. Its callbacks execute on the activity main executor.
  - `App.pinComponent.isBiometricAuthEnabled = true` occurs **only** in `onAuthenticationSucceeded`, after rechecking all prerequisites. Cancellations and failures **do not enable** the setting.
  - If PIN is missing, existing secure six-digit `SetPinPage` opens; the user must return and press Continue again, at which point prerequisites are rechecked. No secret, PIN or biometric state is faked.
  - From onboarding, success returns to the root `EntryPage`; from settings, success returns to existing `SecuritySettingsPage`. Back leaves the setting unchanged.
  - The prompt is cancelled on screen disposal and never runs from a composable effect or without user interaction.
- `SecuritySetupPage.kt`: Biometric choice on screen 8 now opens real `EnableFingerprintPage(finishOnSuccess=true)`, rather than only displaying general settings.
- `PasscodeBlock.kt`: biometric switch from existing Security Settings now navigates through `EnableFingerprintPage()` after the existing PIN prerequisite. Previously its callback set `enableBiometrics()` without a live OS biometric challenge.
- `veldigo_welcome_strings.xml`: reference copy, OS prompt title and clear explanations for unavailable device, missing system lock and unsuccessful authentication.
- `walletkit/src/test/.../settings/security/setup/BiometricSetupGateTest.kt`: pure enrollment decision tests; negative cases include no device lock, missing PIN, unsupported biometrics, cancelled authentication and failure.
- `app/src/test/.../nav3/SecuritySetupRouteTest.kt`: protected route with no credential properties and serializable onboarding completion intent.
- `.github/workflows/android-verify.yml`: the new security-gate test runs as a mandatory step after Android build.

## Security invariants

1. **Never enable biometrics on Continue alone.** A genuine Android success callback AND a still secure device AND an existing PIN AND current biometric support are all required.
2. Unsupported hardware and unregistered biometrics get an explanatory dialog, not a fake enabled toggle.
3. No biometric samples, private keys, PINs, mnemonics or auth success token appear in navigation arguments or debug output.
4. Existing PIN/biometric lock implementation remains in place; no disabling of screen lock checks or fallback creation of weak alternative credentials.
5. AndroidX's currently configured `BIOMETRIC_WEAK` policy can allow a qualifying face modality, not exclusively fingerprints. Hardware-specific sensor testing is required. If a stronger policy is later adopted, update both enrollment and unlock together.

## QA / release gates

| Gate | State when authored |
|---|---|
| Reference screen and copy inspected | YES |
| Source and security navigation updated | YES |
| PIN/device/biometric checks in source | YES |
| OS challenge required on enable | YES, source implementation |
| Failed/cancelled prompt cannot enable | YES, source guard |
| Unit tests committed and included in Android CI | YES; execution pending |
| Latest commit builds on GitHub CI | NOT CONFIRMED YET |
| Physical Android BiometricPrompt positive/negative tests | NOT RUN |
| Biometric app-lock behavior after restart | NOT RUN |
| Android emulator + original-reference pixel-diff | NOT RUN |
| Icon exact path and native-density parity | **NOT VERIFIED** (Canvas approximation) |
| Production financial security audit | NOT COMPLETE |

## Manual device verification checklist

Use an isolated test wallet with **no real funds** and a fully updated Android device:
- Device without enrolled biometrics: Continue must not enable the option; enrollment notice is shown.
- Device without screen lock: Continue must refuse biometric enabling.
- Device with enrolled biometrics but no wallet PIN: Continue must open the real PIN setup, then require the user to retry.
- Device with secure lock, real PIN and enrolled biometrics: Continue shows Android's real biometric prompt.
- Press Android Cancel/Back or fail authentication: storage remains unchanged and app does not navigate as if success.
- Succeed with a real enrolled biometric: enable flag becomes true, subsequent unlock works, and the value persists across app restart.
- Navigate away while the prompt is visible: prompt cancels and no flag is written.
- Test gesture navigation, display insets, TalkBack, small-height landscape and font scale.
- For pixel QA, use Compose previews or non-sensitive isolated test mode, not actual secret-phrase captures or disabling screenshot restrictions in production.

Next target from the reference: **10. Home / Wallet**, following a full CI and device-security verification pass.
