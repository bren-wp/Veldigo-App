# VELDIGO Wallet — Screen 21: Transaction Details

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference reviewed
The user's existing **`Veldigo Crypto Wallet UI Showcase.png`**, bottom row, second handset, labelled **20. Transaction Details** in the old reference board, is the closest visual reference for functional **screen 21** in the new wallet-only sequence. It shows:
- White back chevron and centered Transaction Details heading on deep navy.
- One circular coin icon; transaction direction, green received amount and muted secondary fiat amount.
- Rounded dark card containing actual status, date, From, To and Network Fee.
- One mint-outline **View in Explorer** action.

The file is a flattened multi-phone **mockup**, not a native Android screen capture. Exact screen scale, font family/metrics, status and navigation bar insets cannot be inferred reliably without an original-size device reference.

## Changes committed in the existing PR
- `TransactionInfoPage.kt`: custom, responsive 60dp-minimum top bar and dark navy full-screen background, 16dp horizontal content inset, 20dp card radius, subtle navy card border, separators and mint-outline explorer action.
- Simple single-asset sent/received transfers: coin icon and actual amount moved into a central summary. The original amount cell is removed from its lower section **only** when one eligible amount exists, preventing duplicate display. Multi-asset, NFT-history, approval and contract-transaction cells remain fully represented, without fictitious wallet values.
- Status, timestamps, native chain-specific fee estimates/paid fees, blockchain addresses, transaction hash, address copy, contact actions, explorer, raw transaction and fee replacement flow retain the existing view-item factory and handlers. No hard-coded mockup amounts, addresses, statuses or fees.
- Removed token-amount tap to legacy `CoinPage` market-overview route; transfer values are read-only in wallet-only mode.
- `TransactionInfoPage : HSPage(screenshotEnabled = false)` keeps transaction detail and potential transaction secret keys off screenshots and recent-app thumbnails, including during normal runtime. Screenshot QA must never disable this protection in a real wallet.
- `TransactionInfoAccountAccess.kt` requires the active account ID to match the record's originating account before displaying details. During account initialization, details do not render. On definite account mismatch the screen closes.
- `TransactionInfoAccountAccessTest.kt` checks matching/mismatched/empty and missing account IDs; CI workflow requires this test.

## Test and security gates
| Criterion | Status |
|---|---|
| Source edits and tests committed to existing draft PR | Yes |
| Account guard rejects a cross-wallet record by code | Implemented, unit test queued |
| Genuine transaction data retained from view-model/factory | Confirmed in source only |
| On-device real send/receive status, address and fees | Not verified |
| Multiple accounts switched during live transaction updates | Device regression pending |
| Pending, failed, double-spend, speed-up/cancel, on-chain fee states | Device regression pending |
| Error, loading, no-network and account deletion | Device regression pending |
| 320dp narrow device, tablet, display density, font scale ≥ 1.3 | Not tested |
| Android emulator/physical screenshot and calibrated overlay | Not performed |
| Actual native-pixel diff versus supplied reference | Not possible from flattened concept alone |
| Pixel-perfect 1:1 | **NOT verified** |
| Android CI for final commit | Pending, must inspect latest run |

## Security follow-ups
1. The pre-existing global `TransactionInfoPayload.record` is a one-shot in-memory transfer; it should be replaced after safe record-resolution and lifecycle testing. The account guard prevents cross-account display but is **not** a complete persistence/concurrency audit.
2. Confirm `FLAG_SECURE` is in effect before any sensitive pixels are presented and throughout back navigation and app backgrounding.
3. Ensure externally sourced blockchain-explorer URLs are trusted, chain-appropriate and sanitized before opening; malicious block explorers must not be whitelisted by accident.
4. Verify refresh and account switching cannot deliver stale asynchronous amounts/fees into the active account's screen.
5. Do not publish production APK/AAB or claim safe real-funds use without chain-specific signing, recovery and transaction security review.

## Next screen
**22 — Wallet Management** in the wallet-only functional sequence. Continue in this same PR #7; do not merge before mandatory QA/security gates.
