# VELDIGO Wallet — Screen 27: Settings / Security Overview

Date: 2026-10-30
Existing draft PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Original visual reference and numbering

The saved user's `Veldigo Crypto Wallet UI Showcase.png` places **Settings** in its old **panel 23**. The current wallet-only implementation plan labels the next functional screen **27 — Settings / Security Overview**. This reference is a flattened multi-phone concept image, not an original-resolution Android screenshot with calibrated `dp`, font metrics and status/navigation bar measurements.

Observed reference: near-black navy background, compact white **Settings** heading, thin teal icons at the left of stacked settings rows, white primary text, subdued right value/chevron, hairline row separators, rounded dark panel, and actionable native wallet/security settings. The layout here uses the existing palette `#071421` / `#112337` / `#41E6C7` / `#B5C6D7` / `#234254`. Numbers are implementation targets, not verified measurements.

## Changes on original wallet app

`walletkit/.../settings/main/MainSettingsScreen.kt`:
- Replaces legacy upstream Lawrence list layout with a consistent responsive native Compose screen: 60dp-minimum title bar, 16dp horizontal insets, 16dp panel radius, 54dp-minimum tap rows, local Mint iconography, single-line subordinate current-value state when available, subtle dividers, grouped content, bottom navigation safe-area padding and a real app version footer.
- Section **Security**: Security Center; Biometric Lock with **actual** enabled/off state; Change App PIN; Recovery Phrase (routes to the existing secure account-management flow so the user first selects the correct owned wallet); Privacy.
- Section **Wallet**: Wallet Management; Backup & Restore; Connected Networks; Contacts.
- Section **Preferences**: Appearance; Currency; Language.
- Section **Information**: About, FAQ, Guides.
- All rows navigate to existing live modules. No dummy navigation, invented password, unsupported notification controls, fake finance/portfolio values or hard-coded app version.
- Explicitly removes the old **WalletConnect/dApp connections** settings entry, unfinished swap debug toggles, unused paid-subscription banners, upstream promotional footer/link and the preview promoting old WalletConnect sessions. Existing shared classes outside this UI remain for later decommission/security audit.
- Public `HsSettingCell` function remains with its existing parameter contract because the current About screen uses it; its rendering inherits the new wallet-only row styling.

`MainSettingsViewModel.kt` / `MainSettingsModule.kt`:
- Stop initializing or collecting wallet-connection session and paywall/subscription state only to render wallet-only Settings.
- Observe the real wallet backup completeness, unusual-account status, PIN setup and terms acceptance; refresh PIN/biometrics on Android lifecycle resume after the user visits Settings.
- Security status is determined from actual `IPinComponent`, including the condition that biometric lock cannot be labelled enabled when no app PIN exists.
- `VeldigoSettingsStatusRules` computes real backup/unsafe-account and PIN warnings without a fabricated safety score. New `VeldigoSettingsStatusRulesTest` required in CI.

## QA gates

| Requirement | State |
|---|---|
| Source edits and docs committed to same open/draft PR #7 | Yes |
| Only supported settings actions remain visibly reachable | Source audited |
| Real PIN, biometric, wallet, contacts, backup, network, currency, language, appearance and About destinations | Source inspected |
| Recovery phrase requires wallet selection and existing protected account actions | Source inspected; **device confirmation pending** |
| Removed WC / NFT / Swap / Markets / Buy settings entries | Source audited; CI source guard included |
| Source-backed wallet security and app version indications | Source inspected |
| All localized wallet-only labels exist | Static resource check required |
| Android debug APK, AAB, wallet/security unit tests | **Current CI pending; no success claim** |
| 320dp/narrow, 360dp, 393dp, landscape, tablet, large font, TalkBack | Not tested on physical/emulator |
| Native Android screenshots and calibrated pixel overlay | **Not performed** |
| Pixel-perfect **1:1** | **NOT independently verified** |
| Full PIN/biometric and recovery key-custody security audit | Pending |

## Next screen

**Screen 28 — Security Center detail and account protection controls** if continuing the wallet-only sequence, with emphasis on the original change-PIN, biometric, auto-lock and backup settings. Retain the existing draft PR and never issue a production release until Android CI, cryptographic security and on-device regression gates pass.
