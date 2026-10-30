package io.horizontalsystems.walletkit.modules.moneronetwork

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Shape
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
import io.horizontalsystems.walletkit.core.managers.MoneroNodeManager.MoneroNode
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.uiv3.components.PingState
import kotlinx.serialization.Serializable

/** Local chain-module mirror of the wallet's Midnight Navy/Mint palette. */
private object MoneroVeldigoPalette {
    val Background = Color(0xFF071421)
    val Surface = Color(0xFF112337)
    val Border = Color(0xFF234254)
    val Mint = Color(0xFF41E6C7)
    val White = Color.White
    val Muted = Color(0xFFB5C6D7)
    val Warning = Color(0xFFFFB38A)
}

/**
 * Screen 35 — real Monero node settings. Built-in/custom nodes, measured ping,
 * automatic selection and trust settings come from MoneroNodeManager.
 */
@Serializable
data object MoneroNetworkPage : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        MoneroNetworkScreen(navigation)
    }
}

@Serializable
data object AddNodePage : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        io.horizontalsystems.walletkit.modules.moneronetwork.addnode.AddMoneroNodeScreen(
            navigation = navigation,
        )
    }
}

@Composable
private fun MoneroNetworkScreen(navigation: HSNavigation) {
    val model = viewModel<MoneroNetworkViewModel>(factory = MoneroNetworkModule.Factory())
    val state = model.uiState
    var trustNode by remember { mutableStateOf<MoneroNode?>(null) }
    var pendingRemoval by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MoneroVeldigoPalette.Background)
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
                    tint = MoneroVeldigoPalette.White,
                    contentDescription = stringResource(R.string.Button_Back),
                )
            }
            Text(
                text = stringResource(R.string.Veldigo_MoneroNodes_Title),
                fontWeight = FontWeight.SemiBold,
                color = MoneroVeldigoPalette.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                maxLines = 2,
            )
            IconButton(onClick = model::refresh) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    tint = MoneroVeldigoPalette.Mint,
                    contentDescription = stringResource(R.string.Button_Refresh),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 28.dp,
            ),
        ) {
            item {
                VeldigoMoneroHeader()
                Text(
                    text = stringResource(R.string.MoneroNodeSettings_Description),
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 15.dp, bottom = 7.dp),
                    color = MoneroVeldigoPalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
            item {
                MoneroSectionTitle(R.string.Veldigo_MoneroNodes_Connection)
                MoneroCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp)
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            tint = MoneroVeldigoPalette.Mint,
                            contentDescription = null,
                            modifier = Modifier.size(21.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.NetworkSettings_AutoSelect),
                                color = MoneroVeldigoPalette.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = stringResource(R.string.MoneroNodeSettings_AutoSelectDescription),
                                color = MoneroVeldigoPalette.Muted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                        Switch(
                            checked = state.autoSelectEnabled,
                            onCheckedChange = model::onToggleAutoSelect,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MoneroVeldigoPalette.Mint,
                                checkedThumbColor = MoneroVeldigoPalette.Background,
                                uncheckedTrackColor = MoneroVeldigoPalette.Border,
                                uncheckedThumbColor = MoneroVeldigoPalette.Muted,
                            ),
                        )
                    }
                }
                if (state.autoSelectEnabled) {
                    Text(
                        text = stringResource(R.string.Veldigo_MoneroNodes_AutoNote),
                        color = MoneroVeldigoPalette.Muted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp),
                    )
                }
            }

            item {
                MoneroSectionTitle(R.string.Veldigo_MoneroNodes_Default)
                MoneroCard {
                    state.defaultItems.forEachIndexed { index, node ->
                        if (index > 0) MoneroRowDivider()
                        MoneroNodeRow(
                            item = node,
                            enabled = !state.autoSelectEnabled,
                            onSelect = { trustNode = node.node },
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 5.dp, top = 23.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.Veldigo_MoneroNodes_Custom),
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MoneroVeldigoPalette.Muted,
                    )
                    Text(
                        text = state.customItems.size.toString(),
                        fontSize = 12.sp,
                        color = MoneroVeldigoPalette.Mint,
                    )
                }

                MoneroCard {
                    if (state.customItems.isEmpty()) {
                        Text(
                            text = stringResource(R.string.Veldigo_MoneroNodes_Empty),
                            color = MoneroVeldigoPalette.Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(15.dp),
                        )
                        MoneroRowDivider()
                    }
                    state.customItems.forEachIndexed { index, node ->
                        if (index > 0) MoneroRowDivider()
                        MoneroNodeRow(
                            item = node,
                            enabled = !state.autoSelectEnabled,
                            onSelect = { trustNode = node.node },
                            onRemove = if (MoneroNodeDisplayRules.canRemove(true, node.selected)) {
                                { pendingRemoval = node.id }
                            } else null,
                        )
                    }
                    MoneroRowDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 57.dp)
                            .clickable(role = Role.Button) { navigation.slideFromRight(AddNodePage) }
                            .padding(horizontal = 15.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_plus),
                            tint = MoneroVeldigoPalette.Mint,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = stringResource(R.string.EvmNetwork_AddNew),
                            color = MoneroVeldigoPalette.Mint,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 27.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield_24),
                        tint = MoneroVeldigoPalette.Mint,
                        contentDescription = null,
                        modifier = Modifier.size(21.dp),
                    )
                    Text(
                        text = stringResource(R.string.Veldigo_MoneroNodes_Privacy),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MoneroVeldigoPalette.Muted,
                    )
                }
            }
        }
    }

    trustNode?.let { node ->
        var trusted by remember(node.host) { mutableStateOf(node.trusted) }
        AlertDialog(
            onDismissRequest = { trustNode = null },
            title = { Text(stringResource(R.string.MoneroNodeSettings_TrustedTitle)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Text(stringResource(R.string.MoneroNodeSettings_TrustedDescription))
                    Text(
                        text = MoneroNodeDisplayRules.safeEndpoint(node.host),
                        fontSize = 13.sp,
                        color = MoneroVeldigoPalette.Muted,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.MoneroNodeSettings_Trusted),
                            modifier = Modifier.weight(1f),
                        )
                        Switch(checked = trusted, onCheckedChange = { trusted = it })
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    model.onSelectNode(node.copy(trusted = trusted))
                    trustNode = null
                }) { Text(stringResource(R.string.Button_Done)) }
            },
            dismissButton = {
                TextButton(onClick = { trustNode = null }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }

    pendingRemoval?.let { host ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text(stringResource(R.string.Veldigo_MoneroNodes_DeleteTitle)) },
            text = { Text(stringResource(R.string.Veldigo_MoneroNodes_DeleteWarning)) },
            confirmButton = {
                TextButton(onClick = {
                    val item = model.uiState.customItems.firstOrNull { it.id == host }
                    if (item != null && MoneroNodeDisplayRules.canRemove(true, item.selected)) {
                        model.onRemoveCustomNode(item.node)
                    }
                    pendingRemoval = null
                }) {
                    Text(
                        text = stringResource(R.string.Button_Delete),
                        color = MoneroVeldigoPalette.Warning,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }
}

@Composable
private fun VeldigoMoneroHeader() {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            .clip(shape).background(MoneroVeldigoPalette.Surface)
            .border(1.dp, MoneroVeldigoPalette.Border, shape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.globe_24),
                tint = MoneroVeldigoPalette.Mint,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.Veldigo_MoneroNodes_Header),
                color = MoneroVeldigoPalette.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.Veldigo_MoneroNodes_Subheader),
                color = MoneroVeldigoPalette.Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 5.dp),
            )
        }
    }
}

@Composable
private fun MoneroSectionTitle(id: Int) {
    Text(
        text = stringResource(id),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MoneroVeldigoPalette.Muted,
        modifier = Modifier.padding(start = 5.dp, top = 24.dp, bottom = 10.dp),
    )
}

@Composable
private fun MoneroCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier.fillMaxWidth().clip(shape)
            .background(MoneroVeldigoPalette.Surface)
            .border(1.dp, MoneroVeldigoPalette.Border, shape),
        content = content,
    )
}

@Composable
private fun MoneroRowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 55.dp, end = 12.dp),
        thickness = 1.dp,
        color = MoneroVeldigoPalette.Border,
    )
}

@Composable
private fun MoneroNodeRow(
    item: MoneroNetworkViewModel.ViewItem,
    enabled: Boolean,
    onSelect: () -> Unit,
    onRemove: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 73.dp)
            .semantics { selected = item.selected }
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onSelect)
            .padding(start = 13.dp, end = 9.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name.ifBlank {
                    stringResource(R.string.Veldigo_MoneroNodes_Unnamed)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) MoneroVeldigoPalette.White else MoneroVeldigoPalette.Muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = MoneroNodeDisplayRules.safeEndpoint(item.url),
                fontSize = 12.sp,
                color = MoneroVeldigoPalette.Muted,
                modifier = Modifier.padding(top = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        when (val ping = item.ping) {
            PingState.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(17.dp),
                color = MoneroVeldigoPalette.Mint,
                strokeWidth = 2.dp,
            )
            PingState.Unreachable -> Text(
                text = stringResource(R.string.NetworkSettings_Unreachable),
                color = MoneroVeldigoPalette.Warning,
                fontSize = 11.sp,
            )
            is PingState.Reachable -> Text(
                text = stringResource(R.string.NetworkSettings_Latency, ping.responseTimeMs),
                color = if (ping.level == PingState.Level.Good) MoneroVeldigoPalette.Mint
                    else MoneroVeldigoPalette.Muted,
                fontSize = 11.sp,
            )
        }
        if (item.selected) {
            Icon(
                painter = painterResource(R.drawable.ic_checkmark_20),
                contentDescription = stringResource(R.string.Veldigo_MoneroNodes_Selected),
                tint = MoneroVeldigoPalette.Mint,
                modifier = Modifier.size(19.dp),
            )
        }
        onRemove?.let {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(R.drawable.ic_circle_minus_24),
                    contentDescription = stringResource(R.string.Veldigo_MoneroNodes_Remove),
                    tint = MoneroVeldigoPalette.Warning,
                    modifier = Modifier.size(21.dp),
                )
            }
        }
    }
}
