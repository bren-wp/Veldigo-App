# Veldigo Wallet — Screen 10: Home / Wallet

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Source reference inspected
- Original saved image: `Veldigo Crypto Wallet UI Mockup.png`, a **1536 × 1024** flattened multi-phone concept board.
- Home/Wallet is the **third phone in the second row**, labelled **10. Home / Wallet**, approximate board bounds x=~420–708 and y=~384–724.
- A crop was inspected locally only; no reference image was generated into the chat.
- This is concept artwork, not an unscaled real device screenshot with known density, dynamic font metrics or Android bar insets.

## Design specification
- Midnight nearly-black canvas, mint and white typography, top left Veldigo logo, gear at right.
- Centered *Total Balance* and large value. Reference's `$12,487.32` is **mockup data** and must never be hard-coded.
- Reference's `+5.21% (24h)` is **mockup data**. Portfolio aggregate 24h performance is not provided by the existing `TotalService`; do not display or calculate a fake portfolio-wide change.
- Four equally spaced action icons in dark mint-outlined squares: **Send / Receive / Swap / Buy**.
- Segmented dark **Tokens / NFTs** switch, Tokens selected with mint rim.
- Scrollable token rows: authentic coin icon, coin name and symbol, current primary/secondary value according to existing user's currency settings, true **token market price change** if provided by the live `CoinPrice` service. Do not represent token price movement as portfolio gain.
- Bottom five destinations in the concept: **Home / Markets / Swap / History / Settings**. Only actual available features should be enabled and navigable.

## Implementation and wiring
- `walletkit/.../balance/ui/VeldigoHomeComponents.kt`: native Compose header, live total, action row, Tokens/NFTs switch, coin rows (existing `IconCell` rendering with actual token data), transparent handling of unsupported features.
- `walletkit/.../balance/ui/BalanceItems.kt`: first item now renders Home branding / live total and real Send / Receive / Swap navigation, followed by the segment and live tokens; preserves wallet switching, watch-only restrictions, existing backup/migration warnings, pull-to-refresh, sync-error access, token tap navigation, swipe-to-hide and add-token navigation.
- `walletkit/.../balance/ui/BalanceForAccount.kt`: removes duplicate legacy top bar so the reference's branded header is the only bar; keeps balance screen state, chain navigation, QR scanner launcher, WalletConnect callbacks and migration dialogs. Enforces Android status-bar insets.
- `walletkit/.../main/MainPage.kt`: main destinations visually ordered to prioritize Home, mint selected state and labeled icons; actual History control routes into existing `TransactionsPage`; no separate fake transaction history.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable labels, Buy and NFT capability explanations.

## Intentional truthfulness differences
1. **No invented money:** TotalUIState and BalanceViewItem2 are the existing live value sources; unavailable values remain `---` / `—` rather than mockup numbers.
2. **No fabricated gain:** the portfolio 24h gain from the visual mockup is omitted. Coin-level price diff uses `viewItem.diff` when present; these figures come from the market service and can be stale or unavailable.
3. **Buy is unsupported:** no verified on-ramp/payment or compliance integration exists in this repository. Pressing Buy displays an explicit unavailable notice and **does not debit a card**.
4. **NFT tab is not a fake empty portfolio:** it explains that NFT holdings are not yet connected to this view. This must not be interpreted as confirming there are no NFTs.
5. **Watch account safety:** Send and Swap are disabled for watch accounts; Receive remains available where supported.
6. **Market and Swap bottom tabs:** appearance depends on live feature toggles; History remains an existing real transactions route, not artificial history data.
7. The mockup uses distinct physical dimensions, image shadows, gradients and typeface unknown to the actual Compose device build. Exact 1:1 **requires** emulator/device screenshots and a per-pixel diff.

## QA gates
| Check | Status at source commit |
|---|---|
| Reference layout inspected | YES |
| All display balances and token values sourced from live models | YES in source |
| Token taps preserve `TokenBalancePage` | YES |
| Send/Receive/Swap use established navigation | YES |
| No fake Buy or NFT holdings | YES |
| Hidden-balance state masks values | YES in source |
| Watch-only cannot initiate Send/Swap from Home | YES in source |
| New code compiles on latest head | **PENDING CI** |
| UI instrumentation, genuine screenshots and font scale tests | **NOT RUN** |
| Reference-overlay pixel-diff at matched density | **NOT RUN** |
| Pixel-perfect 1:1 certified | **NO** |
| Real-money release certified | **NO** |

## Manual Android test scenarios
- Fresh wallet (zero balance), funded dummy/testnet wallet, offline state, slowly syncing, hidden balances, watch-only, no tokens.
- Send, Receive, Swap, History, Settings, wallet switching, press-and-hold/switch wallet, token details and swipe-to-hide.
- Unavailable network prices show placeholders rather than a fabricated 24h increase.
- Buy notice does nothing financial; NFTs tab does not claim the user has zero NFTs.
- Color/spacing/text metrics and status/nav insets at 360, 384, 412 dp widths, Android 15/16, large font scaling and gesture/3-button modes.
- Screenshot test fixtures must not expose live balances or recovery phrases; never disable secret-page `FLAG_SECURE`.

**Next reference:** Screen 11 — Market / Price Chart; continue in the same PR when requested.
