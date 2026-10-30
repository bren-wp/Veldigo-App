# Veldigo — Screen 11: Asset Detail / Token Balance

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Approved source

`Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024, a low-resolution four/seven-handset composite).
Screen **11. Asset Detail** is in the second row, fourth handset, approximately `x=650..865` and `y=387..710`.
The reference was examined locally. It is an artwork panel, **not** a device screenshot suitable for measuring native pixel parity.

**Visible structure:**
- Pure dark background, system status icons, top back chevron, center coin icon and `Bitcoin (BTC)` title.
- Price centered, white, about `$66,342.10` in the *reference only*; mint percentage with a 24h label.
- Mint line/area price chart on black; dim grid lines; range selector: 1D, 1W, 1M, 3M, 1Y, ALL.
- Dark teal rounded `Your Balance` card showing amount, second fiat value, and side-by-side mint `Send` / `Receive` buttons.
- `About Bitcoin` header and brief description in muted text.
- No image bitmaps are embedded as data/chart, and there is no dummy BTC amount in the application.

## Actual code changes (in the SAME PR #7)
1. `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/token/VeldigoAssetDetail.kt`
   - Native Compose back/title header, centered price, mint percentage, Canvas line-and-fill chart with genuine historical points.
   - Existing `CoinOverviewModule.Factory` and `ChartViewModel` supply real MarketKit chart data, selectable periods and prices. Chart periods come from the backend-supported intervals, rather than showing working tabs without data.
   - Actual market information is read from `CoinOverviewViewModel.overviewLiveData` only when present. No hardcoded description or prices.
   - If a custom/unknown asset has no market data: show `Market price unavailable` / `Live price chart is unavailable`, not simulated Bitcoin data.
   - Real balance is displayed from the existing `BalanceViewItem`. Hide-balances setting is honored.
   - `Send` navigates to existing `SendV2Page(wallet)` only when account can sign; `Receive` retains chain-specific receive route.
   - Description's More information action opens existing `CoinPage`.
   - Responsive text truncation and scroll via existing `LazyColumn`.
2. `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/token/TokenBalanceScreen.kt`
   - Replaces the legacy generic toolbar/detail header with Veldigo-designed components.
   - Retains existing chain-specific extra cells, incoming address for watch accounts, locked balances and reserved funds warnings, sync error UI, Zcash migration, Tron alert sheets and the real transaction history.
   - Preserves `TokenBalanceViewModel` and `TokenBalanceModule.Factory(wallet)` — no signing, balances, recovery or RPC logic rewritten.
3. `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: accurate unavailable-data/error descriptions.
4. `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/balance/token/VeldigoAssetChartRulesTest.kt`: tests missing series, invalid NaNs/infinities, chronological ordering, duplicate timestamps and changes calculated exclusively from input data.
5. `.github/workflows/android-verify.yml`: mandatory chart-data validation unit test in addition to preexisting Android build and security tests.

## Purposeful differences from the reference

- Price, holdings, chart and performance are **real data**. The artwork-only values of `$66,342.10` and `0.0934 BTC` are never presented as real holdings.
- Price change reflects the **currently selected chart period**, not necessarily a 24-hour value when another period is selected. The implementation avoids a misleading static `(24h)` label.
- Custom assets may lack market charts; show honest unavailability rather than invented lines.
- Live data source may support more or fewer chart periods than the six visible in the artwork.
- The network-specific transaction and safety warnings remain beneath the header; they may extend the screen beyond the flattened reference.

## Quality gates

| Verification | Status |
|---|---|
| Approved visual reference located and inspected | YES |
| Native Compose screen 11 source committed | YES |
| Live MarketKit chart and coin overview connected | YES, source review |
| Live balance and wallet-specific Send/Receive retained | YES, source review |
| Watch-account Send disabled | YES, source review |
| No hardcoded art balance or fake price curve | YES, source review |
| Android build for latest commit | Await latest GitHub Actions result |
| Chart-data JUnit tests | Committed and wired to CI, not yet confirmed passing |
| Sending/receiving with actual testnet assets | NOT RUN |
| Coin data unavailable/error/offline tests on device | NOT RUN |
| Real emulator screenshot, overlay and pixel-by-pixel difference | NOT RUN |
| True 1:1 verified against native-density source | **NO** — original reference is a reduced composite |
| Cryptocurrency production and real-funds audit | **NOT APPROVED** |

## Next source panel

Screen **12. Send BTC / Send Crypto** in the original reference. Maintain real recipient address validation, chain selection, fee estimation, amount precision, confirmation, PIN/biometric signing and cancellation. Do not add a button that bypasses wallet safety gates.
