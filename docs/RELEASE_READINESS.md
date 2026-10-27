# Veldigo — production readiness gates

**Decision: BLOCKED.** Passing Android compilation and publishing test artifacts does not demonstrate safety for storing real cryptocurrency.

| Gate | Expected evidence | Current status |
|---|---|---|
| Android builds | APK/AAB output hashes, build revision | CI pending for v0.3 |
| Visual parity | 21 comparable device screenshots + diff report | Not complete |
| Recovery & storage | Independent review of RNG, seed, encryption, Android Keystore and migrations | Not complete |
| Blockchain sending | Per-chain address/network/fee/signature test vectors | Not complete |
| WalletConnect | Malicious dApp, approval and misleading-signature review | Not complete |
| Provider access | Licensed Veldigo-controlled API keys and compatible services | Not complete |
| Dependency & supply chain | SBOM, pinned provenance, advisory scan | Not complete |
| Upload signing | Dedicated privately stored Veldigo key and Play App Signing | Not configured |
| Google Play | Financial features, Data safety, privacy, legal/compliance review | Not complete |
| Real devices | Small phones, display scaling, biometric/recovery, crashes and ANRs | Not complete |

Production-signing passwords, private keys, mnemonics, actual wallet addresses and Keystore files must never be added to the repository, test snapshots or GitHub Actions logs.

The bundled upstream test keystore is for engineering builds only. APK/AAB files named TEST are **not production builds**.
