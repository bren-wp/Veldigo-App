# Veldigo v0.4.0-preview.1 — Android engineering preview

**TEST ONLY. NOT PRODUCTION-READY. DO NOT STORE OR TRANSFER REAL CRYPTO.**

### Updated Android experience
- Midnight-navy portfolio action tiles with mint icons; existing Send/Receive/Swap handlers preserved.
- Real receiving QR no longer obstructed by a central logo, improving compatibility with QR readers.
- Veldigo copy/share buttons sized for touch and narrow Android screens; actions use live blockchain URI data.
- Security center restyling and removal of upstream paywalled Defense System purchase prompts.
- Continued dark/light/system selection and Android accessibility font scaling from v0.3.
- More descriptive README and per-screen UI validation checklist.

### Release artifacts
- Android **debug/test-signed** APK and AAB for review only; no production Play signing.
- SHA256SUMS and BUILD_INFO documenting exact source commit and signing type.
- Package identifier of TEST debug builds has a development suffix and is not the production application ID.

### Mandatory pre-production gates
- Independently audit key handling, seed phrase backup/recovery, per-network signing, address validation and transactions.
- Replace upstream-owned credentials, wallet services, paywalled modules and configure approved Veldigo-controlled APIs.
- Validate visual parity for all provided screens on emulator and physical Android devices.
- Obtain protected production upload key; complete financial declarations, privacy, regional compliance and crash/ANR QA.

Original Unstoppable Wallet Android MIT attribution retained. Passing CI demonstrates buildability, not financial security.
