# Veldigo Wallet — Screen 15: Buy Crypto / Sell Crypto

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Correct screen identification

**Panel 15 is Buy Crypto, NOT Swap Confirmation.** The approved source is `Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024 composite), first phone of the **third row**. Its embedded phone frame was privately inspected. It is a flattened illustration, not a native-density Android screenshot.

Reference composition:
- Midnight-dark background, Android status row, back arrow and centered `Buy Crypto` title.
- Segmented `Buy / Sell` control; active Buy mint, inactive Sell dark.
- `Asset` heading; dark rounded selector with Bitcoin (BTC), coin icon, subtitle and chevron.
- `Amount` heading; dark rounded `100` editable amount field and `USD` currency suffix.
- Muted approximate `≈ 0.0015 BTC` conversion (only an illustrative estimate in artwork).
- `Payment Method` heading; dark rounded selector with sample Visa card and masked digits.
- Full-width mint `Buy BTC` action anchored near the lower edge.

## What actually exists in the repository

An audit of the current repository found **no functional fiat on-ramp/off-ramp, registered card vault, purchase execution or regulated payment provider module**. Existing `BuySubscription*` files are app subscription checkout, not a cryptocurrency purchase processor. Therefore no payment can safely be sent, and no fiat-to-crypto quote can be called genuine.

## Source implementation in the SAME PR #7

- New `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/buy/BuyCryptoPage.kt`:
  - `@Serializable data object BuyCryptoPage : HSPage()`, Compose native responsive layout with Android insets.
  - Uses dark `#030E0D`, cards `#122020`, borders `#28403E` and mint `#41E6C7`.
  - Buy/Sell segmented control with actual state; does not charge/sell.
  - Real enabled wallet asset selector from `App.walletManager.activeWallets`, prefer BTC only if it is actually enabled; otherwise first real token or an explicit empty state.
  - Asset chooser displays real token identity and coin icon, no fake assets.
  - Decimal fiat input uses the configured base currency's `code` and `decimal` from `App.currencyManager.baseCurrency`. No hardcoded USD for users with a different base currency.
  - An explicit `Fiat conversion unavailable` label avoids fabricated exchange rates without a provider quote.
  - `Payment method not connected` rather than a fictional `Visa •••• 4242`. It opens an explanation modal.
  - Mint `Buy [CODE]` / `Sell [CODE]` CTA validates entered positive fiat amount, then explains the unavailable provider; it never executes a financial action, collects card credentials or presents false success.
  - Pure `VeldigoFiatPurchaseGate` requires a real provider, verified payment method and live quote before any hypothetical future submission; `providerConnected` is explicitly false. No successful path was added.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/ui/VeldigoHomeComponents.kt`: home Buy shortcut invokes an `onBuy` callback instead of the former dialog.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/ui/BalanceItems.kt`: real `navigation.slideFromRight(BuyCryptoPage)` route.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable reference labels and truthful financial availability disclosures.
- `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/buy/VeldigoFiatPurchaseGateTest.kt`: asserts amount validation, zero and negative, malformed and scientific notation, currency precision, no-provider gate and no serialized payment credentials.
- `.github/workflows/android-verify.yml`: mandatory new JUnit test step.

## Intentional deviations to prevent misrepresentation

1. **No sample Visa card or card digits** appear. The app does not have permission/storage/payment processing for that capability.
2. **No quoted conversion or Buy/Sell exchange rate** is shown because there is no executable provider quote for fiat purchase/sale.
3. The reference `USD` is replaced with the user's actual configured base currency; the artwork did not specify a fixed-dollar-only product.
4. The action remains a clearly labelled informational workflow, never a real purchase. A full provider integration requires authorization, payment risk/privacy design, secure redirect/callback validation, compliance, available regions, fees and live signed quotes.
5. Assets come from actually enabled wallet tokens, not an image-only sample. It is not possible to claim a fully working purchase/sell workflow from this UI alone.

## QA gates

| Gate | Current result |
|---|---|
| Correct original panel 15 located and inspected | YES |
| New Compose page and Home navigation committed | YES |
| Real enabled wallet assets and fiat base currency | YES, source review |
| UI avoids fabricated card, quote and charge | YES, source review |
| App cannot submit fiat payment without provider | YES, source review and unit test source |
| Android CI latest commit compiled | **NOT YET CONFIRMED** |
| Unit tests run successfully | **NOT YET CONFIRMED** |
| Payment provider integrated | **NO** |
| Actual fiat buy or sell executable | **NO** |
| Android screenshot/pixel-by-pixel overlay | **NOT PERFORMED** |
| Exact 1:1 pixel parity | **NOT VERIFIED** — sample card and quote intentionally omitted |
| Approved for crypto financial production | **NO** |

Next panel in the same composite is **16 — Markets** (search and ranking list), not Swap Confirmation. Future Market screens must use actual pricing and change data, not the illustrative screenshot numbers.
