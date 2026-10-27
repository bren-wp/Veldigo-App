# Veldigo Android reference matrix

Primary reference: the user's navy/mint four-screen panel. Secondary reference: the user's larger multi-screen board; **use its functional screen inventory, not its purple brand color**.

## Design tokens

| Token | Value | Intended usage |
|---|---|---|
| Background | `#071421` | Global dark base |
| Raised surface | `#112337` | Cards, inputs and panels |
| Elevated/gradient stop | `#10283A` | Action tile / portfolio |
| Hairline border | `#234556` | Containers |
| Accent | `#41E6C7` | Positive actions and selected state |
| Secondary text | `#B5C6D7` | Supporting copy |
| Main text | `#FFFFFF` | Headings and balances |
| Large radius | `20dp` | Cards |
| Action radius | `17dp` | Tiles |
| Action target | `62dp` | Wallet action icons |
| Horizontal screen inset | `16–24dp` | Device-dependent |

## Screen parity and function matrix

- [x] App brand label, palette and original icons — **implementation**, not visual QA
- [x] Intro / onboarding layout — **implementation**, not visual QA
- [x] Empty wallet create/import/watch actions — **wired to real routes**, not visual QA
- [x] Portfolio total balance surface — **wired to actual wallet total**, not visual QA
- [x] Portfolio action tile style — **real handlers retained**, not visual QA
- [x] Receive address card / real QR — **restyled**, not visual QA
- [x] Send form background — **restyled**, transaction flow retained
- [x] Create-wallet choice description — **restyled**, key flows retained
- [ ] Recovery phrase backup / verification — **visual parity and secure test pending**
- [ ] PIN, biometric unlock — **visual parity and device QA pending**
- [ ] Asset details / market charts — **visual parity pending**
- [ ] Swap and route confirmation — **visual parity/provider QA pending**
- [ ] Buy via fiat partner — **unavailable pending contract, jurisdiction and API**
- [ ] Activity / transaction details — **visual parity pending**
- [ ] NFT collection and market pages — **visual parity pending**
- [ ] WalletConnect session manager — **visual parity/security QA pending**
- [ ] Security settings, theme, about and help — **visual parity/content audit pending**

## Screenshot validation protocol

1. Build the same flavor/density on an Android emulator or device as the reference (status/nav insets matter).
2. Record screen dimensions and render at 100% zoom.
3. Compare geometry, text baselines, inter-element spacing, glyphs, rounding, icon bounds, chart sizes and shadows with an image-diff overlay.
4. Validate larger font sizes and narrow phones without clipping or overlaps.
5. For money amounts, fees, recovery phrases and QR addresses, validate underlying **functional data** separately: a reference screenshot never authorizes fake values.
6. Repeat comparisons for enabled/disabled/loading/error/confirmation and translated states.

No full 21-screen pixel comparison or exhaustive device pass has been completed. v0.3 includes branded About/settings, live-data activity card restyling and system text-scale compliance. Only screenshots produced by an actual running Android build may be used to mark visual QA complete.
