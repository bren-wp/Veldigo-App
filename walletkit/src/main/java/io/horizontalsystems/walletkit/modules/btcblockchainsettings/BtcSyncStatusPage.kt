package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.btcblockchainsettings.BtcSyncStatusRules.Phase
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.serialization.Serializable

/** Protected, live status screen. No wallet addresses, seeds or RPC tokens. */
@Serializable
data class BtcSyncStatusPage(val blockchain: Blockchain) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val model = viewModel<BtcSyncStatusViewModel>(factory = BtcSyncStatusViewModel.Factory(blockchain))
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { model.updateSnapshot() }
        BtcSyncStatusScreen(navigation, model)
    }
}

@Composable
private fun BtcSyncStatusScreen(
    navigation: HSNavigation,
    model: BtcSyncStatusViewModel,
) {
    val summary = model.summary
    val active = when (summary.phase) {
        Phase.Connecting, Phase.Syncing, Phase.SearchingTransactions,
        Phase.WaitingForAdapter, Phase.WaitingForTor -> true
        else -> false
    }
    val accent = when (summary.phase) {
        Phase.Failed -> Color(0xFFFFB38A)
        Phase.Synced -> VeldigoHomePalette.Mint
        else -> VeldigoHomePalette.Muted
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
                .padding(start = 8.dp, end = 48.dp),
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
                text = stringResource(R.string.Veldigo_BtcStatus_Title),
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            val shape = RoundedCornerShape(18.dp)
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(shape)
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, shape)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Box(
                    modifier = Modifier.size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF103C3B)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.globe_24),
                        contentDescription = null,
                        tint = VeldigoHomePalette.Mint,
                        modifier = Modifier.size(25.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.blockchain.name,
                        color = VeldigoHomePalette.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 2,
                    )
                    Text(
                        text = stringResource(R.string.Veldigo_BtcStatus_NetworkCaption),
                        color = VeldigoHomePalette.Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            val statusShape = RoundedCornerShape(18.dp)
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                    .clip(statusShape)
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, statusShape)
                    .padding(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (active) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = VeldigoHomePalette.Mint,
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                if (summary.phase == Phase.Synced) R.drawable.ic_checkmark_20
                                else R.drawable.ic_shield_24,
                            ),
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Text(
                        text = stringResource(syncPhaseLabel(summary.phase)),
                        color = if (active) VeldigoHomePalette.White else accent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = stringResource(syncPhaseDescription(summary.phase)),
                    modifier = Modifier.padding(top = 11.dp),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
                summary.progress?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                        color = VeldigoHomePalette.Mint,
                        trackColor = VeldigoHomePalette.Border,
                    )
                    Text(
                        text = stringResource(R.string.Veldigo_BtcStatus_Progress, progress),
                        modifier = Modifier.padding(top = 7.dp),
                        color = VeldigoHomePalette.Mint,
                        fontSize = 12.sp,
                    )
                }
            }

            Text(
                text = stringResource(R.string.Veldigo_BtcStatus_Details),
                color = VeldigoHomePalette.Muted,
                modifier = Modifier.padding(start = 4.dp, top = 24.dp, bottom = 10.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(16.dp)),
            ) {
                BtcStatusDetailRow(
                    label = stringResource(R.string.Veldigo_BtcStatus_Wallets),
                    value = summary.wallets.toString(),
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    thickness = 1.dp,
                    color = VeldigoHomePalette.Border,
                )
                BtcStatusDetailRow(
                    label = stringResource(R.string.Veldigo_BtcStatus_Adapters),
                    value = stringResource(R.string.Veldigo_BtcStatus_CountOf, summary.adapters, summary.wallets),
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    thickness = 1.dp,
                    color = VeldigoHomePalette.Border,
                )
                BtcStatusDetailRow(
                    label = stringResource(R.string.Veldigo_BtcStatus_SyncedWallets),
                    value = stringResource(R.string.Veldigo_BtcStatus_CountOf, summary.synced, summary.wallets),
                )
                summary.remainingBlocks?.let { blocks ->
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 1.dp,
                        color = VeldigoHomePalette.Border,
                    )
                    BtcStatusDetailRow(
                        label = stringResource(R.string.Veldigo_BtcStatus_BlocksRemaining),
                        value = blocks.toString(),
                    )
                }
                summary.transactionsFound?.let { count ->
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 1.dp,
                        color = VeldigoHomePalette.Border,
                    )
                    BtcStatusDetailRow(
                        label = stringResource(R.string.Veldigo_BtcStatus_SearchingCount),
                        value = count.toString(),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 25.dp, bottom = 26.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    contentDescription = null,
                    tint = VeldigoHomePalette.Mint,
                    modifier = Modifier.size(21.dp),
                )
                Text(
                    text = stringResource(R.string.Veldigo_BtcStatus_PrivacyNote),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
        Button(
            onClick = model::refresh,
            enabled = model.canRefresh,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .heightIn(min = 54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VeldigoHomePalette.Mint,
                contentColor = VeldigoHomePalette.Background,
                disabledContainerColor = VeldigoHomePalette.Border,
                disabledContentColor = VeldigoHomePalette.Muted,
            ),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.Veldigo_BtcStatus_Refresh),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun BtcStatusDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)
            .padding(horizontal = 15.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = VeldigoHomePalette.Muted,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = VeldigoHomePalette.White,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
    }
}

private fun syncPhaseLabel(phase: Phase): Int = when (phase) {
    Phase.NoWallet -> R.string.Veldigo_BtcStatus_NoWallet
    Phase.WaitingForAdapter -> R.string.Veldigo_BtcStatus_Starting
    Phase.WaitingForTor -> R.string.Veldigo_BtcStatus_WaitingTor
    Phase.Connecting -> R.string.Veldigo_BtcStatus_Connecting
    Phase.Syncing -> R.string.Veldigo_BtcStatus_Syncing
    Phase.SearchingTransactions -> R.string.Veldigo_BtcStatus_Searching
    Phase.Synced -> R.string.Veldigo_BtcStatus_Synced
    Phase.Failed -> R.string.Veldigo_BtcStatus_Failed
}

private fun syncPhaseDescription(phase: Phase): Int = when (phase) {
    Phase.NoWallet -> R.string.Veldigo_BtcStatus_NoWalletHint
    Phase.WaitingForAdapter -> R.string.Veldigo_BtcStatus_StartingHint
    Phase.WaitingForTor -> R.string.Veldigo_BtcStatus_WaitingTorHint
    Phase.Connecting -> R.string.Veldigo_BtcStatus_ConnectingHint
    Phase.Syncing -> R.string.Veldigo_BtcStatus_SyncingHint
    Phase.SearchingTransactions -> R.string.Veldigo_BtcStatus_SearchingHint
    Phase.Synced -> R.string.Veldigo_BtcStatus_SyncedHint
    Phase.Failed -> R.string.Veldigo_BtcStatus_FailedHint
}
