# VELDIGO Wallet — Asset Details / Token Balance

PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Date: 2026-10-30

## Wallet-only scope
The previous reference showed an asset's market price, price chart, change percentage, period selectors and description. Those features are **explicitly out of scope** for the self-custody-only Veldigo Wallet. The screen preserves the reference's navy/mint balance card, centered chain coin header and native action affordances without the market section.

## Current source
- `VeldigoAssetDetail.kt`: token identity, actual `BalanceViewItem` amount and optional secondary amount, syncing status, chain-specific **Send** and **Receive** actions. Send is disabled for watch-only accounts.
- `TokenBalanceScreen.kt`: existing chain-specific warnings, addresses, locked balances, token transaction list, details, migration alerts and sync information. `TokenBalanceViewModel` and chain-specific operation backends remain unchanged.
- Existing signing and transfer paths must still be security-tested on testnets; source retention alone is not transfer verification.

## Deliberate removals
- No market price/portfolio chart, CoinOverviewViewModel graph fetching, price change or trading action.
- No placeholder BTC quantity, exchange price, investment graph or simulated activity.
- The old graph-specific JUnit test and CI task were removed because the graph feature was removed, **not** to conceal an error in required wallet functionality.

## Verification
| Check | Status |
|---|---|
| New Compose source committed to PR branch | Yes |
| Balance/Send/Receive integrated with existing view models | Source inspected |
| Actual chain-specific fee, signing and send/receive test | Not performed |
| Watch-only regression and account-switch race tests | Not performed |
| Real device screenshot / calibrated overlay | Not performed |
| Pixel-perfect 1:1 | **Not verified** |
| Current Android CI build | Pending; no passing claim |

Next screen in scope: **Transaction History**, followed by **Transaction Details**.
