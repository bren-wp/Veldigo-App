# Veldigo visual parity checklist

Reference: the approved 21-screen mint/teal Veldigo Android concept.

**Current scope:** Veldigo application identity, original vector launcher/splash marks, basic color system, app metadata and safe project import. This is NOT yet pixel-perfect.

## Visual acceptance for each supported device size

- Compare each screen against its reference at the same density and font scale.
- Audit colors, corner radii, status/navigation bars, typography, spacing, alignment, icons and interaction states.
- Test minimum supported resolution, small phones, large phones, gesture navigation, accessibility font scaling and dark theme.
- Never show mock balances as actual user funds; all balances must come from the wallet state.
- Never embed example seed phrases, private keys or real wallet addresses in screenshots or tests.

## Screens to implement/verify

Splash; Onboarding 1-3; Create/Import; Backup phrase; Verify phrase; Security setup; Biometrics; Home portfolio; Token details; Send; Transaction review; Receive; Swap; Fiat buy (provider-dependent); Markets; NFTs; History; Settings; WalletConnect sessions; About.

## Security acceptance gate

A wallet must not ship until recovery/backup, local encryption, biometric authentication, secure logging, correct chain/network/address validation, transaction simulation/confirmation, dependency provenance and key handling are independently reviewed. Protect clipboard/QR addresses against misrouting and never transmit seed phrases to backend/analytics.
