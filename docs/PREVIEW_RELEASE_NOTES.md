# VELDIGO v0.4.0-preview.3 — Android engineering preview

**TEST ONLY — NOT PRODUCTION READY. DO NOT STORE OR TRANSFER REAL FUNDS.**

This preview incorporates the wallet-only security hardening merged in PR #8 and fixes the duplicate-tag release failure after that merge. It supersedes the previously published engineering `v0.4.0-preview.2` while keeping the old release and its assets intact.

### Changes in this preview
- Wallet-only primary navigation: Wallet, Assets, Activity and Settings; old Market/Swap tabs no longer render.
- PIN protects Assets/Activity shortcuts and *all* locked screens, including legacy Market preferences.
- Removed visible donation/rating prompts, Market UI controls, arbitrary custom-token entry points and public dApp/WalletConnect/referral links.
- New custom EVM RPC sources cannot be added from the app; existing saved network sources are preserved.
- Bounded incoming chain payment URIs and deferred Send navigation prevent unexpected duplicate screens.
- Regression coverage added for PIN unlock/cancel behavior and removed feature entry points.
- The prerelease workflow now skips an already published version instead of failing an otherwise valid merge.

### Attachments
- `VELDIGO-v0.4.0-preview.3-TEST.apk` — development-signed APK with `.dev` application ID suffix.
- `VELDIGO-v0.4.0-preview.3-TEST.aab` — development-signed AAB, **not** ready for Google Play production.
- `SHA256SUMS.txt` and `BUILD_INFO.txt` — checksums and source/signing provenance.

### Required before a real-money/Google Play release
- Independent security review of encrypted account restoration, seed handling, signing and fee/network verification.
- Real-device regression and complete testnet send/receive validation for every advertised chain.
- Replace upstream service credentials and audit legacy module/session dependencies without deleting user wallet data.
- Confirm app signing identity, Android policy/Financial features declaration, local data safety, privacy documents and crash/ANR quality.
- Do not treat green CI as evidence that wallet funds are safe.

VELDIGO is a self-custody wallet, not an exchange. No real-money production readiness is claimed.
