# VELDIGO Wallet — self-custody-only migration

Status: **in progress** on draft PR #7. Not certified for use with real funds.

## Target surface
- Create / import / back up / restore wallet.
- Actual owned balances, asset listing and token management.
- Receive real chain-specific addresses and payment QR.
- Send only after address, balance, network and fee verification.
- Actual transaction history and transaction detail.
- PIN, biometrics, backup, recovery and settings.
- Bottom navigation: Wallet / Assets / Activity / Settings.

## Implemented in this pass
- Main-navigation collection offers Balance and Settings only; two explicit bottom-bar actions open existing asset management and activity history screens.
- Home removes Buy, Swap, NFTs. Real Send and Receive callbacks are preserved.
- Legacy navigation enums remain to avoid breaking persisted settings and other code references; they are not offered as tabs.
- Newly added PR-specific NFT gallery and metadata lookup, fiat Buy UI, market UI and swap form were removed with their screen-only tests and QA files.
- MarketScreen and SwapPage use their stable original source instead of custom concept art. Their upstream support infrastructure has not been removed.
- Root navigation no longer presents Market or WalletConnect deep-link pages; the MainViewModel accepts only scheme-admitted chain-native payment requests for later chain validation.
- Asset details no longer fetch market charts or market descriptions. Live balances, sync alerts, Send/Receive and the existing transaction list remain.
- Dark Veldigo palette aligned to #071421 / #112337 / #41E6C7 / #B5C6D7.

## Still open — do NOT claim decommission complete
- Audit every upstream feature entry point in Settings, scanner, notification, widgets, intents, background services and database migrations.
- WalletConnect session deactivation / disconnection requires a deliberate migration: avoid leaving active approvals and do not silently prevent existing users from revoking connections.
- Review chain-specific deep-link plugins, smart-contract approval surfaces, external RPC settings and all payment request variants.
- Separate trading-only code from shared transaction, nonce, fee and signature logic before deleting any shared dependencies.
- Complete dependency audit and security review across :app, :walletkit, :walletkit-chain-* and dapp modules; remove unused code only with passing regression coverage.
- Finish deterministic instrumentation tests, native screenshots, image overlays, accessibility, rotation, font scaling and device QA for each retained screen.
- All APK/AAB outputs are **engineering previews only**. Never test with valuable production funds.

## Verification
Code changes and regression tests were committed. Android GitHub Actions was running at the time of this note; no passing result is asserted here. Pixel-perfect 1:1 was **not measured**, since no emulator screenshot/overlay was performed in this work session.

Current priority after the entry-point cleanup: **Transaction History** and transaction detail, with no fabricated activity records.
