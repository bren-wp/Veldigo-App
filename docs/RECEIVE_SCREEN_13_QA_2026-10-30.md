# Veldigo Wallet — Screen 13: Receive BTC / Receive Crypto

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: feat/onboarding-02-security-mint

## Original approved reference
Veldigo Crypto Wallet UI Mockup.png (1536 × 1024), second row, sixth handset, labelled "13. Receive". The small embedded phone panel is approximately x=1104..1319, y=389..704.
The image is a compressed mockup, NOT a native Android screenshot at a known device scale.

Visible reference composition:
- Very dark navy/green background and white Android status icons.
- Left-arrow toolbar, centered white "Receive BTC".
- Large square black/white QR code with white quiet zone at mid-upper screen.
- "Your BTC Address" label beneath it.
- A dark rounded address cell with shortened bc1 address and mint copy icon.
- A full-width dark mint-tinted "Share Address" button beneath the address field.
- NO sample blockchain address is displayed by real application code.

## Implementation, same open PR #7
- New native Compose file: walletkit/src/main/java/io/horizontalsystems/walletkit/modules/receive/ui/VeldigoReceiveContent.kt
  - Responsive QR size 205dp (174dp on small-height devices), centered with 10dp white quiet zone, no logo overlapping modules.
  - Local QR generation from the real ReceiveModule.AbstractUiState.uri via rememberQrCodePainter and error correction.
  - Label uses actual wallet.coin.code; displayed address is shortened, but Copy always places the COMPLETE adapter-derived uiState.address on the clipboard.
  - Share Address uses the true address. If a requested amount exists, shows Share Payment Request and passes the original chain-native URI made by AddressUriService rather than an invented payment link.
  - Missing address or URI means no QR image and disabled copy/share; never display/sample a replacement address.
  - Preserve watch-only account alert, critical receive warnings, network and testnet labels, address format/type info, requested amount and extra bitcoin used-address navigation.
  - The optional "Set Amount" functionality remains accessible and uses the existing AmountInputDialog and AddressUriService; **Remove requested amount** resets it through setAmount(null), restoring the normal address-only QR and share flow. No new transfer/signing path.
- ReceiveAddressScreen.kt: reuse existing loading/error retry states and replaces only ViewState.Success content with the reference design. Existing setup, view state and error routes remain unchanged.
- ReceivePage.kt: standard receiving title uses "Receive BTC" / real token coin code and explicitly passes the coin identity into UI.
- veldigo_welcome_strings.xml: localized address, Copy, Share and missing-data labels.
- VeldigoReceivePayloadRulesTest.kt: blank-address / missing URI safety, copy-full-address, share-address or amount-request behavior and zero-amount handling.
- android-verify.yml: require this walletkit test in CI.

## Safety and functionality
- All receiving addresses come from existing IReceiveAdapter.getFreshReceiveAddress(), not from mockup text.
- Existing BTC address rotation/history, supported blockchain URI generation, optional requested amount, chain-specific receive plugins, network and testnet details are untouched.
- Copy address differs purposefully from copying a payment URI: an optional amount changes the QR/request, not the underlying public address; the Share button explicitly reflects that choice.
- No screenshot restriction was disabled on any mnemonic/PIN page. Receive address is public information; no seed or secret key in UI/QR/clipboard.
- The QR image is never rendered for blank address/URI; no fabricated or cached address is substituted.

## QA gates
| Gate | Status |
|---|---|
| Panel 13 of original reference inspected | YES |
| Source-level UI and real address service integrated | YES |
| QR generated only from actual blockchain address URI | YES, source |
| Copy always uses full address, not screenshot shortened text | YES, source |
| Amount and watch-account handling retained | YES, source |
| No sample Bitcoin or network address in production code | YES, source |
| New walletkit unit test added to Android CI | YES; results pending |
| Latest CI compilation | PENDING / not asserted passing |
| Real QR scanning on multiple devices | NOT RUN |
| Camera scanning/QR checksum across supported chains | NOT RUN |
| Screenshot / overlay / pixel error measured | NOT RUN |
| **Pixel-perfect 1:1 VERIFIED** | **NO** |
| Production wallet security and real deposits sign-off | **NOT APPROVED** |

Intentional visual deviations: amount request and network/testnet warnings add contextual rows absent from the mockup. The optional Set Amount and Bitcoin used-address navigation remain available for existing workflows.

Next reference: screen 14 Swap (real quote service, slippage, fees and confirmation); must not show a fabricated exchange rate or submit before user confirmation.
