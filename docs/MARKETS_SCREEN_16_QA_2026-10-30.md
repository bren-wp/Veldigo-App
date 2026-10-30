# Veldigo Wallet — screen 16: Markets

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Exact original design reference

User-supplied `Veldigo Crypto Wallet UI Mockup.png`, 1536 × 1024 composite; third row, second phone, labelled **16. Markets**, approximately x=220–440 and y=715–1024 in the montage. A crop was inspected **privately**, without generating or posting any new image in chat.

Visible layout:
- Nearly black background, status row, centered white `Markets` heading.
- Full-width rounded dark input: search icon, `Search coins...` placeholder, right-side dropdown/expand arrow.
- Row of three pill filters: **All** highlighted mint, **Top Gainers** and **Top Losers** dark.
- A compact market table header: `#`, `Coins`, `Price`, `24h%`.
- Rows with integer market-cap rank, round icon, token code, right-aligned price and positive mint/negative red change.
- Artwork displays BTC, ETH, SOL, BNB and XRP with **illustrative** prices/percentages; those values are NOT live and must NOT be used as a data source.
- Existing five-item Home/Markets/Swap/History/Settings bottom navigation remains controlled by `MainPage.kt`.

## Implementation in the EXISTING PR #7

- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/market/VeldigoMarketsContent.kt`:
  - Original Compose midnight/mint layout, centered header, integrated search, three pill tabs, 4-column compact ranked list and native scroll.
  - Actual price, percentage and market-cap ranks come from `MarketKitWrapper.topCoinsMarketInfosSingle`, via the existing `MarketTopCoinsViewModel` and `MarketItem`.
  - Default **All** sorts by actual market capitalization. **Top Gainers** and **Top Losers** invoke the existing ViewModel sorting so the same up-to-date data set is used.
  - The text search filters the currently loaded top-100 coins by code/name; the trailing search action and empty-state link open the existing `MarketSearchPage` for full-catalog search.
  - Tapping a row opens the existing `CoinPage` using that coin's actual UID, not a hardcoded Bitcoin detail.
  - A small watchlist icon in the top right toggles the original `MarketFavoritesScreen`, retaining watchlist access that was previously shown as an extra market tab.
  - Genuine coin artwork is loaded from existing MarketKit URLs with the established placeholder/error handling; icons are not generated.
  - Loading, network error and empty search states use real data and provide retry/full search.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/market/MarketScreen.kt`: routes actual Markets bottom-navigation tab to the new component; keeps the underlying MarketKit, other submodules and the existing MainPage navigation untouched.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/market/topcoins/MarketTopCoinsViewModel.kt`:
  - Exposes the market-item list in state (real rank, price, diff) without invented state.
  - Fixes ranking/sorting to **sort all the fetched 500 market results before selecting top 100**; otherwise Top Gainers/Losers would incorrectly sort only the first 100 market-cap coins.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/market/MarketModule.kt`: adds `priceAvailable` flag to `MarketItem`; if a provider returns null price, the new screen shows an unavailable marker instead of misleading `0.00`.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable labels and empty-state message.
- `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/market/VeldigoMarketRulesTest.kt`: query matching, real sign/percentage formatting and the three intended sorting fields.
- `.github/workflows/android-verify.yml`: new required walletkit unit-test step.

## Previous failing CI fixed on same branch

The prior run https://github.com/bren-wp/Veldigo-Wallet/actions/runs/41104961662 successfully built the debug APK, but **failed compiling walletkit unit tests**, because `VeldigoFiatPurchaseGateTest.kt` used `kotlinx.serialization.json.Json`, absent from the walletkit unit-test classpath. Removed this import and unsupported serialization assertion, retaining the test of absent provider and amount validation. Latest CI must still confirm the fix.

## Intentional differences / caveats

1. Sample screenshot rates `$66,342.10`, `$3,412.20`, etc. are fictional/inapplicable to current time; the app displays only actual MarketKit quotes.
2. Relative movement is the existing configured 1D price-change rule from MarketKit. Note that some users configure 1D from UTC midnight rather than sliding 24h; the label `24h%` is the concept wording and should be changed to `1D%` if the existing price-change settings require strict precision.
3. Watchlist access in a header icon is an additional affordance not present in the mockup to retain previous functionality.
4. Search within this list matches loaded top 100; separate full market search is accessible from the search control.
5. The input reference is a reduced composite, not native device rasterization. Actual 1:1 typography, status bar, density, touch target and alignment need a screenshot comparison at matched emulator resolution.

## Acceptance gates

| Gate | Status at source authoring |
|---|---|
| Correct source panel 16 inspected | YES |
| New UI committed to original PR #7 | YES |
| Coin rank/price/1D change derived from MarketKit | YES, source reviewed |
| Gainers/Losers sorting based on fetched real data | YES, source reviewed |
| Full-catalog search and watchlist remain accessible | YES, source reviewed |
| Example BTC/ETH/SOL/BNB/XRP prices not hardcoded | YES, source reviewed |
| Missing price not displayed as real zero | YES, source reviewed |
| CI unit test source committed and workflow configured | YES |
| Latest Android Gradle build / tests | **PENDING** |
| Network error, slow provider, offline and search on real device | NOT RUN |
| Pixel-by-pixel screenshot overlay at matching resolution | NOT RUN |
| Pixel-perfect 1:1 VERIFIED | **NO** |
| Ready to merge/production release | **NO** |

## Next source panel

**17 — Search / Watchlist / Market detail**, identify against actual reference before editing. Retain authentic data rather than reusing illustrative artwork values.
