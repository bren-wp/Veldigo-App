# VELDIGO Wallet — Screen 30: Network / Blockchain Settings

Date: 2026-10-30
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference and scope

This is functional **screen 30 — Network / Blockchain Settings**, following Settings (27), Security Center (28) and Privacy (29). The saved user-supplied VELDIGO visual showcase includes a shared Midnight Navy/Mint branding system and Settings panel; it does **not** contain a verified separate original-resolution Android Network Settings screenshot. Thus exact pixel-by-pixel parity remains **unverified** rather than inventing reference measurements.

Use shared palette `#071421` background, `#112337` card surface, `#41E6C7` Mint highlights, `#B5C6D7` secondary copy, `#234254` borders. Implementation targets: 60dp header, 16dp horizontal panels, 14dp search corners, 16dp list corners, 42dp Mint network glyph tiles, minimum 70dp touch rows and native scrolling.

## Committed changes

**`BlockchainSettingsPage.kt`**
- Replaces upstream Lawrence appearance with native Compose Veldigo settings, shared navy/mint cards, title/back bar, local network icon assets, understated chevrons and responsive title/subtitle wrapping.
- Real sections from the original service: **Bitcoin-family** (`btcLikeChains`) and **EVM & other** (`otherChains`), with counts based on each current list. No fictitious chain balances, "online" indicators, fabricated block heights, prices or unimplemented chain provider entries.
- Searches the real configured chain name and sync-source text through `NetworkSettingsListRules`; input normalized, controls removed, bounded to 80 UTF-16 code units and surrogate-safe, with accessible search/clear labels.
- A loading indicator displays until the first actual settings-service snapshot arrives. An empty network state is not shown merely because the app is initially loading.
- Each EVM row opens existing `EvmNetworkPage(blockchain)` to select real default/custom RPC source; chain-plugin rows open their existing `page`. Existing per-chain statistics events are retained. The settings screen does **not** silently add, switch or delete network RPC sources.
- `BlockchainSettingsPage` is marked `HSPage(screenshotEnabled=false)` since network provider metadata may be sensitive. Network branding icons here are local resources; no remote image/logo fetch occurs solely for settings list display.
- Public `BlockchainSettingsBlock` is preserved for any existing consumers.

**`BlockchainSettingsViewModel.kt`**
- Continues using `BlockchainSettingsService` fed by `ChainRegistry`, `EvmBlockchainManager` and `EvmSyncSourceManager`.
- Eliminates nested `viewModelScope.launch` on every sync snapshot to prevent delayed, older coroutine jobs overwriting a fresher configured-source list.
- Explicit `loaded` state transitions only after a real service snapshot. Order for each section derives from the existing `BlockchainType.order` extension; no hard-coded enabled chains.

**Tests and CI**
- `NetworkSettingsListRulesTest.kt` verifies case-insensitive filtering by actual chain/source text, whitespace handling, input controls and bound/surrogate behavior.
- Blocking Android workflow runs that test and checks that Network Settings UI source contains no remote image-loader, mock chain, invented balance, wallet-connection or market entry.
- Existing EVM/Bitcoin/network synchronization engine remains unchanged, pending separate functional QA.

## Required validation

| Gate | State |
|---|---|
| Implemented and committed in the **same open draft PR #7** | Yes |
| Chain/source names and navigation sourced from actual managers | Source inspected |
| No remote chain-logo requests made by settings list | Source inspected |
| Empty initial state correctly treated as loading | Source inspected |
| Search test and source guard included in CI | Added; outcome pending |
| All string and drawable resources present | Static check to perform before conclusion |
| Native Android compile and all regression tests green for latest HEAD | **Not established** |
| Actual EVM default/custom RPC selection, removal, persistence and reconnection | **Not device-tested** |
| BTC-like / other native chain settings, wrong-provider error and offline fallback | **Not device-tested** |
| Endpoint credential masking, redirects, HTTPS/TLS, certificate and DNS/Tor proxy leak audit | **Not performed** |
| 320/360/393dp / tablets, font scale 1.5–2.0, TalkBack and landscape | **Not tested** |
| Native screenshot and original-size calibrated overlay | **Not performed** |
| **Pixel-perfect 1:1** | **NOT verified** |

**Security note:** The Network Settings list shows current source names, not a guaranteed live connection, transaction privacy, full Tor anonymity or trusted RPC. Custom RPC endpoints are untrusted network infrastructure and require independent chain-specific and transport security review. Do not treat this screen as a verified blockchain security audit or release with real funds while those gates are outstanding.

## Next implementation

**31 — EVM Network / RPC Provider Settings** (default provider, custom RPC, selection, endpoint validation and deletion), continuing in this same draft PR. No generated screenshots in chat. Do not merge or publish a production release before CI, on-device and signing/security review.
