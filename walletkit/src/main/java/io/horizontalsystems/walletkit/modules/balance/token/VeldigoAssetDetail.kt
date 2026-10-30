package io.horizontalsystems.walletkit.modules.balance.token

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.entities.Wallet
import io.horizontalsystems.walletkit.modules.balance.BalanceViewItem
import io.horizontalsystems.walletkit.modules.balance.ui.IconCell
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.send.v2.SendV2Page
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat

/**
 * Token details are limited to real wallet holdings and native Send / Receive.
 * No market prices, chart provider calls, trading signals or NFT metadata.
 * Network details, sync warnings and real transaction history remain in
 * TokenBalanceScreen below this header.
 */
@Composable
internal fun VeldigoAssetDetail(
    item: BalanceViewItem,
    navigation: HSNavigation,
    onReceive: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp, vertical = 11.dp)
                .background(VeldigoHomePalette.Surface, RoundedCornerShape(11.dp))
                .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(11.dp))
                .padding(14.dp),
        ) {
            Text(
                text = stringResource(R.string.Veldigo_Asset_YourBalance),
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = if (item.balanceHidden) "••••"
                    else item.primaryValue?.value ?: stringResource(R.string.Veldigo_Asset_NoBalance),
                color = VeldigoHomePalette.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (!item.balanceHidden) {
                item.secondaryValue?.value?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = VeldigoHomePalette.Muted, fontSize = 12.sp)
                }
            }
            item.syncingTextValue?.let {
                Spacer(Modifier.height(5.dp))
                Text(it, color = VeldigoHomePalette.Muted, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Button(
                    onClick = {
                        navigation.slideFromRight(SendV2Page(item.wallet))
                        stat(page = StatPage.TokenPage, event = StatEvent.OpenSend(item.wallet.token))
                    },
                    enabled = !item.isWatchAccount,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeldigoHomePalette.Mint,
                        contentColor = VeldigoHomePalette.Background,
                    ),
                ) {
                    Text(stringResource(R.string.Balance_Send), fontSize = 13.sp)
                }
                Button(
                    onClick = onReceive,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeldigoHomePalette.Mint,
                        contentColor = VeldigoHomePalette.Background,
                    ),
                ) {
                    Text(stringResource(R.string.Balance_Receive), fontSize = 13.sp)
                }
            }
        }
    }
}

/** Chain-specific coin name without a market chart header. */
@Composable
internal fun VeldigoAssetTopBar(wallet: Wallet, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(53.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.arrow_m_left_24),
                contentDescription = stringResource(R.string.Button_Back),
                tint = VeldigoHomePalette.White,
            )
        }
        Row(
            modifier = Modifier.weight(1f).padding(end = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconCell(
                failedIconVisible = false,
                token = wallet.token,
                iconAlpha = 1f,
                onClickSyncError = null,
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "${wallet.coin.name} (${wallet.coin.code})",
                color = VeldigoHomePalette.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
