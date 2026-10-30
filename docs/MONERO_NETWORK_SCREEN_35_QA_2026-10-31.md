# VELDIGO Wallet — Screen 35: Monero Network / Node Settings

Date: 2026-10-31
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Existing branch: `feat/onboarding-02-security-mint`

## Scope and design reference

Functional **screen 35**, the next already implemented blockchain-network configuration path after screen 34 (Bitcoin-family live synchronization). Monero's real settings module is `walletkit-chain-monero`, not the core `walletkit` source directory.

The previously supplied VELDIGO visual showcase provides Midnight Navy/Mint branding and generic wallet settings design, but **not a full-resolution, density-calibrated Monero Node Settings reference**. Consequently this change follows the existing VELDIGO palette and established screen spacing. **Pixel-perfect 1:1 parity remains unverified**, pending original-size mockup and actual Android screenshot overlay.

Colors: `#071421` background; `#112337` cards; `#41E6C7` Mint; `#B5C6D7` secondary; `#234254` border. Targets: 60dp native title/back/refresh bar, 16dp card margins, 18dp header and 16dp section corner radii, 70–73dp minimum rows, scalable typography, functional native switches and alert dialogs, scrollable existing node lists.

## Changes in the SAME draft PR #7

- `MoneroNetworkPage.kt`: native, screenshot-protected `MoneroNetworkPage : HSPage(screenshotEnabled=false)`, grouped **Connection preferences**, **Available nodes**, **Custom nodes** with per-row real ping state, Mint selected checkmark, local icons and Add custom node route. Existing manager owns the node lists and actual selection. No fake uptime metrics or fabricated Latency.
- `AddNodePage` is also protected from screen capture because users enter node authentication credentials there; the next iteration (screen 36) can bring the add-node form to complete Midnight Navy/Mint parity.
- Real **Automatic node selection** maps to `MoneroNodeManager.autoSelectEnabled`. Manual node selection is disabled while automatic selection is on; turning it off allows choosing actual built-in or custom nodes. Refresh invokes the existing ping engine rather than calling an unrelated dummy endpoint.
- Selecting a node opens a trust warning and actual **Trusted node** switch; confirmation calls the existing `MoneroNetworkViewModel.onSelectNode(node.copy(trusted=checked))`. No wallet key fields or secrets are used in navigation.
- Existing `PingState.Loading`, `PingState.Unreachable`, `PingState.Reachable` are rendered as an appropriate loading indicator, unavailable text, or actual measured latency. Reachability **does not establish server honesty**, as the explanatory privacy note warns.
- `MoneroNodeDisplayRules.safeEndpoint` hides username/password embedded in URL, URL path and query API tokens, fragments and malformed custom text. Built-in host:port names remain visible without remote image calls. Full serialized node info stays inside existing Monero connection manager; only a display-safe URI portion appears in settings.
- Delete action is available only for **non-selected custom nodes**, requires explicit confirmation, and re-reads the latest list before deletion. `MoneroNetworkViewModel.onRemoveCustomNode` independently revalidates that the node is custom and not currently selected. Deleting the current node could silently force a fallback connection; this is deliberately prevented.
- Ping refresh cancels the prior job **before** launching the next and preserves `CancellationException`, preventing obsolete results from overriding a newer node scan. Ping times must be finite and nonnegative to be shown as reachable.
- The upstream wallet-connect row-shape helpers, swipe-to-reveal and old remote resource/image affordances were removed from the rewritten screen.
- `MoneroNodeTrustBottomSheet.kt` remains available as an existing legacy helper; screen 35 uses the new native trust confirmation dialog. It is not deleted without an independent call-site/dead-code audit.

## Regression gates

- `MoneroNodeDisplayRulesTest` adds four JVM tests for ordinary built-in host:port labels, credential-bearing HTTPS URLs, onion host display, malformed string redaction, and active-node deletion protection.
- Mandatory `android-verify.yml` invokes `./gradlew :walletkit-chain-monero:testDebugUnitTest --tests ...MoneroNodeDisplayRulesTest` and a static guard against upstream wallet-connect UI, unredacted node credentials, serialized network keys, or remote images appearing on the Monero list.

## Known limitations and required QA

| Verification | Status |
|---|---|
| Actual Monero default/custom lists and node manager backing | Source inspected |
| Stored auto-select, trust, add-node navigation and ping refresh preserved | Implemented |
| Screen screenshot protection / no nav-serialized node credentials | Implemented |
| Selected node cannot be deleted without choosing another | Implemented; unit test added |
| No secret API token in displayed node URI | Redaction helper and test added |
| Latest commit Android build + all chain-module tests green | **Not yet established** |
| Actual node ping, Tor proxy behavior and automatic fastest-node handoff on device | **Not tested** |
| Monero wallet synchronization, send/receive, node trust and reorg robustness | **Not tested** |
| Existing credential storage encryption and log/privacy security audit | **Not performed** |
| Responsive 320/360/393dp, landscape, tablets, TalkBack and font scale | **Not tested** |
| Emulator screenshot / original-resolution reference overlay | **Not performed** |
| Literal **pixel-perfect 1:1** | **NOT verified** |

Security note: selection of a reachable node is not authentication of that node. A node operator can observe connection metadata and may return misleading information. Real-device security testing, Tor transport verification and source/credential storage review remain mandatory before production or real-funds use.

## Next screen

**36 — Add Monero Custom Node**, reusing the actual `AddMoneroNodeViewModel` and manager. Focus on local branded form fields, protected masked credentials and URL validation without fabricating support or sending credentials to external test services. Continue in the same draft PR #7. Do not generate images in chat.
