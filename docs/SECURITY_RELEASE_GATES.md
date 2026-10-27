# Veldigo release and security gates

**Status: NOT production-ready (v0.3 preview; security release gates remain blocked).** This project handles irreversible cryptocurrency transfers. A passing compile is not an independent security assessment.

## Release blocking items

- [ ] Independent full-scope security audit of wallets, seed/private-key creation, entropy, backup and restore.
- [ ] Source and dependency provenance review, SBOM, lockfile/checksum verification, vulnerable dependency scan.
- [ ] Transaction signing and address-validation test vectors for **each enabled chain**, chain IDs and network mismatch handling.
- [ ] Cross-check displayed fee, recipient, amount, token contract, destination network and signed transaction bytes.
- [ ] Malicious WalletConnect sessions, misleading signatures and clipboard replacement are defended and documented.
- [ ] Test recovery, biometrics, process death, migrations, device restore, screenshots/secure flags and cleartext policies.
- [ ] Replace all upstream-owned tokens/API credentials, support URLs, analytics and exchange/fiat partner integrations with Veldigo-controlled configurations, or disable services safely.
- [ ] Validate privacy policy, financial-feature declaration, region-specific rules and current Play Console requirements.
- [ ] Verify localization, accessibility, screen reader labels, contrast, text scaling, orientation/device sizes and QR scans.
- [ ] Complete device/emulator screenshots for all 21 references; inspect visual diffs instead of declaring “1:1” from source edits.
- [ ] Provision an exclusive long-term Android **upload keystore** in secured GitHub secrets, separate from test.keystore.
- [ ] Verify app integrity / artifact SHA-256s; sign final AAB/APK with documented keys and archive signing identity securely.
- [ ] Confirm successful internal testing, closed-testing crash/ANR telemetry review (without leaking sensitive data), and rollout approval.

## Preview builds

GitHub engineering previews **v0.2.0-preview.1** and **v0.3.0-preview.1** (when built) include test-signed Android APK and AAB. Signing uses the upstream publicly available test keystore and is **not trusted** for production releases. Never submit this AAB to a production Play app or use these builds with real assets.

## Production signing

Do **not** commit passwords, .jks files or Play Console upload private keys.

The signed production pipeline must be implemented using repository/environment secrets and a dedicated manual approval gate. Suggested repository secret identifiers:

- `VELDIGO_UPLOAD_KEYSTORE_BASE64`
- `VELDIGO_UPLOAD_STORE_PASSWORD`
- `VELDIGO_UPLOAD_KEY_ALIAS`
- `VELDIGO_UPLOAD_KEY_PASSWORD`

Secrets should be provisioned in GitHub's protected release environment. The signing fingerprint, versionCode, versionName, expected package ID and Play App Signing configuration need independent verification.

**Do not represent the engineering preview as a certified financial or production-ready wallet.**
