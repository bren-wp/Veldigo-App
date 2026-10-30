# Create New Wallet (reference panel 5) — source and visual QA

Date: 2026-10-30
Existing pull request: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Existing branch: `feat/onboarding-02-security-mint`

## Approved reference
File: `Veldigo Crypto Wallet UI Mockup.png`, full 1536 × 1024 image.
Fifth top-row phone, labelled **5. Create Wallet**, approximately x=866..1079, y=8..356 in the composite. This panel is **presentation-scale**, not a true-density device capture.

Visible structure in the reference:
- Almost black/dark-navy background and white status icons.
- Header: back arrow on the left, small mint V plus white Veldigo wordmark centered.
- Large left-aligned title `Create New Wallet`.
- Supporting line `Set up your wallet in a few simple steps.`.
- Three vertically stacked, dark rounded rectangular choices with mint icons on raised mint-green squares, titles/subtitles and white right chevrons:
  1. **Create a new wallet** — Generate a new seed phrase.
  2. **Import existing wallet** — Use your seed phrase or private key.
  3. **Connect hardware wallet** — Ledger, Trezor and more.
- Reference leaves the lower part of the screen empty. No fake wallet balance or secret phrase is present.

## Code changes
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/createaccount/CreateAccountPage.kt`
  - New native Compose header, vertical layout and 100dp action cards, 15dp radii, mint local-drawn icon artwork and chevrons.
  - Insets and vertical scrolling for compact phones; 48dp icon hit targets via card.
  - Preserves the existing public `WalletType` component used elsewhere by ImportWalletPage.
  - Real **Create** opens `CreateAccountStandardPage(input)` with the unchanged ViewModel/key generation.
  - Real **Import** opens `ImportWalletPage` through `navigateWithTermsAccepted` and retains the existing after-success pop target, defaulting to `CreateAccountPage`.
  - Existing **Passkey** wallet creation retained in a compact header overflow menu, including the existing passkey terms gate and passkey ViewModel.
  - **Connect hardware wallet** opens an explicit unsupported-feature notice. There is no verified Ledger/Trezor connection module in this repository, so we do not claim or fake hardware signing. Never ask users to enter a hardware seed as a workaround.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: localizable reference text and capability disclosure.
- Maestro: `maestro/subflows/create_standard_wallet.yaml`, `maestro/subflows/restore_wallet_from_phrase.yaml`, `maestro/onboarding/create_wallet_standard.yaml`, `maestro/onboarding/restore_wallet_from_recovery_phrase.yaml`, and `maestro/onboarding/create_wallet_chooser.yaml` updated/added.

## Important visual deviations
- Reference text on hardware choice says `Ledger, Trezor and more`. Our subtitle appends **(unavailable)** to avoid misrepresenting an unsupported security-critical feature.
- The passkey creation path was preserved via a tiny header overflow menu not present in the mockup.
- Icon shapes are editable, original local Canvas vectors rather than exact extracted source icon art.
- Actual rasterization, font family, glyph baselines, status bar, card spacing and color are **not yet compared against a real Android emulator screenshot**.

## Mandatory verification gates
| Gate | Status |
|---|---|
| Reference panel inspected | YES |
| Existing wallet navigation audited | YES at source level |
| UI code pushed to existing PR | YES |
| Terms gate for Import preserved | YES at source level |
| Existing passkey wallet path retained | YES at source level |
| Unsupported hardware not misrepresented | YES at source level |
| Current PR head Android build | Check CI, not confirmed during source authoring |
| Maestro create/import/navigation tests | Authored; NOT RUN |
| Pixel-diff at matching device resolution | NOT RUN |
| Visual fidelity 1:1 | **NOT VERIFIED** |
| Cryptographic safety independently audited | NO |
| Ready for production release | NO |

## Next
Once the newest CI build passes, run actual UI/device QA and review traces/screenshots. Next visual panel is **Backup Your Recovery Phrase**, which requires particularly careful screenshot/clipboard and secret-handling controls. Do not insert sample seed phrases into the real account flow.
