# Screen: Transaction History / Activity — QA record

Status: **implemented style alignment; verification pending**, PR #7.

## Source and functional data
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/transactions/TransactionsScreen.kt`
- `TransactionsPage` resolves `TransactionsViewModel` from the existing EntryPage scope.
- History uses real `uiState.transactions` grouped by date; it does not create illustration transactions.
- Existing transaction filter, lazy paging, sync state, empty state, action to open `TransactionInfoPage` and amount privacy flags remain intact.
- The Activity bottom control opens this existing page, rather than a fake transaction feed.

## Changes
- List background, sticky group headers, loading/empty surface and cards now follow the Veldigo Midnight Navy background and Card Surface palette.
- Trading, NFTs and exchange graphs are not part of the Activity tab.
- Existing on-chain transaction details and historical records are retained.

## Validation matrix
| Check | Status |
|---|---|
| Compose source updates on PR branch | Implemented |
| Actual amounts and transaction IDs originate from wallet services | Existing code path inspected |
| Filter, pagination and details regression checks | Not run on emulator |
| No-network/RPC error and account-switch handling | Not verified |
| On-device accessibility, talkback and large font | Not verified |
| Screenshot overlay, density calibration, image-diff | Not performed |
| Pixel-perfect 1:1 match | **Not verified** |
| Real-chain incoming/outgoing transactions | **Not verified** |

**Security note:** The legacy detail navigation uses `TransactionInfoPayload.record` as mutable shared state; account-switch and asynchronous navigation races require explicit regression testing before production.
