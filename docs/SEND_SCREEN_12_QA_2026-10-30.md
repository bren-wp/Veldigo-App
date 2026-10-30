# Veldigo — Screen 12: Send BTC / Send Crypto

Date: 2026-10-30
Existing PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: feat/onboarding-02-security-mint

## Reference

Approved source: Veldigo Crypto Wallet UI Mockup.png (1536×1024 flattened composite), handset 12. Send in second row (fifth phone; approximately x=875–1100 and y=380–720). The source is a presentation-scale artwork, NOT a native density Android screenshot.

It shows a dark background, centered Send BTC title, To recipient card and adjacent QR button, Amount card with BTC and approximate fiat equivalent, Network Fee plus Normal selector, and full-width mint Continue button at bottom. The sample recipient, amount and fee are NOT production data.

## Changes on the same PR
- VeldigoStandardSendFields.kt: native Compose To / Amount / Network Fee section, original address editor and QR scan/selection affordance; real TokenAmountInput, local styles and a clickable fee settings entry only for supported chains.
- SendV2Screen.kt: the Standard route uses these sections and a mint 54dp Continue button. Continue is enabled only when the existing SendFormViewModel reports SendStep.Proceed. RiskyAddressSheet confirmation still precedes navigation, and SendV2ConfirmPage still performs the actual network-specific fee calculation, review, signing and submission.
- Private Send and CrossPay remain accessible from app-bar menus in Standard mode; their original inputs and tab strip remain unchanged when selected. Donation, memo/extra fields, watch/account and disabled-action flows remain governed by existing app logic.
- The real address and fiat conversions are sourced from the existing wallet, recipient validation and price services. No fake BTC address, 0.01 BTC amount, sample fiat value, 0.00001 BTC fee or fabricated live fee preset is added.
- Fee preview is intentionally disclosed as Calculated on the confirmation screen, because the send form state does not contain the chain transaction fee estimate. It is better to be different from the reference than show an arbitrary fee before broadcasting.
- Updated strings in walletkit/src/main/res/values/veldigo_welcome_strings.xml, plus VeldigoSendUiRulesTest.kt and a CI step validating the Continue gate.

## Previous CI failure
The prior run https://github.com/bren-wp/Veldigo-Wallet/actions/runs/41033176040 failed walletkit compilation on TokenBalanceScreen.kt line 13 because of an invalid import androidx.compose.foundation.layout.weight. This was removed in the same PR. The new CI must confirm that no compilation issue remains.

## Acceptance matrix
| Check | Status |
|---|---|
| Visual reference inspected | YES |
| UI source committed in existing PR #7 | YES |
| Original input/address/risk/confirm logic preserved | YES, source level |
| No hardcoded recipient, amount or fee | YES, source level |
| Full new Android build/test result | Pending |
| Real device transaction, error, offline, fee and testnet QA | NOT RUN |
| Actual screenshots and measured pixel differences | NOT RUN |
| Pixel-perfect 1:1 confirmed | NO |
| Production release authorized | NO |

Notes: Original form can show chain-specific memo and extra input fields not visible in artwork. Exact reference parity remains unverified. Do not bypass PIN/biometric and signing checks or claim a send is complete before broadcast confirmation.

Next reference: screen 13 Receive BTC, with a real address and QR—not a hardcoded sample.
