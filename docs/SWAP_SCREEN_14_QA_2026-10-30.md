# Screen 14 — Veldigo Swap / Exchange

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Reference inspected

Source: the original user-provided `Veldigo Crypto Wallet UI Mockup.png` (1536×1024 composite), **panel 14: Swap**, the last phone in the second row. The crop was inspected privately; the source is a reduced presentation layout, not a 1:1 screenshot at Android physical pixel density.

Its visible sections are:
1. Black/midnight background, white back arrow, centered **Swap** app-bar title.
2. **From** label over a large rounded dark token card (reference ETH, 0.5, USD equivalent).
3. Small centered circular swap-direction icon between From and To.
4. **To** label over a large rounded dark token card (reference USDT, 1668.42, USD equivalent).
5. Centered **1 ETH ≈ 3,336.84 USDT** rate row.
6. **Slippage Tolerance** caption with outlined **0.5%** dropdown.
7. Wide mint **Swap** CTA near the bottom.

**Reference values are illustrative and are never embedded as real prices, balances, quotes or slippage settings.**

## Implementation on the SAME PR #7

- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/multiswap/VeldigoSwapForm.kt`
  - New native Compose From and To panels with Midnight Navy `#030E0D`, Mint `#41E6C7`, 12dp radii, circular swap-direction control, live rate, slippage section and adaptive spacing.
  - Keeps the existing `TokenAmountInput` for **From** editing (currency-aware amount, actual token selector, network fees/precision inherited from the original service), and `TokenSelector` for **To**. Genuine output amount and fiat conversion are derived only from `SwapUiState` and `SwapProviderQuote`.
  - Balances, 25/50/75% quick actions, Max and Clear remain available. Reversal stays disabled whenever an external-recipient requirement makes it unsafe.
  - Rate appears only when the existing service supplies a positive-input provider quote; otherwise **Waiting for a live exchange quote**, not reference's fabricated 3,336.84.
  - **Slippage Tolerance** uses **Set on review** rather than a fake default 0.5%. The current project's configurable and validated slippage resides in `SwapConfirmPage` / `SwapSettingsSlippagePage`, after the actual route and quote have been selected. The explanatory selector is interactive and gives the user this guidance. It does not silently modify transaction settings.
  - A pure `VeldigoSwapReferenceRules` gates display and Continue against missing/stale quote and asynchronous user action.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/multiswap/SwapPage.kt`
  - Replaced generic `SwapInput` with new composition and removed obsolete generic `SwapInput` / `SwapCoinInputTo` blocks, keeping the reusable `AmountInput` and `FiatAmountInput`.
  - Changed background to midnight navy, switched page autofocus off for the uncluttered reference initial state, and styled **SwapStep.Proceed** with a full-width mint 54dp **Swap** button.
  - Retains **existing** SwapViewModel, quote service, recipient selection, SwapConfirmPage, risk scores, provider selector, refresh/expiry, allowance action, AML checks, SwapTermsPage, transaction approval and wallet signing. No swap bypass, direct broadcast or synthetic quote is introduced.
  - The button is enabled only for `SwapStep.Proceed`, not during an existing Proceed latch and **only with an actual valid quote**.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable From/To/action, rate-unavailable and slippage-review messages.
- `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/multiswap/VeldigoSwapReferenceRulesTest.kt`: no quote, partial setup, provider errors, quote calculation, AML and repeated-tap behavior.
- `.github/workflows/android-verify.yml`: new mandatory walletkit unit-test job step.

## Intentional differences for accuracy and custody safety

- Real active wallet tokens may not be ETH and USDT, and actual balance/rates will differ from screenshot; no fake ETH/USDT screen state is injected.
- Existing configurable slippage is on **the confirmation screen**, not the first quote/edit screen. The mockup's 0.5% cannot be assumed; it remains a review setting. Changing that architecture would require reviewed quote/confirmation synchronization work before display and editing on screen 14.
- Provider route info, AML warnings and required authorization cards remain visible below the main form. They are necessary to understand who supplies the quote and prevent unsafe transactions even though omitted from the concept art.
- The app currently supports multi-chain swaps and external recipients; those warnings and controls remain intact.

## QA gates and release blockers

| Gate | Status |
|---|---|
| User-approved screen 14 concept inspected | YES |
| Native Compose form committed to existing PR | YES |
| Real quote, balance, token selection and conversion source retained | YES at source level |
| Swap direction and high-risk external recipient gate preserved | YES at source level |
| AML / swap terms / signing unchanged | YES at source level |
| Mockup price, 0.5% and output never shown as real data | YES at source level |
| CTA requires valid provider quote and no duplicate attempt | YES at source level |
| Unit tests committed and wired to Android CI | YES — execution pending |
| Latest Android Gradle build | PENDING / not yet confirmed |
| Swap testnet route, quote expiry and failure-mode QA | NOT RUN |
| Screenshot on matching device density and measured pixel-diff | NOT RUN |
| **Pixel-perfect 1:1 verified** | **NO** |
| Production crypto/real-funds release safe | **NOT APPROVED** |

## Next reference panel

Screen **15 — Swap Confirmation**, preserving provider quote expiry, fee presentation, slippage, recipient, AML screen and transaction signature consent. Do not replace the confirmation with a fake success screen or send directly from screen 14.
