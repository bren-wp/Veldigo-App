# VELDIGO Wallet — Google Play and non-custodial wallet release gates (2026-10-31)

**Status: engineering verification, not an approval or legal determination.**

## Product declaration

VELDIGO Wallet is intended to be a **non-custodial software cryptocurrency wallet**, not an exchange, investment, trading, swap, buying, selling, staking or subscription platform. Real assets remain recorded on their respective blockchains; the app safeguards keys and uses chain data to reflect balances.

Google Play requires developers to complete the **Financial features** declaration under **App content**. Select **Cryptocurrency wallet** (not exchange unless exchange services are actually provided), complete all country/region-specific forms, and evaluate Google's explicit **non-custodial software wallet** declaration option where applicable. Non-custodial status is not itself proof of eligibility for every country. Keep relevant legal supporting documentation and control distribution regions where necessary.

- Official crypto wallet policy (October 2026 update): https://support.google.com/googleplay/android-developer/answer/16329703
- Official Financial services policy: https://support.google.com/googleplay/android-developer/answer/9876821

## Android requirements

- As of 31 August 2026, new phone apps and updates target **API 36+**. The repository currently configures `compileSdk=37` and `targetSdk=compileSdk`, but actual merged manifest, device behavior and Play checks must still be verified.
- Current Android foundation uses Kotlin, Jetpack Compose, Android Keystore and platform biometric mechanisms where configured. Native-first UI **does not** mean rewriting BIP39, elliptic curve cryptography or chain transaction serialization from scratch.
- External network endpoints for real balances and transaction broadcasts remain essential unless the app runs a local full node; keep keys and signing exclusively on-device. Protect endpoint/privacy handling and offer truthful stale/offline state.
- Review manifest permission minimization, app links, native library architecture, edge-to-edge, screen-reader support, gesture navigation, back behavior, crash/ANR data and privacy disclosures.
- Do not ship with inherited third-party provider API keys or claim that successful compilation certifies financial safety.
- Official target API requirements: https://support.google.com/googleplay/android-developer/answer/11926878

## Mandatory wallet-specific QA

1. Offline creation/restore with public BIP39 test vectors and existing persisted-wallet migration.
2. Correct public addresses and real balance/history from network data; no fabricated confirmation state.
3. Rejection of wrong network/address, invalid memo/tag, insufficient balance, nonce/UTXO conflicts and fee/MAX errors.
4. Local signing and transaction submission/receipt on **testnets** with controlled, valueless funds; no end-user funds in development.
5. Account separation, watch-only restrictions, PIN/biometric lock, seed screenshot protections, encrypted storage and non-destructive recovery after data/Keystore failure.
6. Disable Buy/Sell/Swap/Market/NFT/WalletConnect/ads/donations/custom tokens/custom RPC in public navigation. Preserve legacy session revocation and stored wallet data during decommission.
7. Real-device testing, Play App Signing/upload-key configuration, accurate Data safety form, privacy policy and applicable legal disclosures.

## Release prohibition

`v0.4.0-preview.2` is a **test-only** build signed by a development/test key, with `.dev` package suffix. Do not present as a Google Play production bundle. The production variant needs the correct package identity, unique version code, authorized signing credentials, passed CI, verified testnet transactions, security review and compliant console declarations.
