# VELDIGO Wallet — Screen 23: Add Wallet

Date: 2026-10-30
Existing draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Design evidence
User's saved `Veldigo Crypto Wallet UI Showcase.png`, **panel 14 ("Add Wallet")** in the older 24-panel reference, adjacent to panel 13 ("Wallet List"). In the newly agreed wallet-only development sequence, this is functional **screen 23**. The screenshot is an uncalibrated flattened collage, not an original-resolution Android emulator capture.

Observed structure:
- Deep navy full-height background and small top-left back arrow.
- Compact **Add Wallet** page title aligned after the back arrow.
- Three vertically stacked rounded dark cards, each featuring one mint icon in a deep teal rounded tile, white title, grey-blue supporting text and right chevron.
- Actual labels: **Create New Wallet**, **Import Wallet**, **Watch Only**.
- No wallet addresses, seed words, balances, simulated transfer or fake connect-wallet status on this chooser.

## Changes in this PR
### Layout and branding
`walletkit/.../manageaccounts/AddWalletPage.kt` now renders the actual Compose screen, not a generic Lawrence-themed `HSScaffold`/shared upstream `AddWalletView` list:
- Uses established `VeldigoHomePalette`: `#071421` background, `#112337` raised cards, `#41E6C7` mint, `#B5C6D7` supporting text, `#234254` hairline edges.
- Header minimum height **60dp**, horizontal back button and localized Add Wallet title.
- Cards: **20dp** screen inset, **17dp** corners, minimum **94dp** row height, **48dp** icon tiles, **24dp** icon glyphs, **12dp** vertical gap, responsive typography and Compose scrolling.
- Choice titles/descriptions are string resources. Visual icons are local Android vector drawable resources, not external assets.
- `AddWalletPage` is protected by `HSPage(screenshotEnabled = false)`; no raw keys or restoration text shown here.

### Real choices and navigation
- **Create New Wallet** -> `CreateAccountStandardPage` via existing terms-acceptance gate. Goes directly to the actual secure create form, rather than opening a duplicate creation chooser. The existing wallet-generation and mnemonic-backup flow is not changed.
- **Import Wallet** -> `ImportWalletPage` via existing terms-acceptance gate, retaining existing recovery phrase, private key and supported backup-file restoration options.
- **Watch Only** -> `WatchAddressPage` using existing chain/account input checks. No signing or secret-key feature is invented.
- `VeldigoAddWalletRoute.resolveInput` preserves the original calling `ManageAccountsModule.Input`, including Manage/Switcher inclusive pop behavior. If opened standalone without one, it uses `AddWalletPage` as the target on success; avoids popping to an absent `CreateAccountPage`.
- No fictional hardware-wallet integration has been added.

### Regression testing
`walletkit/src/test/java/io/horizontalsystems/walletkit/modules/manageaccounts/VeldigoAddWalletRouteTest.kt`:
- Standalone screen completion dismisses `AddWalletPage`.
- Manage mode retains the account list when returning from successful creation/import/watch.
- Switcher mode preserves inclusive account-list removal.
The Android verification workflow adds the test as a blocking step.

## Verification matrix
| Requirement | Result |
|---|---|
| Android/Compose code committed to **same open draft PR #7** | Yes |
| Three actionable choices mapped to real production source modules | Verified by source inspection |
| No placeholder wallet balances, statuses or keys | Yes |
| Original data, mnemonic backup, import validation retained | Preserved; functional integration unverified |
| Screen font sizing, rounded surfaces, mint/navy palette and layout from mockup | Implemented approximately |
| CI Android compilation and regression suite | Outcome pending at last check |
| Emulator screenshots on exact density/insets | Not performed |
| Pixel-by-pixel diff against an original-resolution reference | Not performed |
| **Pixel-perfect 1:1** | **NOT verified** |
| Recovery, import, watch-only on-device end-to-end tests | Not performed |
| Lost-password/recovery data, KDF, secure storage and signing audit | Not covered by this visual task |

## Important QA follow-up
A true 1:1 claim requires a real-device render, a same-size reference image with device density, display scale, status/navigation insets, pixel-difference overlay and testing both system font scaling and dark-mode contrast. The supplied reference is a reduced multi-screen concept, so exact numeric measurements are implementation targets, not independently verified matches.

## Next screen
**Screen 24 — Create Wallet Form / Name + recovery options** in the wallet-only functional sequence; preserve the same draft PR #7. Never mark production ready until cryptographic recovery and real-device security tests pass.
