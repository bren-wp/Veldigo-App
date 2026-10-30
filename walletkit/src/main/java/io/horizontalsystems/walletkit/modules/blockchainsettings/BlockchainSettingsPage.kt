package io.horizontalsystems.walletkit.modules.blockchainsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.evmnetwork.EvmNetworkPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.serialization.Serializable

/**
 * Veldigo wallet-only Network Settings. Renders only actual chain settings
 * provided by ChainRegistry / EvmBlockchainManager, not mock network status.
 */
@Serializable
data object BlockchainSettingsPage : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        BlockchainSettingsScreen(navigation)
    }
}

@Composable
private fun BlockchainSettingsScreen(
    navigation: HSNavigation,
    viewModel: BlockchainSettingsViewModel = viewModel(factory = BlockchainSettingsModule.Factory()),
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val bitcoinLike = viewModel.btcLikeChains.filter {
        NetworkSettingsListRules.matches(it.title, it.subtitle, searchQuery)
    }
    val other = viewModel.otherChains.filter {
        NetworkSettingsListRules.matches(it.title, it.subtitle, searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = navigation::removeLastOrNull) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = VeldigoHomePalette.White,
                )
            }
            Text(
                text = stringResource(R.string.BlockchainSettings_Title),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.Button_Close),
                        tint = VeldigoHomePalette.Muted,
                    )
                }
            } else {
                Spacer(Modifier.size(48.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(VeldigoHomePalette.Surface)
                .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = VeldigoHomePalette.Muted,
            )
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = stringResource(R.string.Veldigo_Networks_Search),
                        fontSize = 14.sp,
                        color = VeldigoHomePalette.Muted,
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = NetworkSettingsListRules.normalizeQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    textStyle = TextStyle(color = VeldigoHomePalette.White, fontSize = 14.sp),
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            item {
                Text(
                    text = stringResource(R.string.Veldigo_Networks_Description),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 17.dp, bottom = 4.dp),
                )
            }

            if (bitcoinLike.isNotEmpty()) {
                item { VeldigoNetworkHeader(R.string.Veldigo_Networks_BtcLike, bitcoinLike.size) }
                item {
                    BlockchainSettingsBlockSection(
                        items = bitcoinLike,
                        navigation = navigation,
                    )
                }
            }

            if (other.isNotEmpty()) {
                item { VeldigoNetworkHeader(R.string.Veldigo_Networks_Other, other.size) }
                item {
                    BlockchainSettingsBlockSection(
                        items = other,
                        navigation = navigation,
                    )
                }
            }

            if (bitcoinLike.isEmpty() && other.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 76.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.globe_24),
                            contentDescription = null,
                            tint = VeldigoHomePalette.Muted,
                            modifier = Modifier.size(42.dp),
                        )
                        Text(
                            text = stringResource(
                                if (searchQuery.isNotBlank()) R.string.EmptyResults
                                else R.string.Veldigo_Networks_None,
                            ),
                            color = VeldigoHomePalette.Muted,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
            item { Spacer(Modifier.size(28.dp)) }
        }
    }
}

/**
 * Public legacy entry point remains for any existing callers; grouping uses
 * only chain items supplied by the active manager, never hard-coded networks.
 */
@Composable
fun BlockchainSettingsBlock(
    btcLikeChains: List<BlockchainSettingsModule.BlockchainViewItem>,
    otherChains: List<BlockchainSettingsModule.BlockchainViewItem>,
    navigation: HSNavigation,
) {
    if (btcLikeChains.isNotEmpty()) {
        VeldigoNetworkHeader(R.string.Veldigo_Networks_BtcLike, btcLikeChains.size)
        BlockchainSettingsBlockSection(btcLikeChains, navigation)
    }
    if (otherChains.isNotEmpty()) {
        VeldigoNetworkHeader(R.string.Veldigo_Networks_Other, otherChains.size)
        BlockchainSettingsBlockSection(otherChains, navigation)
    }
}

@Composable
private fun VeldigoNetworkHeader(@androidx.annotation.StringRes title: Int, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, end = 20.dp, top = 23.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(title),
            color = VeldigoHomePalette.Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count.toString(),
            color = VeldigoHomePalette.Mint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun BlockchainSettingsBlockSection(
    items: List<BlockchainSettingsModule.BlockchainViewItem>,
    navigation: HSNavigation,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                androidx.compose.material3.HorizontalDivider(
                    color = VeldigoHomePalette.Border,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 69.dp, end = 13.dp),
                )
            }
            BlockchainSettingCell(item) { onClick(item, navigation) }
        }
    }
}

private fun onClick(
    item: BlockchainSettingsModule.BlockchainViewItem,
    navigation: HSNavigation,
) {
    when (val chainItem = item.blockchainItem) {
        is BlockchainSettingsModule.BlockchainItem.Evm -> {
            navigation.slideFromBottom(EvmNetworkPage(chainItem.blockchain))
            stat(
                page = StatPage.BlockchainSettings,
                event = StatEvent.OpenBlockchainSettingsEvm(chainItem.blockchain.uid),
            )
        }
        is BlockchainSettingsModule.BlockchainItem.Chain -> {
            navigation.slideFromBottom(chainItem.page)
            stat(
                page = StatPage.BlockchainSettings,
                event = chainItem.statEvent,
            )
        }
    }
}

@Composable
private fun BlockchainSettingCell(
    item: BlockchainSettingsModule.BlockchainViewItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 13.dp, end = 13.dp, top = 13.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.globe_24),
                contentDescription = null,
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(23.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = VeldigoHomePalette.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.subtitle,
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = VeldigoHomePalette.Muted,
            modifier = Modifier.size(18.dp),
        )
    }
}
