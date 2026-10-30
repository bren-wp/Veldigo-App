package io.horizontalsystems.walletkit.modules.evmnetwork

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.entities.EvmSyncSource
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.evmnetwork.addrpc.AddRpcScreen
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.marketkit.models.Blockchain
import kotlinx.serialization.Serializable

@Serializable
data class EvmNetworkPage(val blockchain: Blockchain) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        EvmNetworkScreen(navigation, blockchain)
    }
}

@Serializable
data class AddRpcPage(val blockchain: Blockchain) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        AddRpcScreen(navigation = navigation, blockchain = blockchain)
    }
}

/**
 * Veldigo screen 31. The existing EvmSyncSourceManager remains the only
 * authority for selected and available RPC sources. No fake connection state
 * and no credential-bearing URL is rendered on this screen.
 */
@Composable
private fun EvmNetworkScreen(
    navigation: HSNavigation,
    blockchain: Blockchain,
    viewModel: EvmNetworkViewModel = viewModel(factory = EvmNetworkModule.Factory(blockchain)),
) {
    var pendingDeletion by remember { mutableStateOf<EvmNetworkViewModel.ViewItem?>(null) }
    val state = viewModel.viewState

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
                .padding(start = 8.dp, end = 56.dp),
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
                text = stringResource(R.string.Veldigo_EvmRpc_Title),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            VeldigoRpcHeader(viewModel.title)
            Text(
                text = stringResource(R.string.Veldigo_EvmRpc_Description),
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 13.dp),
            )

            VeldigoRpcSectionTitle(R.string.Veldigo_EvmRpc_Default)
            VeldigoRpcGroup {
                state.defaultItems.forEachIndexed { index, item ->
                    if (index > 0) VeldigoRpcDivider()
                    VeldigoRpcRow(
                        item = item,
                        onSelect = {
                            viewModel.onSelectSyncSource(item.syncSource)
                            stat(
                                page = StatPage.BlockchainSettingsEvm,
                                event = StatEvent.SwitchEvmSource(blockchain.uid, item.name),
                            )
                        },
                    )
                }
                if (state.defaultItems.isEmpty()) {
                    Text(
                        text = stringResource(R.string.Veldigo_EvmRpc_NoDefault),
                        modifier = Modifier.padding(16.dp),
                        color = VeldigoHomePalette.Muted,
                        fontSize = 13.sp,
                    )
                }
            }

            VeldigoRpcSectionTitle(R.string.Veldigo_EvmRpc_Custom)
            VeldigoRpcGroup {
                state.customItems.forEachIndexed { index, item ->
                    if (index > 0) VeldigoRpcDivider()
                    VeldigoRpcRow(
                        item = item,
                        isCustom = true,
                        onSelect = {
                            viewModel.onSelectSyncSource(item.syncSource)
                            stat(
                                page = StatPage.BlockchainSettingsEvm,
                                event = StatEvent.SwitchEvmSource(blockchain.uid, "custom"),
                            )
                        },
                        onRequestDelete = { pendingDeletion = item },
                    )
                }

                if (state.customItems.isEmpty()) {
                    Text(
                        text = stringResource(R.string.Veldigo_EvmRpc_NoCustom),
                        color = VeldigoHomePalette.Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 16.dp),
                    )
                    VeldigoRpcDivider()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .clickable(role = Role.Button) {
                            navigation.add(AddRpcPage(blockchain))
                            stat(
                                page = StatPage.BlockchainSettingsEvm,
                                event = StatEvent.OpenBlockchainSettingsEvmAdd(blockchain.uid),
                            )
                        }
                        .padding(horizontal = 15.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = null,
                        tint = VeldigoHomePalette.Mint,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(R.string.EvmNetwork_AddNew),
                        color = VeldigoHomePalette.Mint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = VeldigoHomePalette.Muted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 24.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    tint = VeldigoHomePalette.Mint,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.Veldigo_EvmRpc_Safety),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }

    pendingDeletion?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDeletion = null },
            title = { Text(stringResource(R.string.Veldigo_EvmRpc_DeleteTitle)) },
            text = {
                Text(stringResource(R.string.Veldigo_EvmRpc_DeleteMessage, item.name))
            },
            confirmButton = {
                TextButton(onClick = {
                    // Revalidate the current state after any account/provider
                    // update; never delete the currently selected source.
                    val latestItem = viewModel.viewState.customItems.firstOrNull { it.id == item.id }
                    if (latestItem != null && RpcEndpointDisplayRules.canDelete(true, latestItem.selected)) {
                        viewModel.onRemoveCustomRpc(latestItem.syncSource)
                        stat(
                            page = StatPage.BlockchainSettingsEvm,
                            event = StatEvent.DeleteCustomEvmSource(blockchain.uid),
                        )
                    }
                    pendingDeletion = null
                }) {
                    Text(
                        text = stringResource(R.string.Button_Delete),
                        color = Color(0xFFFFB38A),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion = null }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }
}

@Composable
private fun VeldigoRpcHeader(chainName: String) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.globe_24),
                tint = VeldigoHomePalette.Mint,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chainName,
                color = VeldigoHomePalette.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 2,
            )
            Text(
                text = stringResource(R.string.Veldigo_EvmRpc_SourceTitle),
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun VeldigoRpcSectionTitle(title: Int) {
    Text(
        text = stringResource(title),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = VeldigoHomePalette.Muted,
        modifier = Modifier.padding(start = 4.dp, top = 23.dp, bottom = 10.dp),
    )
}

@Composable
private fun VeldigoRpcGroup(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape),
        content = content,
    )
}

@Composable
private fun VeldigoRpcDivider() {
    HorizontalDivider(
        color = VeldigoHomePalette.Border,
        thickness = 1.dp,
        modifier = Modifier.padding(start = 66.dp, end = 14.dp),
    )
}

@Composable
private fun VeldigoRpcRow(
    item: EvmNetworkViewModel.ViewItem,
    isCustom: Boolean = false,
    onSelect: () -> Unit,
    onRequestDelete: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .semantics { selected = item.selected }
            .clickable(role = Role.RadioButton, onClick = onSelect)
            .padding(start = 13.dp, end = 7.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.globe_24),
                tint = VeldigoHomePalette.Mint,
                contentDescription = null,
                modifier = Modifier.size(21.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name.ifBlank { stringResource(R.string.Veldigo_EvmRpc_Unnamed) },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                // Important: don't leak API keys from paths, query or userinfo.
                text = RpcEndpointDisplayRules.safeLabel(item.url),
                fontSize = 12.sp,
                color = VeldigoHomePalette.Muted,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (item.selected) {
            Icon(
                painter = painterResource(R.drawable.ic_checkmark_20),
                contentDescription = stringResource(R.string.Veldigo_EvmRpc_Selected),
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(20.dp),
            )
        }
        if (isCustom && onRequestDelete != null && RpcEndpointDisplayRules.canDelete(true, item.selected)) {
            IconButton(onClick = onRequestDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_circle_minus_24),
                    contentDescription = stringResource(R.string.Veldigo_EvmRpc_Remove),
                    tint = Color(0xFFFFB38A),
                    modifier = Modifier.size(22.dp),
                )
            }
        } else {
            Spacer(Modifier.size(8.dp))
        }
    }
}
