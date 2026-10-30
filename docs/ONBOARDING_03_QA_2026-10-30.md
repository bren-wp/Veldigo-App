# Onboarding 3 — All Your Crypto in One Place

Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Original approved reference
File: `Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024 pixels).
Third top-row handset, labelled **3. Onboarding**. This is a flattened concept board; the internal phone art is far smaller than a device-resolution screenshot.

### Screen elements verified by reading the reference
- Nearly black midnight-navy background (shared mint branding).
- Compact, centered mint V and white Veldigo wordmark at top.
- Floating Bitcoin/Ethereum/other token symbols in circles, larger blue ETH near lower center.
- Two-line heading **All Your Crypto / in One Place**.
- Supporting text **Manage, send, receive and swap thousands of tokens.**
- Four progress dots; first two mint, remaining dim.
- Full-width mint **Next** CTA, with centered **Skip** underneath.

## Changes on the SAME PR #7
- `VeldigoCryptoIntroSlide.kt`: new interactive native Compose screen and illustrative, locally drawn floating coin cluster. No remote image libraries or external background assets.
- `VeldigoIntroProgress.kt`: shared four-dot marker that follows actual reference progress (security has first dot highlighted; crypto has first two highlighted).
- `VeldigoSecuritySlide.kt`: reuse the shared progress marker.
- `IntroScreen.kt`: expand pager to **four** pages to match the four-panel reference. Third screen Next leads to the fourth page. The original `Freedom without borders` text uses `model.slides[index - 1]`, preventing out-of-range access.
- `veldigo_welcome_strings.xml`: add the two-line heading and description.
- `maestro/onboarding/intro.yaml` and `maestro/subflows/pass_intro.yaml`: test all four pages in sequence and the final wallet entrance.
- `maestro/onboarding/skip_crypto.yaml`: verify Skip on the crypto page persists the existing onboarding completion state.

## Functional safety
- BTC/ETH decorations are static illustration, *not* wallet holdings, prices, exchange-rate data or statements about enabled networks.
- Onboarding events continue through the existing ViewModel and account navigation. No mnemonic, secret key, chain/signature, RPC or storage code was modified.
- The fourth page remains the original implementation until its own design pass. Avoid falsely marking it visually finished.

## Verification gates
| Check | Result at authoring |
|---|---|
| Reference screenshot inspected | YES |
| Source implementation committed | YES |
| Four-screen navigation sequence mapped | YES |
| Maestro assertions updated | YES, not executed |
| Android Gradle build for latest commit | CI pending / requires checking |
| Screenshot from real Android runtime | NOT CAPTURED |
| Reference-vs-runtime measured pixel difference | NOT RUN |
| Font scale / TalkBack / cutout / navigation QA | NOT RUN |
| Original 3D coin artwork reproduced exactly | NO — Canvas / native elements are approximations |
| **1:1 VERIFIED** | **NO** |
| Crypto wallet production ready | **NO** |

## Next page
Onboarding 4 / **Freedom Without Borders**, after critical checks are resolved. Retain PR #7 and do not merge if compile or navigation checks fail.
