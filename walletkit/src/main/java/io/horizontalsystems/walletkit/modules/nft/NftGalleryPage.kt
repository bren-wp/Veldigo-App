package io.horizontalsystems.walletkit.modules.nft

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.entities.nft.NftAssetShortMetadata
import io.horizontalsystems.walletkit.modules.main.MainModule
import io.horizontalsystems.walletkit.modules.main.MainModule.MainNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.transactions.TransactionsPage
import kotlinx.serialization.Serializable

private val midnight = Color(0xFF030D0E)
private val mint = Color(0xFF41E6C7)
private val muted = Color(0xFF9FB5B8)
private val card = Color(0xFF142525)

@Serializable
data object NftGalleryPage : HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val model: NftGalleryViewModel = viewModel()
        val state by model.state.collectAsStateWithLifecycle()
        val account = App.accountManager.activeAccount?.id
        val activity = LocalActivity.current as ComponentActivity
        val mainModel = MainModule.viewModel(activity)
        var selected by remember { mutableStateOf<NftAssetShortMetadata?>(null) }

        LaunchedEffect(account) {
            model.clearOnAccountChange()
            selected = null
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(midnight)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(61.dp),
            ) {
                IconButton(
                    onClick = navigation::removeLastOrNull,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 11.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_m_left_24),
                        contentDescription = stringResource(R.string.Button_Back),
                        tint = Color.White,
                    )
                }
                Text(
                    text = stringResource(R.string.Veldigo_Nft_Title),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when (val s = state) {
                    NftGalleryViewModel.State.Idle -> NftGalleryMessage(
                        body = stringResource(R.string.Veldigo_Nft_Consent),
                        button = stringResource(R.string.Veldigo_Nft_Load),
                        onClick = model::load,
                    )
                    NftGalleryViewModel.State.Loading -> CircularProgressIndicator(color = mint)
                    NftGalleryViewModel.State.Unsupported -> NftGalleryMessage(
                        body = stringResource(R.string.Veldigo_Nft_EthereumOnly),
                        button = stringResource(R.string.Veldigo_Nft_Refresh),
                        onClick = model::load,
                    )
                    NftGalleryViewModel.State.WalletNotReady -> NftGalleryMessage(
                        body = stringResource(R.string.Veldigo_Nft_WalletNotReady),
                        button = stringResource(R.string.Veldigo_Nft_Refresh),
                        onClick = model::load,
                    )
                    NftGalleryViewModel.State.Failed -> NftGalleryMessage(
                        body = stringResource(R.string.Veldigo_Nft_ProviderFailed),
                        button = stringResource(R.string.Veldigo_Nft_Refresh),
                        onClick = model::load,
                    )
                    is NftGalleryViewModel.State.Ready -> {
                        if (!NftGallerySafety.isSameAccount(s.accountId, account)) {
                            NftGalleryMessage(
                                body = stringResource(R.string.Veldigo_Nft_AccountChanged),
                                button = stringResource(R.string.Veldigo_Nft_Refresh),
                                onClick = model::load,
                            )
                        } else if (s.assets.isEmpty()) {
                            NftGalleryMessage(
                                body = stringResource(R.string.Veldigo_Nft_Empty),
                                button = stringResource(R.string.Veldigo_Nft_Refresh),
                                onClick = model::load,
                            )
                        } else {
                            val gridState = rememberLazyGridState()
                            Column {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    state = gridState,
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(11.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        start = 17.dp, end = 17.dp, top = 15.dp, bottom = 19.dp,
                                    ),
                                ) {
                                    items(s.assets, key = { it.nftUid.uid }) { asset ->
                                        val label = s.collectionNames[asset.providerCollectionUid]
                                        NftGalleryTile(asset, label) { selected = asset }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state is NftGalleryViewModel.State.Ready) {
                Text(
                    text = stringResource(R.string.Veldigo_Nft_ProviderDisclaimer),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 5.dp),
                    color = muted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }

            NftGalleryBottomBar(
                onHome = {
                    mainModel.onSelect(MainNavigation.Balance)
                    navigation.removeLastOrNull()
                },
                onMarkets = {
                    mainModel.onSelect(MainNavigation.Market)
                    navigation.removeLastOrNull()
                },
                onHistory = { navigation.slideFromRight(TransactionsPage) },
                onSettings = {
                    mainModel.onSelect(MainNavigation.Settings)
                    navigation.removeLastOrNull()
                },
            )
        }

        selected?.let { asset ->
            val meta = (state as? NftGalleryViewModel.State.Ready)
            val collection = meta?.collectionNames?.get(asset.providerCollectionUid)
            AlertDialog(
                onDismissRequest = { selected = null },
                title = {
                    Text(asset.displayName, maxLines = 2, overflow = TextOverflow.Ellipsis)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        collection?.let { Text(it, color = muted) }
                        Text(
                            text = stringResource(
                                R.string.Veldigo_Nft_TokenId,
                                asset.nftUid.tokenId
                            ),
                        )
                        Text(stringResource(R.string.Veldigo_Nft_ProviderDisclaimer))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selected = null }) {
                        Text(stringResource(R.string.Button_Ok))
                    }
                },
            )
        }
    }
}

@Composable
private fun NftGalleryTile(
    asset: NftAssetShortMetadata,
    collection: String?,
    onClick: () -> Unit,
) {
    val url = NftGallerySafety.safeRemoteImage(asset.previewImageUrl)
    val description = listOfNotNull(asset.displayName, collection).joinToString(", ")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.91f)
            .clip(RoundedCornerShape(9.dp))
            .background(card)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = url,
                    error = painterResource(R.drawable.icon_24_nft_placeholder),
                    placeholder = painterResource(R.drawable.icon_24_nft_placeholder),
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.icon_24_nft_placeholder),
                contentDescription = null,
                tint = muted,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Composable
private fun NftGalleryMessage(body: String, button: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_24_nft_placeholder),
            contentDescription = null,
            tint = mint,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = body,
            color = muted,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(11.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mint, contentColor = midnight),
        ) {
            Text(button, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NftGalleryBottomBar(
    onHome: () -> Unit,
    onMarkets: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color(0xFF071515)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NftGalleryNavItem(
            icon = R.drawable.ic_wallet_24,
            title = stringResource(R.string.Veldigo_Home_Home),
            selected = false,
            onClick = onHome,
        )
        NftGalleryNavItem(
            icon = R.drawable.ic_market_24,
            title = stringResource(R.string.Veldigo_Home_Markets),
            selected = false,
            onClick = onMarkets,
        )
        NftGalleryNavItem(
            icon = R.drawable.icon_24_nft_placeholder,
            title = stringResource(R.string.Veldigo_Nft_Short),
            selected = true,
            onClick = {},
        )
        NftGalleryNavItem(
            icon = R.drawable.ic_circle_clock_24,
            title = stringResource(R.string.Veldigo_Home_History),
            selected = false,
            onClick = onHistory,
        )
        NftGalleryNavItem(
            icon = R.drawable.ic_settings_24,
            title = stringResource(R.string.Settings_Title),
            selected = false,
            onClick = onSettings,
        )
    }
}

@Composable
private fun NftGalleryNavItem(
    icon: Int,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(67.dp)
            .clickable(enabled = !selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = if (selected) mint else muted,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = title,
            color = if (selected) mint else muted,
            fontSize = 10.sp,
            maxLines = 1,
        )
    }
}
