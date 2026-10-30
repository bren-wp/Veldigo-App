# Veldigo Wallet — screen 7: Confirm Recovery Phrase
Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Visual reference
Approved source: `Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024 pixel composite), first-row handset **7. Confirm Seed**, approximately x=1315–1525, y=0–355.

Visible design to compare:
- Almost-black `#030E0F` background, Android status area and white back arrow.
- White **Confirm Recovery Phrase** single-line heading near upper left, approx 14–17 px in scaled handset.
- Muted description **Tap the words in the correct order.**
- Twelve dark outlined selectable word chips in a **3-column × 4-row** grid. Real words are shuffled rather than reference placeholder words.
- A **Confirm** button near the bottom. It is dark/disabled before successful selection, mint when enabled.
- No sample mnemonic, fake verification result or fabricated account state.

## Implementation in this PR
1. `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/manageaccount/backupconfirmkey/BackupConfirmKeyPage.kt`
   - New live native Compose layout, 3-column outlined chips, navy/mint styling, reference title and supporting copy.
   - Safe scroll on 15, 18, 21, 24 and 25-word accounts; independent word chips with unique source-index IDs.
   - A small progress caption is an intentional usable deviation from mockup.
   - Final **Confirm** is disabled until the actual saved phrase has been entered in original order.
   - On app pause all selections reset and word labels are hidden until resume; no clipboard or share operation.
   - Page still inherits `HSPage(screenshotEnabled=false)` and loads the actual account from `App.accountManager.account(accountId)`; mnemonic does not enter persisted navigation state.
   - Real success HUD and navigation back to the wallet after the ViewModel confirms the backup.
2. `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/manageaccount/backupconfirmkey/BackupConfirmKeyViewModel.kt`
   - Verify **every word in order** (not 2–4 randomly selected words).
   - The checker handles repeated words through unique source indices and matches text to the next expected phrase position.
   - Wrong word resets the pending attempt and emits an existing localized error, **without** touching the account's `isBackedUp` flag.
   - Selection completion only enables Confirm; **accountManager.update(account.copy(isBackedUp=true)) happens exclusively on the explicit `confirmBackup()` call** after verifying all words selected.
   - No encryption, key generation or phrase content is modified.
3. `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable title, description, Confirm label and progress.
4. `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/manageaccount/backupconfirmkey/RecoveryWordVerifierTest.kt`: pure unit tests for incomplete selection, duplicate options, wrong word and unavailable choice.
5. `.github/workflows/android-verify.yml`: adds the walletkit JUnit test as mandatory CI after build and the preexisting navigation-serialization security check.

## Security acceptance
| Gate | Status at authoring |
|---|---|
| Existing non-capturable page attribute retained | YES in source |
| Account ID only in navigation payload | YES from previous screen work |
| No real secret in CI tests or documentation | YES |
| Correct 12-word sequence gates Confirm | YES in source |
| 15–25 words accommodated | YES in source; device UX untested |
| Repeated words have stable identifiers | YES in source |
| Confirm action required before backup status update | YES in source |
| Wrong word cannot mark backup verified | YES in source |
| Lifecycle pause resets selection/masks words | YES in source |
| Latest Android Gradle compile and unit tests | PENDING GitHub CI |
| Maestro instrumented run | NOT RUN |
| Exact pixel-diff versus same-size Android device screenshot | NOT RUN |
| **1:1 pixel parity VERIFIED** | **NO** |
| Safe for real assets and production | Independent crypto/privacy audit still mandatory |

### Visual limitations
The approved input is a small flattened montage, not a screenshot at native Android density. Pixel-perfect numeric alignment cannot be certified from the illustration alone. Since the real phrase view is protected by FLAG_SECURE, do **not** disable that flag or capture live seeds for visual testing. Use a completely isolated non-asset demo fixture in a test harness for image comparison, without allowing bypass in distributed builds.

## Next reference panel
Screen 8: **Secure Your Wallet** (Security Setup). Preserve actual PIN, biometric availability and key protection; do not claim password-based key storage without an implemented secure workflow.
