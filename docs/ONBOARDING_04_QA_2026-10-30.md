# Onboarding 4 — Freedom Without Borders
Date: 2026-10-30
PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## User-provided visual reference
File: `Veldigo Crypto Wallet UI Mockup.png`, 1536 × 1024 pixels (fourth handset in top row, titled `4. Onboarding`).
The phone image is a small panel within a composite and is **not** a native-density capture.

### Reference appearance
- Nearly black (#030D0D family) base, restrained navy/mint lighting and white text.
- Centered small mint V / white Veldigo wordmark at the top.
- Large dark teal/blue globe with continents, subtle longitude/latitude grid, mint highlight on upper left; approximate position: middle of the screen above the headline.
- Two-line bold centered heading: `Freedom Without` and `Borders`.
- Centered subtitle: `Your crypto, on your terms.` / `Anywhere in the world.`.
- Four tiny progress dots. The fourth-panel artwork unexpectedly shows the **first** dot mint; that artwork was followed for rendering, with a correct fourth-step accessibility announcement.
- **One** full-width mint `Get Started` button. **No Skip button**.

## Implementation in existing PR #7
1. `walletkit/.../intro/VeldigoFreedomSlide.kt`: new native Compose screen; locally drawn globe in Canvas with clipped sphere, curved grid, landmasses, rim and lighting, plus responsive typography and the only CTA.
2. `walletkit/.../intro/IntroScreen.kt`: replace the last legacy intro page with this screen and delete obsolete `FollowupIntroSlide` / `SpaceIllustration` UI implementation and their imports. Retain the original local-storage completion callback.
3. `walletkit/.../intro/VeldigoIntroProgress.kt`: allow `semanticStep=4`, while keeping visual dots faithful to the fourth-panel concept.
4. `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: new localizable title, subtitle and CTA text.
5. `maestro/onboarding/intro.yaml` and `maestro/subflows/pass_intro.yaml`: new final-screen CTA, removal of old text assertions.
6. `maestro/onboarding/freedom_final.yaml`: test full transition to the fourth page, final action and persistence across relaunch.

## Security and correctness
- The globe is **decorative only**: no balances, keys, user data, wallet address or third-party network calls.
- No seed/private-key creation, encryption, keystore, signing, blockchain integration or transaction verification was modified.
- `onGetStarted` calls existing `IntroViewModel.onStartClicked()`, preserving the durable `mainShowedOnce` behavior.
- No fake wallet balances, accounts, QR codes, signing success or external funds are presented.

## Acceptance and open gates
| Gate | Status |
|---|---|
| Approved source composite inspected | YES |
| Correct fourth-panel structure | Implemented in Compose |
| Obsolete Skip / landscape removed | YES in source |
| New CTA callback wired to existing state | YES in source |
| Gradle CI for latest PR head | PENDING until current run concludes |
| Maestro instrumentation | Authored, **NOT RUN** |
| Pixel-diff at equal density with real Android screenshot | **NOT RUN** |
| Exact globe coastline and original 3D shading | **NOT VERIFIED** — local approximation |
| Accessibility font scaling, device insets and talkback | **NOT DEVICE-TESTED** |
| **Pixel-perfect 1:1** | **NOT VERIFIED** |
| Approved for production as a cryptocurrency wallet | **NO** — independent security and signing gates remain |

## Next scope
The introductory flow is now code-implemented across all four screens, but must receive device screenshot QA and final visual tuning. The next design panel after onboarding is **Create New Wallet / Import Existing Wallet**. Continue in the existing repository and prioritize crypto asset safety over appearance where they conflict.
