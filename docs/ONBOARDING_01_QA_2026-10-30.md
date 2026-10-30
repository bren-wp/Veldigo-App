# Onboarding 1 — reference review and acceptance log (2026-10-30)

## Reference provenance
- Original previously uploaded file: `VELDIGO Crypto Wallet Screenshots.png`.
- Overall composite: **1536 × 1024 px**, four adjacent screens.
- Onboarding/welcome panel: first **384 × 1024 px** (leftmost panel, approximately x=0–383).
- First panel is the approved midnight-navy and mint visual reference. It is not a deployed Android capture.
- No reference photo or screenshot has been embedded as a static background for functional UI.

## Visible reference features and approximate pixel bounds
| Feature | Approximate visible position in 384 × 1024 reference |
|---|---|
| Status bar | top 0–35 px |
| Language picker | right-aligned, around y=64–84 px |
| Large mint V | around x=128–260, y=119–235 px |
| Wordmark and CRYPTO WALLET | centered around y=248–293 px |
| Landscape / globe / mint beam | extends across y=138–454 px |
| Two-line headline | x≈29 px, y≈456–535 px |
| Supporting copy | x≈29 px, y≈556–623 px |
| Three feature glyphs and labels | y≈650–730 px |
| Primary create button | x≈23–363 px, y≈766–822 px |
| Secondary import button | x≈23–363 px, y≈834–890 px |
| Legal explanatory text | around y=917–946 px |
| Navigation gesture bar | bottom 1005–1015 px |

These are *visual inspection estimates*, not measured emulator parity results. Device dp sizes depend on actual density, font scaling and system insets.

## Changed implementation
- `walletkit/.../intro/VeldigoWelcomeScreen.kt`: interactive layout, large mint logo, typography hierarchy, feature row, secondary button, graphics drawn locally in Compose, compact-height vertical scrolling.
- `walletkit/.../intro/IntroScreen.kt`: replace first pager page with approved welcome structure; retain the other two intro slides pending their separate visual work.
- `walletkit/.../nav3/EntryPage.kt`: thread the existing `HSNavigation` into intro.
- `walletkit/src/main/res/drawable/ic_veldigo_welcome_mark.xml`: locally defined vector approximation of the mark.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: text resources.
- `maestro/onboarding/intro.yaml` and `maestro/subflows/pass_intro.yaml`: replace obsolete `Next` assertions with real first-page labels and swipe navigation.

## Functional wiring
- **Create a new wallet** → `navigateWithTermsAccepted(CreateAccountPage(), ...)`.
- **Import existing wallet** → `navigateWithTermsAccepted(ImportWalletPage(), ...)`.
- **English** control → existing `LanguageSettingsPage`.
- Both wallet actions preserve existing account creation/import, terms gates and cryptographic engine, and flag the welcome flow as seen.
- Pages 2 and 3 remain reachable by horizontal swipe, with existing `Get started` flow.

## Acceptance gates
| Gate | Status |
|---|---|
| Primary source screenshot located and reviewed | YES (original composite) |
| First page implemented in real Compose components | SOURCE COMMITTED |
| Source-level action routing | WIRED; not runtime tested |
| Reference pixel geometry matched on device | **NOT VERIFIED** |
| Main screenshot captured from same device conditions | **NOT RUN** |
| Numeric pixel-diff / overlay measured | **NOT RUN** |
| Small-screen layout and font scale | IMPLEMENTED FALLBACK; NOT DEVICE-TESTED |
| Maestro tests | UPDATED; NOT RUN |
| Gradle / Android CI for latest commit | CHECK RUN IN PROGRESS / REVIEW REQUIRED |
| Blockchain, backup/restore, security audit | NOT RUN; production blocked |

## Remaining visual differences
- The full-bleed landscape/globe illustration is a local Compose approximation. The **separate original art asset is missing**; rebuilding shapes from the flattened composite cannot be called 1:1.
- Mint V mark is recreated as scalable vector and requires a pixel diff against the source.
- Typeface, line wrapping, feature glyphs, button baselines, status bar and navigation inset must be compared with a real emulator capture.
- Treat the visual status as `IMPLEMENTED / NOT 1:1 VERIFIED`, not as a completed or production-ready financial wallet.

## Next screen
Proceed with **Onboarding 2** after validating the preceding page through actual build output and screenshots. Do not merge if critical CI checks fail.
