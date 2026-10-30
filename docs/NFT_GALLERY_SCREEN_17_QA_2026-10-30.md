# Veldigo Wallet — Screen 17: Your NFTs

Date: 2026-10-30
Open PR: https://github.com/bren-wp/Veldigo-Wallet/pull/7
Branch: `feat/onboarding-02-security-mint`

## Original user-approved reference

`Veldigo Crypto Wallet UI Mockup.png` (1536 × 1024) contains **17. NFTs** in the **third row, third phone**. The handset was inspected privately. This is a reduced concept image and not an Android screenshot at known display density.

Visible:
- Nearly-black/navy screen, native status bar, back arrow top left and centered white `Your NFTs` title.
- Three-column NFT artwork grid, around 10px gutter in artwork. Thumbnail cards have ~9px rounded corners. Reference displays colorful illustrated example collectibles, not known assets held by the user's account.
- At least three rows of artworks and vertical scrolling for overflow; no additional artwork labels directly over the images.
- Screen-specific five-item bottom navigation: Home, Markets, **NFTs** (mint active), History and Settings.
- Other original screens retain the main app's established navigation and crypto swaps.

## Existing infrastructure and a critical limitation

The repository already includes `NftMetadataManager`, `NftStorage`, OpenSea metadata provider, `NftAdapterManager` and `NftMetadataSyncer`; however, **both NFT adapter initialization and background metadata synchronization are explicitly commented out** in the existing code. Re-enabling them without synchronizer, transfer and ownership security tests could incorrectly report custody or destabilize wallets. **This PR does NOT re-enable the disabled NFT adapter or background synchronization.**

The UI instead offers an explicit **user-triggered, read-only** metadata lookup for the currently selected account's active Ethereum wallet and its actual Ethereum receiving address. The existing `IReceiveAdapter` derives the public address; `NftMetadataManager.addressMetadata(BlockchainType.Ethereum, address)` asks the existing OpenSea provider for its address-specific metadata. The user sees a consent notice before any lookup.

This is **provider-reported metadata, not on-chain cryptographic proof of current NFT ownership**. If no Ethereum wallet, adapter, provider response, or NFTs are available, there is an honest, actionable empty/error state; no placeholder collectible is presented as owned.

## Actual source changes in the SAME PR #7

- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/nft/NftGalleryPage.kt`
  - Native Compose `@Serializable NftGalleryPage`, three responsive columns with `LazyVerticalGrid(GridCells.Fixed(3))`, 10dp horizontal / 11dp vertical spacing, 9dp rounding, dark background, centered title/back arrow and screen-specific bottom navigation.
  - Real provider URLs are displayed only for actual returned NFT metadata, with a generic local placeholder icon for unavailable/blocked images.
  - Tap an asset to inspect metadata including collection and token ID; no invented Send/Transfer or sale controls.
  - Explicit Load NFTs / Retry button and corresponding error/empty/loading/account-switch states.
  - Working Home/Markets/History/Settings navigation uses `MainModule.viewModel` / existing navigation; NFT tab is selected.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/nft/NftGalleryViewModel.kt`
  - Does **not** trigger an external NFT lookup until the user explicitly taps Load NFTs.
  - Uses the real active account and actually enabled Ethereum wallet to obtain its receive address through `App.adapterManager.getReceiveAdapterForWallet(wallet)`, then queries the existing provider on `Dispatchers.IO`.
  - Refuses to show data if the user changed the active account during the network request; page also observes account changes and resets account-scoped displayed information.
  - Dedupe by `nftUid.uid` and preserve actual provider collection names. No wallet secrets or recovery phrases are sent.
  - `NftGallerySafety.safeRemoteImage` only permits HTTPS public URLs without credentials, blocking local/private addresses and other unsafe URI schemes. Remote image providers may still see the public address's activity and should be considered part of the opt-in.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/ui/BalanceItems.kt`:
  - The real Home/Wallet **NFTs** selector opens `NftGalleryPage`, replacing the previous inert `NFT unavailable` placeholder while leaving Tokens as the home list.
- `walletkit/src/main/java/io/horizontalsystems/walletkit/modules/balance/ui/VeldigoHomeComponents.kt`: removed the obsolete dead placeholder composable.
- `walletkit/src/main/res/values/veldigo_welcome_strings.xml`: labels, privacy notice, network and supported-network disclosures.
- `walletkit/src/test/java/io/horizontalsystems/walletkit/modules/nft/NftGallerySafetyTest.kt`: account isolation, blank/unsafe URL schemes, local/private network addresses and safe HTTPS input.
- `.github/workflows/android-verify.yml`: mandatory JUnit step `NftGallerySafetyTest`.

## Security / privacy invariants

1. Never show the concept's sample NFT artwork as assets actually held by a real wallet.
2. Never derive NFT holdings from a different active account or a stale public address.
3. Public Ethereum address is shared with provider **only after an explicit user click**.
4. No automatic re-enabling of disabled NFT adapters, transfer signing, or unreviewed background ownership synchronization.
5. Neither NFT images nor other JSON/provider metadata are trusted security evidence. User-facing view is read-only.
6. No user signing/private key, mnemonic, PIN or wallet secret leaves the wallet for NFT discovery.
7. External image downloads are HTTPS-only with basic rejection of localhost and private network hosts, but full redirect, DNS rebinding and metadata ownership validation require additional security testing.

## Functional and visual acceptance

| Gate | Status at authoring |
|---|---|
| Correct reference panel 17 inspected | YES |
| New screen source created and Home selector connected | YES |
| Three-column rounded image grid and matching navigation authored | YES |
| Actual active-account Ethereum address passed to existing provider | YES — source level |
| Explicit consent for provider address request | YES — source level |
| Cross-account result rejection and HTTPS image guard | YES — source level |
| Unsupported / empty / loading / provider failure states | YES — source level |
| New JUnit tests and CI step | COMMITTED, awaiting execution |
| Android Gradle compilation for this HEAD | NOT YET CONFIRMED |
| OpenSea API response with real assets on an Android device | NOT TESTED |
| On-chain NFT ownership and ERC-721/1155 reconciliation | NOT IMPLEMENTED |
| NFT transfer, buying and selling | NOT IMPLEMENTED — intentionally |
| Native density screenshot + reference pixel overlay | NOT PERFORMED |
| **Pixel-perfect 1:1** | **NOT VERIFIED** |
| Production real-assets release | **NOT APPROVED** |

## Next screen

Identify panel **18** against the user's original reference before continuing. Maintain the existing PR #7 until requested otherwise. No generated images are shared in chat.
