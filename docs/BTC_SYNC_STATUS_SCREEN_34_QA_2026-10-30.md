# VELDIGO Wallet — Screen 34: Live Bitcoin-family Synchronization Status

Date: 2026-10-30
Existing draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Design and reference

This is the next functional wallet-only screen **34**, opened from screen 33 (**Bitcoin-family Synchronization Source**), and automatically opened after saving a supported changed source. The user's VELDIGO showcase establishes the Midnight Navy/Mint palette and general UI shape; there is **no original-resolution Bitcoin sync-status reference image** available for a pixel-calibrated comparison. A literal **pixel-perfect 1:1** claim is not yet supported.

Implementation: `#071421` background; `#112337` panels; `#41E6C7` Mint; `#B5C6D7` muted text; `#234254` border. Native Compose page uses a 60dp header, 16dp insets, 18dp status cards, actual sync state/partial progress, wallet/adapter counters, a local network icon, actionable Refresh and privacy explanations.

## Real data and scope

- `BtcSyncStatusPage.kt` has `HSPage(screenshotEnabled=false)`. The navigation argument contains only the **public blockchain model**; no account, mnemonic, wallet addresses, private key or network credentials.
- `BtcSyncStatusViewModel.kt` observes `WalletManager.activeWalletsUpdatedFlow`, `IAdapterManager.adaptersReadyFlow` and `IBalanceAdapter.balanceStateUpdatedFlow` for enabled wallets in the **active account** on the **requested blockchain**. It reacts to real wallet/adapter changes instead of polling third-party endpoints or showing a demonstration timer.
- `BtcSyncStatusRules` summarizes actual `AdapterState` values: `Synced`, `Connecting`, `Downloading`, `Syncing(progress, blocksRemained)`, `SearchingTxs(count)` and `NotSynced(error)`. It never prints the exception string, avoiding leaked endpoint credentials or account data.
- When Tor is configured but `TorStatus.Connected` has not been reported, the screen explicitly shows **Waiting for Tor** rather than claiming the wallet has an unrelated network failure.
- Zero wallets means **No enabled wallet**; active wallets with no adapters means **Preparing wallet adapter**. The screen never substitutes `Synced` for missing adapters or missing wallets.
- Real numerical progress and remaining blocks are displayed only if exactly **one** adapter supplies a plausible value; for several independent adapters the screen shows real synced/total counters, **not an invented average percentage**.
- **Adapter synchronized** is deliberately phrased as a local adapter state, not a verified blockchain/network trust guarantee. Synchronization is not proof of finality or complete transaction history correctness.
- **Refresh synchronization** calls `IAdapterManager.refreshByWallet` for real active adapters. It is unavailable if no adapters exist or configured Tor is not connected. This is not an unrelated cache deletion, secret reset, forced key regeneration or simulated rescan.
- After a successful source change in screen 33, the app returns to the Network Settings page and pushes this screen to show the new adapter status. The user can also open it before changing the source from the existing BTC settings page.
- All icons are local. No clipboard, image download, raw error text, wallet data telemetry or private key rendering is introduced.

## Testing added

- `BtcSyncStatusRulesTest`: **9** unit tests for no wallet, no adapter, Tor warming vs errors, known valid progress, invalid progress, multiple wallets without fabricated average, strict all-adapters-synced requirement, searching transactions/connecting state, and a failing adapter preventing false success.
- Android workflow requires `BtcSyncStatusRulesTest` and a static guard against mocked status, downloaded logos or exposing raw adapter exceptions.

## Outstanding verification

| Gate | State |
|---|---|
| Source and docs committed to same open draft PR #7 | Yes |
| Live adapters, active-account wallet scope and network type filtering | Source inspected |
| Actual progress and block counts only when reported | Logic implemented and JVM tests added |
| No fabricated sync completion or multi-wallet average | Rules tested in source |
| Tor gating and no raw exception text in UI | Implemented, unit test added |
| Source-change navigation into status screen | Implemented, on-device test pending |
| Native Android compile and regression suite on current HEAD | **Not established** |
| Adapter reload after changing Blockchair/Hybrid/Blockchain for BTC/BCH/LTC/DASH/eCash | **Not device-tested** |
| Tor connection/reconnect, offline mode, adapter reinitialization, error recovery | **Not device-tested** |
| UTXO/balance/history correctness, reorg and block-scan validation | **Not audited** |
| 320/360/393dp, tablets, landscape, large font and TalkBack | **Not tested** |
| Original-resolution reference, emulator screenshots and pixel overlay | **Unavailable / not performed** |
| **Pixel-perfect 1:1** | **NOT independently verified** |

Do not merge the draft PR or ship a real-funds production build until Android CI, physical-device sync regression and key/network security reviews are completed.

## Next screen

**35 — Other chain-specific connection settings or synchronized wallet state detail**, selected from the next existing repository module that actually supports user configuration. Continue in draft PR #7, without generating images in chat.
