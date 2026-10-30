# VELDIGO Wallet — Screen 24: Create Wallet Form

Date: 2026-10-30
Existing draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference and interpretation

The user's saved `Veldigo Crypto Wallet UI Showcase.png` shows a **Create / Import** chooser in panel 14, recovery phrase in panel 7 and seed confirmation in panel 8. It does **not** provide a dedicated full-resolution screenshot of the intermediate **wallet-name + advanced creation form**. Accordingly, screen 24 is implemented using the proven VELDIGO visual system, not falsely measured against a nonexistent pixel-for-pixel reference.

Design implementation targets:
- Background `#071421`; raised fields/cards `#112337`; accent `#41E6C7`; muted copy `#B5C6D7`; hairline `#234254`.
- 60dp-minimum back/title bar, 20dp horizontal content insets, 17dp advanced card radius, 14dp input/button corner radius, 54dp-minimum primary button, 14–24sp responsive type scale.
- Narrow-screen scroll, IME padding, bottom-anchored primary action, content descriptions, local Android drawables.
- No generated imagery or artificial wallet values / mnemonic words.

## Functional integration kept intact

`walletkit/.../createaccount/CreateAccountStandardPage.kt` was refactored into a wallet-only Compose form. It still uses `CreateAccountViewModel`, `CreateAccountModule.Factory`, `AccountFactory`, `WordsManager`, `AccountManager`, `WalletActivator` and `BackupKeyPage` from the existing app.

1. New wallet display name: observable ViewModel state, bounded to 48 UTF-16 code units, ISO control characters removed, empty input falls back to generated default. Never persist a half-surrogate pair.
2. Generate another name invokes the actual `AccountManager.getRandomWalletName()` behavior via ViewModel, with the same normalization.
3. Advanced settings switch reuses `setAdvancedOptionsEnabled`. When disabled, it resets to 12-word mode and clears optional passphrase and confirmation fields.
4. Recovery phrase length selector displays genuine supported BIP39 counts: 12, 15, 18, 21 and 24 words, with the existing `CreateAccountModule.Kind` title strings. `selectedKind` is now Compose-observable, fixing stale selector state.
5. Optional passphrase uses the existing `PassphraseValidator` restrictions, secret-masked input and passphrase confirmation error state. The passphrase cannot be exported in navigation arguments, state restoration or screenshots.
6. Create button calls the real `CreateAccountViewModel.createAccount`; existing duplicate-submit guard remains. It saves exactly one generated account and activates the existing default tokens. After success, the UI opens `BackupKeyPage(account)` which serializes only the saved account ID, **not its mnemonic**.
7. The form sets `HSPage(screenshotEnabled=false)`; the visibility toggle resets to hidden on `ON_PAUSE`. Transient passphrase fields are cleared after navigation success.

## Tests and known risks

- Added pure unit tests in `VeldigoWalletNameRulesTest`: Unicode names, stripping control characters, nonempty fallback, length cap and surrogate-safe cutoff.
- Required the test explicitly in `.github/workflows/android-verify.yml`.
- Existing mnemonic-generation, backup verification and navigation-safety CI checks remain in the workflow.
- **Actual Android compilation/test success: pending.** Full on-device creation/restore, 12/15/18/21/24 mnemonic type, passphrase recovery, protected screenshot and account activation not verified.
- **Pixel 1:1: NOT verified** — missing an original full-resolution reference for this specific intermediate form, emulator rendering, calibrated overlay, and density / font-scale QA.
- Do not handle real funds or make a production release before BIP39 vectors, randomness source, persistence, account derivation, recovery and exception-path security tests pass.

## Next functional screen

**25 — Recovery Phrase Backup / Verification flow** (saved key, secure reveal, word order check), continuing in the same existing PR #7. The old collage uses different panel numbers; never conflate the artwork's panel index with the incremental implementation sequence.
