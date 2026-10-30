package io.horizontalsystems.walletkit.modules.balance.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.modules.balance.BalanceViewItem2
import io.horizontalsystems.walletkit.modules.balance.TotalUIState
import io.horizontalsystems.walletkit.ui.compose.components.diffText

/** Home reference panel 10: UI is derived exclusively from live wallet and price services. */
internal object VeldigoHomePalette {
    val Background = Color(0xFF030E0D)
    val Mint = Color(0xFF41E6C7)
    val White = Color(0xFFF5F8F7)
    val Muted = Color(0xFF98B4B3)
    val Surface = Color(0xFF152322)
    val Border = Color(0xFF28504A)
}

@Composable
internal fun VeldigoHomeTopBar(
    onWallets: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onWallets),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_veldigo_welcome_mark),
                contentDescription = null,
                modifier = Modifier.size(31.dp),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "Veldigo",
                color = VeldigoHomePalette.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5f).sp,
            )
        }
        IconButton(onClick = onSettings) {
            Icon(
                painter = painterResource(R.drawable.ic_settings_24),
                contentDescription = stringResource(R.string.Settings_Title),
                tint = VeldigoHomePalette.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
internal fun VeldigoTotalBalance(
    state: TotalUIState,
    hidden: Boolean,
    onToggleHide: () -> Unit,
    onToggleCurrency: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 13.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.Veldigo_Home_TotalBalance),
            color = VeldigoHomePalette.Muted,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (hidden) "••••••" else state.primaryAmountStr,
            modifier = Modifier.clickable(role = Role.Button, onClick = onToggleHide),
            color = VeldigoHomePalette.White,
            fontSize = 33.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (hidden) stringResource(R.string.Veldigo_Home_Hidden)
                else state.secondaryAmountStr,
            modifier = Modifier.clickable(role = Role.Button, onClick = onToggleCurrency),
            color = VeldigoHomePalette.Mint,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        // Do not display a fabricated 24-hour portfolio gain from the concept art.
    }
}

@Composable
internal fun VeldigoHomeActions(
    sendEnabled: Boolean,
    swapEnabled: Boolean,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onSwap: () -> Unit,
    onBuy: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 19.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        VeldigoAction(
            title = stringResource(R.string.Balance_Send),
            icon = R.drawable.ic_arrow_up_24,
            enabled = sendEnabled,
            onClick = onSend,
        )
        VeldigoAction(
            title = stringResource(R.string.Balance_Receive),
            icon = R.drawable.ic_arrow_down_24,
            enabled = true,
            onClick = onReceive,
        )
        VeldigoAction(
            title = stringResource(R.string.Swap),
            icon = R.drawable.ic_swap_circle_24,
            enabled = swapEnabled,
            onClick = onSwap,
        )
        VeldigoAction(
            title = stringResource(R.string.Veldigo_Home_Buy),
            icon = R.drawable.ic_wallet_24,
            enabled = true,
            onClick = onBuy,
        )
    }
}

@Composable
private fun VeldigoAction(
    title: String,
    icon: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(VeldigoHomePalette.Surface)
                .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(17.dp))
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                tint = if (enabled) VeldigoHomePalette.Mint else VeldigoHomePalette.Muted,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            color = if (enabled) VeldigoHomePalette.White else VeldigoHomePalette.Muted,
            fontSize = 12.sp,
        )
    }
}

/**
 * Tabs do not fake NFTs. No holdings/metadata are invented when the NFT
 * data source is not wired to the portfolio view.
 */
@Composable
internal fun VeldigoAssetTabs(selectedNfts: Boolean, onSelectNfts: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 17.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border.copy(alpha = .55f), RoundedCornerShape(11.dp)),
    ) {
        listOf(false to R.string.Veldigo_Home_Tokens, true to R.string.Veldigo_Home_Nfts)
            .forEach { (isNft, title) ->
                val selected = selectedNfts == isNft
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .then(
                            if (selected) Modifier.border(
                                width = 1.5.dp,
                                color = VeldigoHomePalette.Mint,
                                shape = RoundedCornerShape(10.dp),
                            ) else Modifier
                        )
                        .clickable(role = Role.Tab, onClick = { onSelectNfts(isNft) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(title),
                        color = if (selected) VeldigoHomePalette.Mint else VeldigoHomePalette.White,
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
    }
}

@Composable
internal fun VeldigoNftUnavailable() {
    Text(
        text = stringResource(R.string.Veldigo_Home_NftUnavailable),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 30.dp),
        color = VeldigoHomePalette.Muted,
        textAlign = TextAlign.Center,
        lineHeight = 21.sp,
        fontSize = 14.sp,
    )
}

@Composable
internal fun VeldigoAssetRow(
    item: BalanceViewItem2,
    hidden: Boolean,
    onClick: () -> Unit,
    onSyncError: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconCell(
            failedIconVisible = item.failedIconVisible,
            token = item.wallet.token,
            iconAlpha = 1f,
            onClickSyncError = onSyncError,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.wallet.coin.name,
                color = VeldigoHomePalette.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.wallet.coin.code,
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (hidden) "••••" else item.primaryValue?.value ?: "—",
                color = VeldigoHomePalette.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (hidden) "••••" else item.secondaryValue?.value ?: "—",
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
            )
        }
        val change = if (hidden) null else item.diff?.value
        if (change != null) {
            Spacer(Modifier.width(10.dp))
            Text(
                text = diffText(change),
                color = if (change.signum() >= 0) VeldigoHomePalette.Mint else Color(0xFFED8A8A),
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}
