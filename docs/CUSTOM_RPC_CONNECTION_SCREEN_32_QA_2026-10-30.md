# VELDIGO Wallet — Screen 32: Custom RPC / Connection Validation

Date: 2026-10-30
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Head branch: `feat/onboarding-02-security-mint`

## Visual reference and implementation context

This is wallet-only functional screen **32**, refining the existing native **Add Custom RPC** page from screen 31. The user's saved VELDIGO Wallet showcase covers overall Midnight Navy/Mint branding but has **no dedicated, original-resolution screenshot of the RPC validation interface**. Reuse shared tokens: background `#071421`, surface `#112337`, Mint `#41E6C7`, muted `#B5C6D7`, border `#234254`; matching rounded inputs, 20dp content insets, bottom-anchored 54dp Mint primary action and accessible checkbox/status controls.

A true pixel-by-pixel **1:1** claim is NOT justified without an original-size reference and emulator/physical-device screenshot overlay at matched display scale and system insets.

## Implemented changes

**Actual endpoint test — `RpcConnectionProbe.kt`**
- The user explicitly taps **Test Connection**; there is no background URL request on form open or every keystroke.
- A one-shot JSON-RPC `eth_chainId` request is sent **only to user-provided HTTPS** endpoints and **only when Tor is not configured**. The probe never sends a wallet address, seed, private key or transaction.
- A dedicated plain OkHttp client has **no logging interceptors**, disables both HTTP and HTTPS redirects, enforces a 7-second connect/read and 10-second call timeout, and limits response data to 8192 characters before parsing.
- The exact JSON-RPC response must contain `jsonrpc=2.0`, `id=1`, a valid positive hexadecimal result and no `error` field. Do not display raw HTTP exceptions or server responses; these can contain endpoint API tokens or other credentials.
- Compare the reported chain ID against a **known explicit mainnet ID** for Ethereum, BSC, Polygon, Avalanche, Optimism, Base, ZkSync, Arbitrum One, Gnosis and Fantom. For new/unlisted chains (including Arc or Robinhood Chain until audited), return **Unverified: expected chain ID unavailable** rather than inventing a network identity.
- If the endpoint reports a **different chain ID**, the save action is hard-blocked. An apparently matching chain ID is **not** proof of provider honesty, account safety, correct balance, TLS path integrity or transaction validity.
- The independent HTTPS client intentionally **does not** probe WebSocket, `.onion` or when app Tor mode is enabled, to avoid silently bypassing the wallet's Tor transport, leaking a direct IP address or misrepresenting a transport it cannot authenticate. These cases are marked *Not tested*, never *Connected*.

**Save states — `AddRpcViewModel.kt` + `RpcConnectionUiRules.kt`**
- **Add** is disabled until a test result is available.
- Matching known chain ID permits save through the existing `EvmSyncSourceManager.saveSyncSource`.
- Wrong chain ID and structurally invalid URL do not permit save, even if the user tries to acknowledge the warning.
- For an endpoint whose connection was **not verified** (Tor enabled, WSS/onion, new chain, unavailable host, invalid response or unsupported credentials), saving requires an **additional explicit checkbox** explaining the risk. This preserves the existing ability to configure a valid endpoint offline or in Tor-only contexts without falsely labeling it tested.
- Changing the URL or Basic Auth invalidates prior results and acknowledgment; a revision number prevents an old asynchronous reply from verifying a newly edited endpoint.
- Basic Auth stays masked in the Compose field and local to ViewModel memory; no new settings/navigation serialization. The existing storage's encryption-at-rest and logging posture is **not audited by this change**.
- Tests are not required for viewing the list of existing RPC sources; only for adding a new custom endpoint.

**Native UI — `AddRpcScreen.kt`**
- Adds a **Connection verification** heading, explanatory copy, Test Connection action with loading indicator, status report (verified / wrong chain / invalid response / offline / Tor/transport unavailable / unknown mainnet ID), displayed numeric chain ID when available, and an accessible single-action consent checkbox on unverified outcomes.
- The prior Add action now uses `viewModel.canSave` instead of accepting any non-empty URL.
- No mocked RPC response or server state is displayed.

## Regression tests in the mandatory Android CI

- `RpcChainIdentityRulesTest`: known-chain mainnet ID mapping, hex validation and unknown-chain rejection.
- `RpcChainIdResponseTest`: strict JSON-RPC response schema, error, wrong ID, malformed payload and zero chain ID.
- `RpcConnectionUiRulesTest`: blocked invalid/wrong-chain saves, stale/no result, mandatory consent for unverified cases, successful match.
- `RpcConnectionPrivacyTest`: an interceptor configured to throw on network calls proves that Tor-enabled checks, WSS and `.onion` checks **make no HTTP call through the standalone client**.
- Workflow source guard disallows logging credentials/URL text and following redirects in the probe.

## Outstanding release and 1:1 gates

| Gate | Status |
|---|---|
| Changes committed to the same PR #7 / same branch | Yes |
| HTTPS eth_chainId request implemented without wallet secrets | Source inspected |
| Known-chain ID mismatch hard-blocks save | Implemented; unit test added |
| Tor-proxy-bypass avoidance for independent probe | Implemented; isolated test added |
| Unknown/unavailable endpoints require explicit unverified consent | Implemented; unit test added |
| Latest HEAD Android debug build + all tests green | **Not established at writing** |
| E2E test against genuine RPC chains, including wrong chain and unreachable endpoint | **Not done** |
| WebSocket and Tor RPC chain ID verification via properly configured proxy | **Not implemented; warned as unverified** |
| Tor DNS leak tests, custom HTTPS TLS/cert/redirect tests | **Not done** |
| HTTP Basic Auth provider interop, credential encryption-at-rest, proxy header safety audit | **Not done** |
| Actual chain ID coverage for all supported networks, including Arc/Robinhood/Tron | **Not complete; unknown blocked from positive "verified"** |
| Emulator screenshots at fixed device size, density, font scale and insets | **Not done** |
| Original-size reference to compute pixel-difference overlay | **Unavailable** |
| Pixel-perfect **1:1** | **NOT verified** |

Do not merge or publish a production release, represent the RPC as fully secure, or use engineering builds with real assets while chain interoperability, privacy, on-device access and broader wallet key-security tests remain incomplete.

## Next screen

**33 — Bitcoin-family synchronization source settings** (actual BTC-like node selection and wallet-only UI), continuing in the **same** open draft PR #7. Do not generate images in chat.
