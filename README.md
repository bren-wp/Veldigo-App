<div align="center">

# VELDIGO
### Your crypto. Your control.

**A modern Android crypto wallet built for self-custody, transparent transactions and a clean, focused experience.**

![Veldigo brand icon](branding/veldigo-icon.png)

[Android](https://github.com/bren-wp/Veldigo-App) · [Source code](https://github.com/bren-wp/Veldigo-App/tree/main) · [Releases](https://github.com/bren-wp/Veldigo-App/releases) · [Security & release status](docs/SECURITY_RELEASE_GATES.md)

</div>

---

## Take control of your crypto

Veldigo combines a distinctive dark-navy design with mint accents and an open-source, multi-chain wallet foundation. Create your own wallet, import one you already control, and explore crypto assets from a single Android app.

### Designed around what matters

| Experience | What you can do |
|---|---|
| **Your wallet, your keys** | Create or import a non-custodial wallet. Keep a recovery phrase offline and never share it. |
| **One portfolio** | View supported assets, live wallet balances, network details and transaction history. Availability varies by chain and provider. |
| **Send with clarity** | Enter an address, review the amount, selected network and network fee, then confirm through the wallet's transaction flow. |
| **Receive with confidence** | Display and share the receiving address and an actual QR code for the selected asset/network. |
| **Explore supported networks** | The underlying wallet provides multiple blockchain adapters, including Bitcoin, EVM-compatible networks and other integrated networks. |
| **Security controls** | PIN/biometric entry and local wallet recovery flows are inherited from the upstream foundation and require independent review before production distribution. |

### The Veldigo aesthetic

- **Night navy:** `#071421`
- **Card surface:** `#112337`
- **Mint:** `#41E6C7`
- **Secondary text:** `#B5C6D7`
- Rounded action tiles, clearly separated assets, restrained hierarchy and accessible targets
- Optimized for dark mode; the visual design is being implemented against the reference mockups

### Your essential actions, in one place

**Create** a wallet and back up its recovery phrase. **Import** an existing wallet using the supported secure recovery flow. **Receive** through a chain-specific address and QR code. **Send** only after inspecting the recipient, amount and fee. **Monitor** actual wallet balances and history.

### Veldigo isn't an exchange

Veldigo is intended as a non-custodial wallet. Fiat purchases, swaps, WalletConnect and other third-party integrations depend on independent providers, their terms, supported jurisdictions and appropriate API credentials. A visible UI design does **not** mean a partner service is ready for commercial use.

## Downloads and current status

The [Releases page](https://github.com/bren-wp/Veldigo-App/releases) distinguishes engineering previews from verified production releases. Preview APK/AAB artifacts may use **test signing** and are **not intended for real funds**.

**Production readiness is pending.** A successful Android compilation does not establish that a financial application is safe. Before publishing as a wallet for real assets, independent security review, transaction validation by chain, real-device QA, provider credential replacement, release signing, and regulatory/platform checks are required. See [release gates](docs/SECURITY_RELEASE_GATES.md).

## Open source & attribution

Veldigo is developed under the **Brendigo** brand. Its wallet foundation is a modified derivative of [Unstoppable Wallet Android](https://github.com/horizontalsystems/unstoppable-wallet-android), sourced from pinned upstream commit `d6661c8d0dc02d259767f58b7cd73cabb877da18` under MIT terms. The original authors retain their rights; Veldigo is independent and **not endorsed by Horizontal Systems**.

Review [the root license](LICENSE), [upstream MIT license](licenses/Unstoppable-Wallet-MIT.txt) and [third-party notices](THIRD_PARTY_NOTICES.md). Blockchain token symbols, external provider names and other trademarks are the property of their respective owners.

## Development and verification

Built with **Kotlin / Jetpack Compose**. Package name: `com.brendigo.veldigo`. Minimum Android version follows upstream (API 28 at the pinned revision).

```bash
./gradlew :app:assembleBaseDebug
./gradlew :app:bundleBaseDebug
```

For reproducible provenance, refer to the pinned import commit in [the import script](scripts/import_upstream.py). Use [the screen parity matrix](docs/REFERENCE_1TO1.md) when reviewing every Android screen. For a production build, configure project-owned credentials and secure signing through CI secrets; never commit release keys, mnemonics or private keys.

---

<div align="center"><strong>VELDIGO</strong> · Built with purpose by <strong>Brendigo</strong></div>
