# VELDIGO Wallet — Screen 31: EVM RPC Provider Settings

Date: 2026-10-30
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Source of design

The saved `Veldigo Crypto Wallet UI Showcase.png` supplies the VELDIGO wallet visual system but **does not show a standalone native-resolution EVM RPC Settings screen**. This is the next functional wallet-only **screen 31**, reached from screen 30 (Blockchain Settings). Therefore the style matches established palette/layout tokens rather than falsely claiming calibrated pixel equality.

Palette `#071421` / `#112337` / `#41E6C7` / `#B5C6D7` / `#234254`. Design targets: 60dp native header, 16dp content insets, grouped rounded raised provider cards, local Mint glyph, selected provider check, readable name and *redacted* URL, custom provider remove action, confirmation dialog, and native Add RPC form. Neither screenshots nor network/API keys are generated in chat.

## Changes

### EVM providers list (`EvmNetworkPage.kt`)
- Uses the existing `EvmNetworkViewModel` and `EvmSyncSourceManager`. **Available providers** come from `defaultSyncSources(blockchain.type)` and **Custom RPC providers** from actual saved custom endpoints. No fake networks, block height, connection latency, prices, balances, or green online state.
- The active source is exactly the existing `ViewItem.selected` state. Tap calls `onSelectSyncSource(item.syncSource)` and records the existing chain-scoped statistic; it does **not** claim success of a live blockchain connection.
- Local-only icons; remote `rememberAsyncImagePainter` requests removed from the provider UI. No WalletConnect navigation or old subscription/market design helpers needed.
- Custom provider deletion is visible only for a non-selected custom source. It requires an explicit confirmation and revalidates the current list and selected state before invoking the original manager's deletion. For an active custom source, user must select another provider first.
- `RpcEndpointDisplayRules.safeLabel` reduces the displayed URL to scheme, host and optional port. Query tokens, secret URL path segments, userinfo and fragments are **not shown**, including when a preconfigured provider uses a hidden API key. Actual source URL stays within the existing EVM network manager.
- `EvmNetworkPage` and `AddRpcPage` now use `HSPage(screenshotEnabled=false)` for wallet network metadata and entered endpoint credentials.

### Add RPC form (`AddRpcScreen.kt` / `AddRpcViewModel.kt`)
- Native navy/mint Compose form with back bar, blockchain name, masked optional Basic Auth field, URL validation notice, error feedback, source-reliability warning, bottom Add action.
- URL and Basic Auth values are in **transient ViewModel state**, not navigation arguments or `rememberSaveable`. PasswordVisualTransformation masks Basic Auth visually, and string references are cleared when setup completes or the ViewModel is disposed.
- `RpcEndpointValidation` accepts only HTTPS, WSS and HTTP for `*.onion` according to existing `OnionNodeUrl` semantics. Reject blank/oversized/malformed URLs, public HTTP, insecure ws, invalid ports, embedded URL userinfo, fragments and duplicate URI sources.
- Existing persistence through `EvmSyncSourceManager.saveSyncSource` is unchanged. This form does **not** claim encryption of credentials at rest or validate that the remote RPC actually serves the selected blockchain. Both require separate security/device tests.

## Tests and CI

- `RpcEndpointDisplayRulesTest`: hides API keys in URL path and query, removes userinfo from displayed endpoint and only permits deleting unselected custom entries.
- `RpcEndpointValidationTest`: accepted secure and onion URI schemes; invalid hosts, credential-bearing URLs, insecure protocols, malformed/oversized ports and duplicates.
- Both are required by `.github/workflows/android-verify.yml` along with source guard forbidding remote chain-logo downloads or unsanitized URL labels in `EvmNetworkPage.kt`.

## Verification matrix

| Gate | Status |
|---|---|
| Screen 31 and related Add RPC form committed in existing draft PR #7 | Yes |
| Real default/custom sources and source selection | Source inspected |
| No URL token/credential details in settings provider list | Redaction helper added; unit test queued |
| User confirms before custom RPC deletion; active source protected | UI guard added; device test pending |
| Only TLS and Tor onion custom endpoints accepted | URL policy added; unit test queued |
| Basic Auth masked and transient on form | Source inspected; storage audit pending |
| Compose fonts/spacings/palette and string keys | Implementation set; static resource validation pending |
| Android compilation + CI tests green on current HEAD | **Not yet established** |
| RPC connection to correct EVM chain, response chainId verification | **Not tested** |
| DNS/TLS/cert/redirect/Tor leakage test for custom endpoints | **Not tested** |
| Credential encryption-at-rest and logging audit | **Not performed** |
| Emulator screenshots with matching original-size art and pixel diff | **Not performed** |
| **Pixel-perfect 1:1** | **NOT verified** |
| Mainnet transaction safety, reorg, gas and malicious RPC threat model | **Not independently validated** |

Never use a development build with real funds or merge/release to production before Android CI, actual chain-id/RPC response validation, transport and key-storage review are complete.

## Next screen

**32 — Add Custom RPC / provider connection safety** may be the next standalone UI iteration for validation, error states and chain identity, or proceed to native BTC-family sync-source settings. Keep existing PR #7; do not generate chat images.
