# VELDIGO Wallet — Screen 33: Bitcoin-family Synchronization Source

Date: 2026-10-30
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Visual reference and design constraints

This is functional **screen 33**, reached from the real **Network / Blockchain Settings** screen 30. The user's saved VELDIGO Wallet showcase defines the Midnight Navy/Mint branding and Settings UI, but does **not** supply a standalone original-resolution Android Bitcoin-family sync-source screenshot. Thus the new UI follows design tokens and spacing conventions rather than claiming a verified calibrated pixel copy.

Targets: Midnight Navy `#071421`, raised surface `#112337`, Mint `#41E6C7`, subdued copy `#B5C6D7`, fine borders `#234254`. The Compose page includes a 60dp minimum back/title bar, 18dp header corners, 16dp mode list card corners, 74dp minimum tap rows, 40dp local icon tiles, source description, selected indicator, scrollable content, native keyboard/Android system back behavior, confirmation dialogs and 54dp minimum Mint Save button.

## What is implemented

- `BtcBlockchainSettingsPage.kt`: replaced legacy upstream/remote-logo styling with native wallet-only VELDIGO layout. The selected blockchain name comes from the existing manager; **no remote image URLs, invented connection status, fabricated P2P nodes or blockchain balances**.
- Only the actual restore modes returned by `BtcBlockchainManager.availableRestoreModes(blockchain.type)` are displayed: Blockchair when supported, Hybrid and Blockchain where supported. **This application does not implement arbitrary custom Bitcoin node endpoint entry on this page**.
- Existing mode titles and descriptions are sourced from `BtcRestoreMode.title` / `.description`; selected state reflects `BtcBlockchainSettingsService.restoreMode` and is shown with a Mint checkmark and RadioButton semantics.
- Screen uses `HSPage(screenshotEnabled=false)`. Public `BlockchainSettingCell(title, subtitle, checked, icon, onClick)` signature remains available for existing consumers. Custom resource URL icons fall back to the local `globe_24`; Blockchair and Hybrid icon resources are local.
- Tapping a mode only updates pending selection. **Save** becomes available only for a valid changed selection; a dialog warns that changing sync source can require rescanning or temporary delay of balances/transaction history. The existing manager still performs persistence and sends the original blockchain-scoped statistics event on a successful change.
- If Save fails, the page remains open and displays a generic error, without exposing account or provider details. No optimistic success state is claimed. When selection is unchanged or invalid, no save and no new sync event occur.
- Toolbar and system Back show a discard confirmation for unsaved changes. A normal back with no pending edits closes without modifying the stored mode.
- Text accurately describes privacy limits: a restore/sync provider can observe public-chain requests; the UI **does not claim all wallet network transport is private or anonymous**.
- The service now rejects unknown or unsupported restore mode identifiers rather than using `BtcRestoreMode.values().first` which could throw; it rechecks supported modes before persisting stale selections. The view model uses the same service-backed availability and handles errors without closing the screen.

## Regression coverage in mandatory Android CI

- `BtcRestoreSelectionRulesTest` includes 4 tests: supported mode matching; absence of Blockchair on unsupported networks; unchanged selections do not save; valid modified selections do save.
- The CI workflow requires this suite and fails if upstream network-logo fetch, mocked Bitcoin nodes, trading connections, or paid/subscription actions reappear in the BTC Settings page/view model/service.
- These are source and JVM tests, not evidence of real blockchain sync; test all supported networks and on-device rescan behavior before merging.

## Remaining verification

| Gate | State |
|---|---|
| Actual source choices and persistence retained, changes committed to PR #7 | Yes |
| Only supported per-chain restore modes selectable and saved | Implemented; tests added |
| Native wallet-only Midnight Navy/Mint UI with local resources | Implemented in source |
| User confirmation before source change and unsaved discard | Implemented |
| Latest HEAD Android compilation and all unit tests green | **Not established** |
| BTC / BCH / LTC / DASH / eCash switch and full resync on device | **Not performed** |
| Actual proxy/Tor transport usage and provider privacy/security audit | **Not performed** |
| Real wallet reorg, rescans, UTXO correctness, balances, transaction history regression | **Not performed** |
| Original-size design reference plus matching Android emulator screenshot overlay | **Unavailable / not performed** |
| **Pixel-perfect 1:1** | **NOT verified** |
| Production readiness with real assets/funds | **NOT verified** |

Do not merge the draft PR or publish a production release until the Android CI, native visual comparison, actual network sync regression and wallet security audit are complete.

## Next functional screen

**34 — Bitcoin-family restore/synchronization state and source-change behavior** (or another still-unconverted native chain settings page, based on actual repository modules), continuing in the same draft PR #7. No generated images in chat.
