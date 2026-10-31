# VELDIGO Wallet — incremental wallet-only and security audit (2026-10-31)

> Engineering audit work in progress. Not a security certification. No funds should be stored in the current preview.

## Verified baseline

- Base branch: `main`, source `c035c9ec1314d4f9b00dcbfdf36861f426ab7120`.
- Release `v0.4.0-preview.2` uses **debug/test signing** and a `.dev` package suffix. It must not be presented as a production wallet.
- PR #7 was already merged. This follow-up is a separate PR with no database schema change and no wallet-key migration.

## Code changes in this PR

1. `MainPage` Assets and Activity shortcuts now invoke `MainViewModel.openProtectedPage`, which delegates to the existing `LockGate.requireUnlocked`. Locked access is deferred until successful PIN unlock; a cancelled request does not navigate.
2. Main-screen rate-app prompts and donation overlays, including triggering subscriptions, were removed. Existing stored transactions, chain adapters, and wallet data remain unchanged.
3. Unused main-screen manager constructor dependencies were removed without deleting shared managers that may still be consumed elsewhere.
4. Add an explicit lock-gate regression test and run it in the PR's Android verification workflow. The same workflow checks that both shortcuts are protected and that the promotional overlays are absent.
5. Run external Send navigation through a Compose `LaunchedEffect` rather than mutating the navigation stack during composition; this prevents repeated page pushes caused by recomposition.
6. Restrict chain-native payment deep-link admission to a bounded length and reject control characters before invoking the chain URI parser; add regression tests. Actual address, network and fee validation remain required in send adapters.
7. Require full `:walletkit:testDebugUnitTest` and `:app:testBaseDebugUnitTest` suites in the test-only preview publication workflow; any test failure blocks artifact publication.
8. Replace the legacy Market read-only PIN exemption with unconditional wallet locking across all tabs, including persisted legacy Markets preferences; tests cover cold start, deferred/cancelled actions and throwing/cancelled navigation callbacks.
9. Stop Android from exporting legacy WalletConnect, referral and Market widget entry points; keep necessary stored legacy session state until a safe revocation/migration path is verified.
10. Remove the arbitrary Add Token action from Assets (keep existing enabled tokens); remove old Market/price settings from Appearance.
11. Disable creating new custom EVM RPC endpoints, including the restored legacy AddRpcPage back-stack route, while preserving selection/deletion of existing stored sources. New chain transaction providers still need independent verification.
12. Main navigation no longer renders Market or Swap; legacy persisted enum values safely fall back to Wallet.
13. Add PR CI guards to prevent reinstating those entry points. Document the current Google Play non-custodial crypto wallet declaration and API target requirements in `docs/GOOGLE_PLAY_WALLET_COMPLIANCE_2026.md`.

## Security findings requiring further work (NOT FIXED here)

| Risk | Evidence in source | Required handling |
|---|---|---|
| **Critical — production use not verified** | `docs/CHAIN_SUPPORT_MATRIX.md` lists chain adapters but no complete, independent send/receive certification | Prove derivation, recovery, chain validation, UTXO/nonce/gas, fees/MAX, broadcast, status and rollback per network on testnets |
| **High — upstream service credentials embedded in build configs** | `app/build.gradle.kts` supplies provider tokens and API keys as BuildConfig values in debug and release | Inventory all dependencies, rotate provider-owned keys, implement controlled credential provisioning; do not disable RPCs blindly |
| **High — possible silent unreadable account state** | `DatabaseConverters.decryptSecretString/decryptSecretList` catches decryption failures and returns null; `AccountsStorage.allAccounts` catches record mapping failures and drops the row from the returned list | Design non-destructive encrypted-data recovery/error surface and migrations; regression-test corruption, wrong keystore, updates and recovery before changing this path |
| **High — upstream wallet-only feature retirement incomplete** | `settings.gradle.kts` still includes `dapp-core`, `dapp-wallet-connect` and subscription modules; `AppDatabase` still holds WalletConnect, NFT and swap tables | Audit live entry points and dependency graph, revoke legacy approvals/sessions safely, preserve account data and signing dependencies before removing modules |
| **High — provider and chain quality unknown** | Blockchain adapters exist but network-specific transaction acceptance, balances and receipt verification are not documented as tested | Keep the support matrix conservative and ensure the UI does not advertise unverified functionality as production safe |
| **Medium — app documentation and release compliance** | New financial-feature declaration and local privacy disclosures required for Google Play | Review country-specific non-custodial declaration, Data safety, privacy policy, target API and test-signing before release |

## Acceptance and release gates

- Required PR CI: Android debug build; recovery, biometric/PIN, Send/Receive UI, activity isolation, LockGate and chain checks that are configured in `.github/workflows/android-verify.yml`.
- No schema, derivation-path, stored-account, signing-key or migration changes are authorized in this increment.
- Review real CI logs; a green compile is not equivalent to safe blockchain transfers.
- Test real network interactions only on funded testnets or controlled local fixtures. Never use end-user funds.
- No new APK/AAB release until required tests and production prerequisites are met; never label a debug/test bundle Google Play ready.
