# Veldigo Wallet

**Your crypto. Your control.**

An independent, MIT-licensed Android non-custodial wallet project under the **Veldigo** brand (by **Brendigo**).

## Development status

This repository contains a **work-in-progress foundation**, not an audited or production-ready cryptocurrency wallet. The source-import workflow fetches a **pinned commit** of [Unstoppable Wallet Android](https://github.com/horizontalsystems/unstoppable-wallet-android) and applies the first layer of Veldigo branding. Android UI alignment with the 21 provided mockups is a separate, unfinished task. No cryptographic implementation is being claimed as original or independently reviewed.

## Design identity

- Brand: **VELDIGO**
- Tagline: **Your crypto. Your control.**
- Application ID: `com.brendigo.veldigo`
- Main background: `#071421`
- Surface: `#112337`
- Accent: `#41E6C7`
- Technology: Kotlin / Jetpack Compose
- Android minimum: API 28 (follows pinned upstream)

## Provenance and licenses

This is a derivative of **Unstoppable Wallet Android** by Horizontal Systems. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and [the preserved upstream license](licenses/Unstoppable-Wallet-MIT.txt). The root LICENSE covers original Veldigo contributions. Upstream product trademarks and service names remain owned by their respective holders.

## Development

Import source from GitHub Actions workflow **Bootstrap Veldigo wallet source** on the feature branch. Once imported:

```bash
./gradlew :app:assembleBaseDebug
```

For real-funds usage, audit key storage, backup/recovery, chain integrations, deep links, transaction confirmation, WalletConnect, dependencies, RPC endpoints, swap/buy providers, and app permissions. Third-party service API credentials and app-link domains must be replaced/configured for Veldigo. No funds should be stored in an unreviewed build.

**Release status:** No production-signed APK/AAB, security audit, or pixel-perfect validation has been completed.
