# VELDIGO Wallet — Screen 22: Wallet Management / Wallet List

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference
The previously saved `Veldigo Crypto Wallet UI Showcase.png`, **panel 13 ("Wallet List")**, with its adjacent **panel 14 ("Add Wallet")**, is the closest reference. Screen **22** is the next screen in the wallet-only functional sequence used in this PR, **not** panel 22 of the flattened mockup.

Observed layout: nearly black navy backdrop, top back and search controls, horizontally arranged rounded filters, compact card rows with wallet identity, selection/control affordances, and one add button. The illustrated "Main / Trading / Savings / NFTs" names, balances and categories are **mock data** and must not be hardcoded.

## Implemented in Compose
- `ManageAccountsPage.kt` implements a native 60dp-minimum header, left back action, localized Wallets title, search toggle and Mint add control; wallet-only dark palette `#071421`, raised cards `#112337`, Mint `#41E6C7`, grey-blue secondary text `#B5C6D7`.
- Card geometry: 16dp outer horizontal inset, 15dp card corners, 44dp avatar surface, 12dp inter-column gap, 13dp vertical card inset, subtle border; selected wallet indicated by a Mint edge and check.
- Filter chips are 40dp minimum height with native selection semantics. **All / Wallets / Watch-only** correspond to real accounts; fictional "Main"/"Savings"/"Trading" categories are not persisted and are deliberately omitted.
- Search uses `ManageAccountsViewModel.updateFilter`, matching real account names; results are filtered by owned/watch type, then sorted.
- Each card uses the real account name and `AccountType.detailedDescription`. No aggregate fiat or token balance is fabricated; this screen's account view model provides no verified per-wallet totals.
- Tap an account: existing active-account selection, `Mode.Switcher` automatically closes screen. Overflow (three dots): original `ManageAccountPage(Input(account.accountId))` to rename, back up or manage the wallet.
- Top `+` and empty-state Add Wallet open the existing `AddWalletPage`, preserving Create New / Import Existing / Watch Address and terms acceptance.
- Accessibility: selectable semantics on filters and wallet rows, 44dp avatar, 48dp default `IconButton` targets, multi-line type/subtitle support, visible labels and dedicated security warning description.
- `ManageAccountsPage` sets `screenshotEnabled = false`; account switching and wallet names are not exposed to ordinary screenshots/recents when the host honors this page flag.

## Backup warning correctness
- Watch-only accounts are not prompted to back up nonexistent signing keys.
- A genuine file backup qualifies as a backup for an owned account.
- Missing backup and nonstandard/not-recommended mnemonic still trigger account security warning.
- `VeldigoWalletRulesTest.kt` covers all filters, backup rules and security warnings and is required by CI.

## Deliberate constraints
The reference's portfolio balances and special account groups are fictional and absent from the source model. This implementation does **not** display them. Native screenshots and a calibrated pixel overlay remain necessary to establish 1:1, since the reference is a flattened multi-device concept without a documented density or system-inset profile.

## Validation
| Gate | State |
|---|---|
| Screen source and unit tests committed to draft PR #7 | Yes |
| Original account selection and add/import/watch routes preserved | Source reviewed |
| Auto-dismiss on switcher selection | Existing behavior retained |
| Correct watch-only and backed-up warning logic | Pure-code checks added; CI unverified |
| Real wallet values, no synthetic balances | No wallet amounts displayed |
| Emulator/phone test of switching accounts, backup, import and watch-only accounts | **Not performed** |
| 320dp phones, tablets, font scale 1.3–2.0, TalkBack and landscape | **Not performed** |
| Screenshots, pixel-diff overlay and exact 1:1 parity | **Not verified** |
| Current GitHub Android CI | Check live Actions result before merge |

## Next screen
**23 — Add Wallet (Create / Import / Watch-only choices)** from the adjacent panel in the saved reference, in the same open PR. Production release is blocked until chain and security review.
