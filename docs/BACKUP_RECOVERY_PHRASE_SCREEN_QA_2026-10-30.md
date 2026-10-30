# Veldigo Wallet — Backup Your Recovery Phrase (screen 6)
Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Source
The sixth phone in the first row of `Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024 overall, x≈1080–1310, y≈0–355 within the composite).

Observed reference design:
- Dark navy/black canvas; white back arrow; no centered wordmark on this screen.
- Two-line **Backup Your / Recovery Phrase** heading, white.
- `Write down these 12 words in order and keep them safe. Never share them with anyone.` muted subtitle.
- A rounded, dark rectangle with **12 words arranged in 3 columns and 4 rows**, each prefixed by its 1-based position.
- Wide mint `I've written it down` primary button and mint `Show different words` link.
- Source sample words are part of the **mockup only**; they are not embedded in the app.

## Implemented in existing application
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/manageaccount/backupkey/BackupKeyPage.kt`: navy/mint typographic hierarchy, 3-column adaptive mnemonic card, dynamic count, 54dp main action, original navigation callback, semantic back control and optional passphrase warning.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localized reference heading, description and actions.
- The words come **exclusively** from `BackupKeyModule.Factory(account)` and `BackupKeyViewModel`, which use the persisted real `AccountType.Mnemonic` or `MoneroMnemonic` account. It renders 12, 15, 18, 21, 24 or 25 words as appropriate, never substituting a demo phrase.
- The original `BackupConfirmKeyPage` verification step is retained; the main CTA moves to confirmation and **does not change `isBackedUp`**. The existing confirmation ViewModel marks a backup only after correctly selected words.
- `CreateAccountViewModel.kt` holds the newly persisted account temporarily and prevents duplicate Create taps; `CreateAccountStandardPage.kt` routes the actual newly saved account to its backup page after creation.
- `BackupKeyPage` and `BackupConfirmKeyPage` navigation data now contain **only account IDs**; the account with its secrets is read on the protected screen via `App.accountManager.account(accountId)`. A missing account closes the screen. This reduces exposure in serializable navigation state while preserving existing Account-based callers through secondary constructors.
- `app/src/test/.../SensitiveBackupRouteSerializationTest.kt`: JSON roundtrip and no-secret navigation payload assertions.
- `maestro/subflows/create_standard_wallet.yaml`, `maestro/onboarding/create_wallet_standard.yaml` and `maestro/onboarding/recovery_phrase_backup.yaml`: follow the real security-sensitive route but do **not** print or assert private recovery words or claim backup verified.

## Deliberate security deviations from the mockup
1. **Hidden on entry**: the supplied mockup displays words immediately. This implementation requires user action to reveal them, and hides them again on activity pause.
2. **No phrase rotation**: `Show different words` opens an explanation that an already generated wallet's seed cannot be replaced without changing wallet identity; no keys are regenerated silently.
3. **Verification required**: `I've written it down` is not final confirmation. Only the existing randomized word quiz may mark the account backed up.
4. **FLAG_SECURE retained**: both pages use `HSPage(screenshotEnabled = false)`, and the navigation host applies `WindowManager.LayoutParams.FLAG_SECURE`. No clipboard action or share/export of seed words was added.
5. **Optional BIP39 passphrase**: when the real account has one, the screen explicitly asks the user to also record that secret.

## Acceptance checklist
| Gate | Current status |
|---|---|
| Original composite reference inspected | YES |
| Source changes committed on the existing PR | YES |
| No sample seed phrase used in production code | YES, source review |
| Existing actual mnemonic model reused | YES, source review |
| Verification must precede `isBackedUp = true` | Existing ViewModel; retained |
| Serializable navigation no longer contains Account | Implemented; JUnit test added, not executed yet |
| Android build of latest commit | PENDING CI; never presume success |
| JUnit security/serialization tests executed | NOT CONFIRMED |
| Maestro on device (sensitive output disabled) | NOT RUN |
| Real device screenshot + matched pixel diff | NOT RUN |
| Reference visual 1:1 certified | **NO**; source is a reduced composite |
| Key generation/storage independently audited | NO; required before real funds |

## Next screen
Screen 7: **Confirm Recovery Phrase**. Do not use fake or static mnemonic content; preserve private-data screen capture restrictions, per-word validation, and the real backup confirmation gate.

**Do not merge this draft while critical Android CI or security checks are failing.**
