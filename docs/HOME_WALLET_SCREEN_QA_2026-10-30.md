# VELDIGO Wallet — Home / Wallet

PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Date: 2026-10-30

## Source reference and scope
The original Veldigo concept board is a flattened montage. Its example money amounts are **not wallet data**, and pixel-perfect 1:1 cannot be asserted from this reference without native-density screenshots. Only the visual language (Midnight Navy, Mint, typography and spacing) is retained where appropriate to a **self-custody wallet**.

## Current source
- `VeldigoHomeComponents.kt`: Veldigo wordmark, real wallet balance, **Send / Receive** only, visible assets and a token-management header.
- `BalanceItems.kt`: uses existing `BalanceViewModel`, actual enabled token rows, account switch, token hiding/adding, backup/sync warnings, watch-only restrictions and real token navigation.
- `MainPage.kt`: wallet-only bottom controls **Wallet / Assets / Activity / Settings**, where Assets opens `ManageWalletsPage` and Activity opens `TransactionsPage`.
- Palette: `#071421` background, `#112337` card, `#41E6C7` mint, `#B5C6D7` secondary text.

## Intentional differences
- No Buy, Swap, NFT, market chart, Markets tab, fiat purchase or fabricated portfolio return.
- Send is disabled for watch-only accounts; Receive still requires a supported chain-native address.
- No mockup price, transaction ID, token holdings or coin changes are substituted for live data.
- Existing market/dApp infrastructure remains under migration review outside the visible wallet surface.

## Quality gates
| Check | Status |
|---|---|
| Source modifications committed in PR #7 | Yes |
| Live wallet data plumbing retained | Source inspected |
| Android build and unit tests for latest head | CI pending; **not verified** |
| UI interactions on emulator/physical device | Not performed |
| Correct padding, insets, accessibility, TalkBack, font scaling | Not verified |
| Reference crop calibration, screenshot and pixel-diff | Not performed |
| Pixel-perfect 1:1 | **Not verified** |

Next: Activity/Transaction History and real transaction details.
