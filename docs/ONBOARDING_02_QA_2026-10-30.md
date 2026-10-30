# Onboarding 2 — Mint Security slide QA
Date: 2026-10-30

## Approved visual input
- Source image in the user's saved assets: `Veldigo Crypto Wallet UI Mockup.png`, **1536 × 1024 px**.
- Reference: first row, **second phone panel**, labelled `2. Onboarding`.
- Use navy and mint values from `docs/REFERENCE_1TO1.md`, **not** the violet palette in `Veldigo kripto novčanik – UI tijekovi.png`.
- This is a reduced-scale multi-screen composite, not a true-resolution screenshot. Its embedded phone panel cannot establish 1:1 pixel accuracy independently.

## Source-audited layout
| Area | Target visible in reference | Source-level implementation |
|---|---|---|
| Header | centered small mint V and white Veldigo | existing local vector and text |
| Backdrop | dark navy gradient, subtle green glow | native Compose Canvas gradient |
| Illustration | three layered dark cards with prominent mint shield | locally drawn scalable Canvas; *not exact 3-D art* |
| Headline | `Secure` bold white, centered | 30sp, center aligned |
| Description | `Your assets, protected with industry-leading security.` | localized resource, centered 15sp / 22sp |
| Pagination | three small dots, active mint position 2 | 3 indicators, accessibility announcement |
| Next | wide mint rounded CTA | 54dp button; pager to page index 2 |
| Skip | small secondary action | invokes original `IntroViewModel.onStartClicked()` |

### File changes
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/intro/VeldigoSecuritySlide.kt`: dedicated responsive second page, native illustration, semantic progress.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/intro/IntroScreen.kt`: mount second page in original pager.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: new UI strings.
- `maestro/onboarding/intro.yaml`, `maestro/subflows/pass_intro.yaml`: navigation regression coverage.

## Engineering and safety constraints
- UI work only, no changes to local wallet storage, mnemonic handling, signing, recovery, key derivation or blockchain RPC clients.
- Real security claims require independent security audit. The design copy follows the user-supplied illustration, **not** a certification or security conclusion.
- Skip persists the original onboarding-complete flag. Next advances to the existing third intro page.
- Scroll fallback for compact heights; physical-device font scaling, landscape, display cutout, gesture navigation and TalkBack checks are still mandatory.
- Reference artwork itself was not embedded in the app, and no AI image or screenshot masquerading as a real build was produced.

## Acceptance matrix
| Validation | Status |
|---|---|
| Reference identified | YES |
| Code changed | YES |
| Gradle compilation on current PR SHA | PENDING at document creation |
| Maestro test script updated | YES; not executed |
| Android screenshot of current commit | NOT CAPTURED |
| Same-resolution pixel-diff comparison | NOT RUN |
| External audit of security claims | NOT RUN |
| Onboarding 2 marked **1:1 VERIFIED** | **NO** |
| Safe to merge automatically | **NO**, review CI and visual gate first |

Next screen: **Onboarding 3** after source and device QA on the second screen.
