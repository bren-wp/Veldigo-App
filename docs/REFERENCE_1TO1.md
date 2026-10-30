# Veldigo Android reference matrix

Primary reference: the user's navy/mint four-screen panel. Secondary reference: the user's larger multi-screen board; **use its functional screen inventory, not its purple brand color**.

## Design tokens

| Token | Value | Intended usage |
|---|---|---|
| Background | `#071421` | Global dark base |
| Raised surface | `#112337` | Cards, inputs and panels |
| Elevated/gradient stop | `#10283A` | Action tile / portfolio |
| Hairline border | `#234556` | Containers |
| Accent | `#41E6C7` | Positive actions and selected state |
| Secondary text | `#B5C6D7` | Supporting copy |
| Main text | `#FFFFFF` | Headings and balances |
| Large radius | `20dp` | Cards |
| Action radius | `17dp` | Tiles |
| Action target | `62dp` | Wallet action icons |
| Horizontal screen inset | `16–24dp` | Device-dependent |

## Screen parity and function matrix

- [x] App brand label, palette and original icons — **implementation**, not visual QA
- [x] Intro / onboarding layout — **implementation**, not visual QA
- [x] Empty wallet create/import/watch actions — **wired to real routes**, not visual QA
- [x] Portfolio total balance surface — **wired to actual wallet total**, not visual QA
- [x] Portfolio action tile style — **real handlers retained**, not visual QA
- [x] Receive address card / real QR — **restyled**, not visual QA
- [x] Send form background — **restyled**, transaction flow retained
- [x] Create-wallet choice description — **restyled**, key flows retained
- [ ] Recovery phrase backup / verification — **visual parity and secure test pending**
- [ ] PIN, biometric unlock — **visual parity and device QA pending**
- [x] Asset details — **balance/Send/Receive retained; market charts removed**, native visual QA pending
- [x] Activity / transaction history — **real history and mint/navy style implemented**, visual QA pending
- [x] Transaction Details — **live single-asset summary, account isolation and chain-specific rows**, screenshot/pixel diff pending
- [ ] Transaction Details — **native screenshots, full error/state coverage and pixel parity are NOT verified**
- [x] Wallet Management — **real account list, name filter, account switch, add route and backup warnings implemented**, native pixel parity pending
- [ ] Wallet Management — **device screenshots, screen-density calibration, accessibility and account-switch regression NOT verified**
- [x] Add Wallet (screen 23) — **three real Create / Import / Watch-only routes and branded cards implemented**, screen QA documented
- [ ] Add Wallet (screen 23) — **native screenshot/density parity, hardware and recovery flow tests NOT verified**
- [x] Create Wallet Form (screen 24) — **real account creation, title/name, advanced BIP39 option, passphrase confirmation and existing backup route retained**, native visual QA pending
- [ ] Create Wallet Form (screen 24) — **no exact-size standalone reference, emulator/physical pixel diff and recovery regression pending**
- [x] Recovery Phrase Backup / Verification (screen 25) — **real saved words, responsive 2-column phone grid, Mint navigation and full ordered check retained**
- [ ] Recovery Phrase Backup / Verification (screen 25) — **native pixel diff, phrase restoration vectors, screenshot blocking and device accessibility remain unverified**
- [x] Security Setup / PIN / Biometrics (screen 26) — **navy/mint chooser, native PIN keypad, real security routes and OS biometric prompt preserved**, device QA pending
- [ ] Security Setup / PIN / Biometrics (screen 26) — **unverified native screenshot diff, biometric device paths, lockout/privacy QA and cryptographic security review**
- [x] Settings / Security Overview (screen 27) — **native navy/mint grouped wallet-only settings, live PIN/biometric statuses and real routes implemented**, source QA recorded
- [ ] Settings / Security Overview (screen 27) — **Android screenshots, font/density overlay and on-device navigation/accessibility verification NOT performed**
- [x] Security Center (screen 28) — **real PIN/biometric/duress/auth controls, auto-lock, privacy toggles and source-backed status implemented**, QA documentation committed
- [ ] Security Center (screen 28) — **native screenshot overlays, OS biometric/PIN device checks, authorization regression and 1:1 pixel parity NOT verified**
- [x] Privacy Settings (screen 29) — **real usage-data opt-in, actual Tor status, confirmed restart, no lock bypass, security entry and wallet-only navy/mint layout**, source QA recorded
- [ ] Privacy Settings (screen 29) — **native screenshot/pixel overlay, Tor DNS/proxy leak and on-device relock/accessibility checks NOT performed**
- [x] Network / Blockchain Settings (screen 30) — **real configured chain groups, source-backed RPC labels, search, local icons and EVM/chain navigation implemented**, QA documented
- [ ] Network / Blockchain Settings (screen 30) — **native screenshot/pixel diff, real provider switch/reconnect, offline error and RPC/Tor leak audit NOT performed**
- [x] EVM Network / RPC Providers (screen 31) — **real default/custom sources, selected provider, secret-redacted URL, confirmed custom-source removal and branded Add RPC form implemented**
- [ ] EVM Network / RPC Providers (screen 31) — **native screenshot/pixel diff, chain-ID check, endpoint transport security and device integration NOT verified**
- [x] Custom RPC Connection Validation (screen 32) — **read-only HTTPS eth_chainId probe, wrong-chain block, real unverified-consent state and Tor-aware no-probe rule implemented**, QA documented
- [ ] Custom RPC Connection Validation (screen 32) — **real device network tests, complete chain coverage, WSS/onion transport identity, proxy/TLS audit and exact 1:1 screenshot parity NOT verified**
- [ ] Legacy swaps/buy/markets/NFT/dApp/WalletConnect — **out of scope, backend and historical-session decommission audit pending**
- [ ] Security settings, theme, about and help — **visual parity/content audit pending**

## Screenshot validation protocol

1. Build the same flavor/density on an Android emulator or device as the reference (status/nav insets matter).
2. Record screen dimensions and render at 100% zoom.
3. Compare geometry, text baselines, inter-element spacing, glyphs, rounding, icon bounds, chart sizes and shadows with an image-diff overlay.
4. Validate larger font sizes and narrow phones without clipping or overlaps.
5. For money amounts, fees, recovery phrases and QR addresses, validate underlying **functional data** separately: a reference screenshot never authorizes fake values.
6. Repeat comparisons for enabled/disabled/loading/error/confirmation and translated states.

No full wallet-only screen pixel comparison or exhaustive device pass has been completed. Transaction Details now follows the original `Veldigo Crypto Wallet UI Showcase.png` mockup (legacy panel 20, functional screen 21), but the flattened board is not calibrated native-resolution screenshot evidence. v0.4 additionally styles the wallet action tile row, replaces receiving actions with full-width CTA buttons and removes the QR center overlay for scan reliability. Security center now hides unconfigured upstream premium actions. These are source-code changes, **not screenshot-based evidence of 1:1 completion**. v0.3 includes branded About/settings, live-data activity card restyling and system text-scale compliance. Only screenshots produced by an actual running Android build may be used to mark visual QA complete.
