# VELDIGO Wallet — Screen 29: Privacy Settings

Date: 2026-10-30
Existing open draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference and scope

Functional wallet-only screen **29 — Privacy Settings**, following screen 27 (Settings overview) and screen 28 (Security Center). The user's saved VELDIGO UI showcase provides a shared Midnight Navy/Mint design system and a general Settings panel; there is **no verified full-resolution dedicated Privacy Settings screenshot** to measure exact spacing/density against. We therefore must not claim native pixel-perfect 1:1 until such reference and actual emulator screenshots are obtained.

Established design targets: `#071421` navy background; `#112337` cards; `#41E6C7` Mint accent; `#B5C6D7` muted copy; `#234254` subtle border; 60dp header, 16dp horizontal card inset, 20dp header card corners, 16dp list corners, accessible native switches and responsive text.

## Changes committed to the same draft PR

1. `PrivacySettingsPage.kt`: replaces Lawrence/upstream UI with native Compose privacy settings and grouped sections: **Usage data**, **Network privacy**, **Device protection** and a cautionary privacy note. The page is marked `HSPage(screenshotEnabled=false)`; it does not open while the wallet is PIN-locked.
2. Usage data switch is backed by the existing `StatsManager.uiStatsEnabledFlow` and `StatsManager.toggleUiStats`. Do not claim usage data are anonymous unless verified by a separate telemetry audit. Parent row no longer toggles and duplicates the child switch callback.
3. Tor switch is backed by the actual `ITorManager.isTorEnabled` preference and `torStatusFlow` (Connected, Connecting, Failed, Closed). Only display **Connected** when the actual Tor connection manager reports it; the persisted enabled preference is not equivalent to a live connection.
4. `TorBlock.kt` uses a real confirmation dialog. The switch stays at the persisted value until the user accepts; cancellation and system dismiss leave the setting unchanged. The user is informed that app restart and PIN/biometric re-authorization may be required.
5. `SecurityTorSettingsViewModel.kt` stores a nullable pending choice and rejects requests matching the current preference, including stale confirmation. Only explicit confirmation persists the value, stops Tor when disabling it and restarts the process to rebuild network clients. **Removed the prior forced keep-unlocked behavior**, so normal app lock evaluation is not bypassed by a network-setting restart.
6. Device protection entry opens the existing real `SecuritySettingsPage` for PIN, biometrics, auto-lock and hidden balances; no fabricated network/privacy toggles are introduced.
7. Removed the unrelated promotional VPN outbound link from Privacy Settings and the unused link/config dependency from `PrivacyViewModel`. This does **not** assert that the app includes a built-in VPN or that Tor guarantees anonymity.

## Regression tests

- `TorPreferenceChangeRulesTest` checks that unchanged Tor preferences do not require restart, changed ones require confirmation and missing/stale pending requests cannot be applied.
- `PrivacySettingsRouteTest` checks protected route metadata and confirms that serialized navigation has no mnemonic, private key, PIN or Tor credentials.
- Blocking CI includes both suites and guards against any forced unlock call in the Tor restart settings ViewModel.

## Validation and security limits

| Gate | State |
|---|---|
| Same existing draft PR #7; code/doc/tests committed | Yes |
| Actual stats opt-in and Tor state, not simulated toggle | Reviewed in source |
| Tor switch only saves on explicit confirmation | Implemented; unit test queued |
| App restart no longer requests forced unlocked state | Reviewed in source and CI guard |
| No external paid VPN promotion or fake security state | Verified by source |
| UI localized, supported icons, balanced Compose syntax | Static review pending/following |
| Latest GitHub Actions build and walletkit unit tests passed | **Not yet established** |
| Physical-device testing (Tor on/off/restart/lock, no-network, failed bootstrap) | **Not performed** |
| Network transport/proxy leak audit across all supported blockchains and DNS | **Not performed** |
| Sensitive telemetry fields, collection paths, opt-out purge review | **Not performed** |
| Font-size, narrow device, tablet, accessibility testing | **Not performed** |
| Native full-resolution screenshot/pixel overlay | **Not performed** |
| **Pixel-perfect 1:1** | **NOT verified** |

Important: Tor does not conceal the public transaction graph on-chain and does not guarantee anonymity. For real funds, verify network transport independently on each chain, Tor bootstrap failures, proxy settings, connection reuse, PIN re-lock behavior, and telemetry policy. Do not merge or produce a production release until those checks pass.

## Next functional screen

**30 — Network/Blockchain Settings**, keeping the same existing draft PR, with only actually implemented network/provider configuration and security-verified entries. Do not create chat-generated screen images.
