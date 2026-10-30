package io.horizontalsystems.walletkit.modules.balance.token

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.chartview.models.ChartPoint
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.modules.balance.BalanceViewItem
import io.horizontalsystems.walletkit.modules.chart.ChartViewModel
import io.horizontalsystems.walletkit.modules.coin.overview.CoinOverviewModule
import io.horizontalsystems.walletkit.modules.coin.overview.CoinOverviewViewModel
import io.horizontalsystems.walletkit.modules.coin.CoinPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.receive.ReceivePage
import io.horizontalsystems.walletkit.modules.send.v2.SendV2Page
import io.horizontalsystems.walletkit.core.chain.ChainRegistry
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.marketkit.models.FullCoin
import io.horizontalsystems.marketkit.models.HsTimePeriod
import java.util.Locale

private val assetBg = Color(0xFF030D0E)
private val assetMint = Color(0xFF41E6C7)
private val assetMuted = Color(0xFF97B0B5)
private val assetCard = Color(0xFF152323)

@Composable
internal fun VeldigoAssetDetail(
    item: BalanceViewItem,
    navigation: HSNavigation,
    onReceive: () -> Unit,
) {
    val wallet = item.wallet
    val coinUid = wallet.coin.uid
    // MarketKit may not index custom or unsupported tokens. A missing coin
    // must not silently be replaced with BTC's graph or a manufactured price.
    val fullCoin: FullCoin? = remember(coinUid) {
        runCatching { App.marketKit.fullCoins(listOf(coinUid)).firstOrNull() }.getOrNull()
    }
    Column(Modifier.fillMaxWidth()) {
        if (fullCoin == null) {
            VeldigoMarketPanel(
                price = item.exchangeValue?.value,
                points = emptyList(),
                change = null,
                periods = emptyList(),
                onSelectPeriod = {},
            )
            VeldigoHoldingPanel(item, navigation, onReceive)
            VeldigoAboutPanel(wallet.coin.name, null) {
                navigation.slideFromRight(CoinPage(CoinPage.Input(coinUid)))
            }
        } else {
            val factory = remember(coinUid) { CoinOverviewModule.Factory(fullCoin) }
            val chart = viewModel<ChartViewModel>(key = "veldigo-asset-chart-$coinUid", factory = factory)
            val overview = viewModel<CoinOverviewViewModel>(key = "veldigo-asset-overview-$coinUid", factory = factory)
            val about by overview.overviewLiveData.observeAsState()
            val chartState = chart.uiState
            val graph = chartState.chartInfoData?.chartData
            val change = graph?.takeIf { it.items.size > 1 }?.diff()?.toFloat()
            VeldigoMarketPanel(
                price = chartState.chartHeaderView?.value ?: item.exchangeValue?.value,
                points = graph?.items.orEmpty(),
                change = change,
                periods = chartState.tabItems.map { p -> Triple(p.title, p.selected, p.item) },
                onSelectPeriod = chart::onSelectChartInterval,
            )
            VeldigoHoldingPanel(item, navigation, onReceive)
            VeldigoAboutPanel(wallet.coin.name, about?.about) {
                navigation.slideFromRight(CoinPage(CoinPage.Input(coinUid)))
            }
        }
    }
}

@Composable
private fun VeldigoMarketPanel(
    price: String?,
    points: List<ChartPoint>,
    change: Float?,
    periods: List<Triple<String, Boolean, HsTimePeriod?>>,
    onSelectPeriod: (HsTimePeriod?) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = price ?: stringResource(R.string.Veldigo_Asset_NoPrice),
            color = Color.White,
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = change?.let { String.format(Locale.US, "%+.2f%%", it) }
                ?: stringResource(R.string.Veldigo_Asset_NoChange),
            color = if (change == null) assetMuted
                else if (change >= 0) assetMint else Color(0xFFEC8585),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(10.dp))
        VeldigoRealPriceChart(points)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            periods.forEach { (title, selected, interval) ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .background(
                            if (selected) Color(0xFF12483D) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { onSelectPeriod(interval) })
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(title, color = if (selected) assetMint else assetMuted, fontSize = 11.sp)
                }
            }
            if (periods.isEmpty()) {
                Text(stringResource(R.string.Veldigo_Asset_ChartUnavailable), color = assetMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun VeldigoRealPriceChart(points: List<ChartPoint>) {
    val plotPoints = remember(points) { points.filter { it.value.isFinite() } }
    Box(Modifier.fillMaxWidth().height(180.dp)) {
        if (plotPoints.size < 2) {
            Text(
                text = stringResource(R.string.Veldigo_Asset_ChartUnavailable),
                modifier = Modifier.align(Alignment.Center),
                color = assetMuted,
                fontSize = 12.sp,
            )
            return@Box
        }
        Canvas(Modifier.fillMaxWidth().height(180.dp)) {
            val w = size.width
            val h = size.height
            val min = plotPoints.minOf { it.value }
            val max = plotPoints.maxOf { it.value }
            val range = (max - min).takeIf { it > 0.000001f } ?: 1f
            val minTime = plotPoints.first().timestamp
            val endTime = plotPoints.last().timestamp
            val duration = (endTime - minTime).coerceAtLeast(1L)
            for (j in 1..3) {
                val y = h * j / 4f
                drawLine(
                    Color(0xFF1B3532).copy(alpha = .5f),
                    Offset(0f, y), Offset(w, y), strokeWidth = 1.dp.toPx(),
                )
            }
            val shape = Path()
            val fillPath = Path()
            plotPoints.forEachIndexed { i, p ->
                val x = w * (p.timestamp - minTime).toFloat() / duration.toFloat()
                val y = (h * .86f) - (p.value - min) / range * (h * .71f)
                if (i == 0) {
                    shape.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    shape.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }
            fillPath.lineTo(w, h)
            fillPath.close()
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(listOf(assetMint.copy(alpha = .18f), Color.Transparent)),
            )
            drawPath(shape, color = assetMint, style = Stroke(width = 1.6.dp.toPx()))
        }
    }
}

@Composable
private fun VeldigoHoldingPanel(
    item: BalanceViewItem,
    navigation: HSNavigation,
    onReceive: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 11.dp)
            .background(assetCard, RoundedCornerShape(11.dp))
            .border(1.dp, Color(0xFF24423E), RoundedCornerShape(11.dp))
            .padding(14.dp),
    ) {
        Text(stringResource(R.string.Veldigo_Asset_YourBalance), color = assetMuted, fontSize = 12.sp)
        Spacer(Modifier.height(5.dp))
        Text(
            text = if (item.balanceHidden) "••••" else
                item.primaryValue?.value ?: stringResource(R.string.Veldigo_Asset_NoBalance),
            color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold,
        )
        if (!item.balanceHidden) {
            item.secondaryValue?.value?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = assetMuted, fontSize = 12.sp)
            }
        }
        if (item.syncingTextValue != null) {
            Spacer(Modifier.height(5.dp))
            Text(item.syncingTextValue, color = assetMuted, fontSize = 11.sp)
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
                colors = ButtonDefaults.buttonColors(containerColor = assetMint, contentColor = assetBg),
            ) {
                Text(stringResource(R.string.Balance_Send), fontSize = 13.sp)
            }
            Button(
                onClick = onReceive,
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = assetMint, contentColor = assetBg),
            ) {
                Text(stringResource(R.string.Balance_Receive), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun VeldigoAboutPanel(
    name: String,
    about: String?,
    onDetails: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text(
            text = stringResource(R.string.Veldigo_Asset_About, name),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = about?.takeIf { it.isNotBlank() } ?: stringResource(R.string.Veldigo_Asset_NoAbout),
            color = assetMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
        )
        if (!about.isNullOrBlank()) {
            Spacer(Modifier.height(9.dp))
            Text(
                text = stringResource(R.string.Veldigo_Asset_LearnMore),
                color = assetMint,
                fontSize = 12.sp,
                modifier = Modifier.clickable(onClick = onDetails),
            )
        }
    }
}

/** Matches the reference's back chevron and centered coin name without a duplicate app bar. */
@Composable
internal fun VeldigoAssetTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(53.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.arrow_m_left_24),
                contentDescription = stringResource(R.string.Button_Back),
                tint = Color.White,
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f).padding(end = 40.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
