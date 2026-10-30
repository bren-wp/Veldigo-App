# VELDIGO Wallet — Screen 25: Recovery Phrase Backup / Verification

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Visual reference and sequence

The previously supplied `Veldigo Crypto Wallet UI Showcase.png` contains panels **7. Create Wallet** (12 recovery words) and **8. Confirm Phrase**. The earlier `Veldigo Crypto Wallet UI Mockup.png` likewise depicts **6. Seed Phrase** and **7. Confirm Seed**. In the functional wallet-only development sequence, this paired recovery/verification flow is **screen 25**. These are flattened concept boards, not calibrated Android screenshots.

Design implementation uses the established Midnight Navy/Mint palette:
- `#071421` full-screen background; `#112337` card surface; `#234254` subtle borders; `#41E6C7` primary Mint action; `#B5C6D7` supporting copy.
- 60dp minimum back/header row, title and warning text hierarchy, masked-word first state, scrollable grid, rounded Mint reveal/continue/confirm buttons.
- Individual numbered word cards; **two columns on phones** and **three on wide tablets** using a measured width >= 520dp, to avoid truncating mnemonic words on compact devices.
- Confirmation uses the matching grid geometry for the shuffled real word choices; shows next word index and number selected. Disabled chips have Mint selection affordances and localized accessibility state descriptions.

## Privacy and real-wallet semantics

- `BackupKeyPage(accountId)` and `BackupConfirmKeyPage(accountId)` preserve `HSPage(screenshotEnabled = false)`; the nav host currently toggles `FLAG_SECURE` for this protection. No Account or mnemonic is serialized into route arguments.
- Actual saved `AccountType.Mnemonic` or `AccountType.MoneroMnemonic` supplies the words and optional BIP39/Monero passphrase; no prefilled demo mnemonic, clipboard/share, random replacement or network upload is introduced.
- Words are hidden by default, shown only after tapping reveal, and immediately remasked on the page's `ON_PAUSE`. The warning explains the additional passphrase must be backed up separately.
- The pre-existing **Show different words** control is a notice only: it does not mutate a saved mnemonic.
- The first-page acknowledgement **does not mark the account backed up**. It routes to the confirmation page. In confirmation, the user must select **all words in their original order** before the explicit Confirm action.
- A challenge resets after a wrong word or app backgrounding; repeated identical BIP39 words remain represented by separate source-indexed chips.
- `RecoveryBackupAccountGuard` now checks the fresh account manager entry before persisting `isBackedUp = true`. If the account was deleted or its key-bearing `AccountType` or origin changed, stale confirmation is rejected rather than resurrecting the prior record.

## Validation gates and limitations

| Gate | Status |
|---|---|
| Same open/draft PR; changed code committed | Yes |
| Actual saved recovery phrase and passphrase, not examples | Code inspected |
| Secure flag and lifecycle masking kept | Source inspected |
| Full ordered phrase required before backup-state write | Existing logic retained |
| Deleted/replaced wallet cannot be marked backed up | Guard implemented, test added |
| Narrow layout renders 2 cols, wide layout 3 | Helper implemented and unit-tested in source |
| CI includes `RecoveryWordVerifierTest` plus two new regression test suites | Configured |
| CI completed successfully for latest HEAD | **Not established** |
| Emulator screenshots at target density and system-insets profile | **Not performed** |
| Pixel-by-pixel overlay / objective 1:1 | **NOT verified** |
| Android 320/360/393/480dp, tablets, large font, TalkBack | **Not tested on devices** |
| 12/15/18/21/24 BIP39 phrases and Monero 25-word end-to-end backup/restore | **Not tested on devices** |
| Randomness/KDF/seed storage/backup attack model security audit | **Not performed in this screen pass** |

Security-critical test checklist before merge: reveal/mask and task-switch behavior, wrong-word reset, repeated word handling, delete/switch wallets during confirmation, restoring words and optional passphrase to the identical receiving addresses, PIN/biometric gating, Android screenshots and screen-record prevention, and all supported chain derivation paths.

Do **not** use test builds with real funds or issue a production release while those gates are outstanding.

## Next screen

**26 — Security Setup / PIN & biometrics**, continuing in PR #7, without resetting the architecture or posting generated images in chat.
