# VELDIGO v0.4.0-preview.2 — Android engineering preview

**TEST-ONLY PREVIEW. NOT PRODUCTION-READY. DO NOT STORE, SEND OR RECEIVE REAL FUNDS.**

This preview includes the wallet-only UI work merged from PR #7 and the Android/Kotlin CI fixes.

### Included in this test build
- Main wallet navigation: Wallet, Assets, Activity and Settings; Send/Receive actions.
- Updated onboarding, create/import/watch-only wallet routes and recovery phrase confirmation UI.
- Updated PIN and biometric setup, Security Center, privacy and network settings screens.
- Transactions list and detail screen account isolation; revised BTC-family synchronization and EVM RPC settings UI.
- Android PR verification #358 passed at source commit `ba391907c5cefc5d2e1b5683986d77c870df4a1a`; this new version builds from its own release commit.

### Attached files
- `VELDIGO-v0.4.0-preview.2-TEST.apk`: Android debug/test-signed installation package (development application ID with `.dev` suffix).
- `VELDIGO-v0.4.0-preview.2-TEST.aab`: Android debug/test-signed app bundle (not signed with the production Google Play upload key).
- `SHA256SUMS.txt`: SHA-256 hashes of both files.
- `BUILD_INFO.txt`: exact source commit and signing type.

### Critical pre-production work
- Audit mnemonic/key storage, account switching, transaction signing, fee/address handling and every supported network.
- Replace upstream-owned credentials and unused legacy integrations and independently verify transport/privacy boundaries.
- Complete real-device backup/recovery, biometric/PIN, privacy, send/receive and per-chain test transfers on test networks.
- Compare original-size device screenshots for visual parity; passing compilation does not establish pixel-perfect 1:1.
- Perform production keystore signing, Play Console compliance and crash/ANR testing before releasing to real users.

Upstream MIT attribution remains in the repository. This is a test build, not a security certification.
