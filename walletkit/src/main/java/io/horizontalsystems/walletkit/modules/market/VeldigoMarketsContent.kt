package io.horizontalsystems.walletkit.modules.market

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.alternativeImageUrl
import io.horizontalsystems.walletkit.core.iconPlaceholder
import io.horizontalsystems.walletkit.core.imageUrl
import io.horizontalsystems.walletkit.entities.ViewState
import io.horizontalsystems.walletkit.modules.coin.CoinPage
import io.horizontalsystems.walletkit.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.walletkit.modules.market.search.MarketSearchPage
import io.horizontalsystems.walletkit.modules.market.topcoins.MarketTopCoinsViewModel
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.ui.compose.components.ListErrorView
import io.horizontalsystems.walletkit.modules.coin.overview.ui.Loading
import java.math.BigDecimal
import java.util.Locale

private val navy = Color(0xFF030D0D)
private val surface = Color(0xFF152222)
private val border = Color(0xFF293E3D)
private val mint = Color(0xFF41E6C7)
private val muted = Color(0xFF9FB2B5)
private val negative = Color(0xFFEB7776)

/** Reference filter labels have real ranking and 24-hour price-change semantics. */
internal enum class VeldigoMarketFilter(val sortingField: SortingField) {
    All(SortingField.HighestCap),
    TopGainers(SortingField.TopGainers),
    TopLosers(SortingField.TopLosers),
}

internal object VeldigoMarketRules {
    fun matching(items: List<MarketItem>, query: String): List<MarketItem> {
        val needle = query.trim()
        if (needle.isEmpty()) return items
        return items.filter {
            matchesQuery(it.fullCoin.coin.code, it.fullCoin.coin.name, needle)
        }
    }

    fun matchesQuery(code: String, name: String, query: String): Boolean {
        val needle = query.trim()
        return needle.isEmpty() ||
            code.contains(needle, ignoreCase = true) ||
            name.contains(needle, ignoreCase = true)
    }

    fun changeLabel(change: BigDecimal?): String? = change?.let {
        String.format(Locale.US, "%s%.1f%%",
            if (it.signum() > 0) "+" else "",
            it.toFloat())
    }

    fun showPrice(item: MarketItem): Boolean = item.priceAvailable
}

@Composable
internal fun VeldigoMarketsContent(navigation: HSNavigation) {
    val viewModel = viewModel<MarketTopCoinsViewModel>(
        factory = MarketTopCoinsViewModel.Factory(
            TopMarket.Top100, SortingField.HighestCap
        )
    )
    val state = viewModel.uiState
    var query by rememberSaveable { mutableStateOf("") }
    var showWatchlist by rememberSaveable { mutableStateOf(false) }
    val visible = remember(state.marketItems, query) {
        VeldigoMarketRules.matching(state.marketItems, query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navy)
            .statusBarsPadding(),
    ) {
        Box(Modifier.fillMaxWidth().height(58.dp)) {
            Text(
                text = stringResource(R.string.Veldigo_Markets_Title),
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = { showWatchlist = !showWatchlist },
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 7.dp),
            ) {
                Icon(
                    painter = painterResource(
                        if (showWatchlist) R.drawable.ic_heart_filled_20 else R.drawable.ic_heart_24
                    ),
                    contentDescription = stringResource(R.string.Market_Tab_Watchlist),
                    tint = if (showWatchlist) mint else muted,
                )
            }
        }

        if (showWatchlist) {
            MarketFavoritesScreen(navigation)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(51.dp)
                    .background(surface, RoundedCornerShape(11.dp))
                    .border(1.dp, border, RoundedCornerShape(11.dp))
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(11.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            stringResource(R.string.Veldigo_Markets_Search),
                            color = muted,
                            fontSize = 14.sp,
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it.take(80) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search,
                        ),
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                IconButton(onClick = {
                    navigation.slideFromBottom(MarketSearchPage)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_down_arrow_20),
                        contentDescription = stringResource(R.string.Veldigo_Markets_AllSearch),
                        tint = muted,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                VeldigoMarketFilter.entries.forEach { filter ->
                    val selected = state.sortingField == filter.sortingField
                    Box(
                        modifier = Modifier
                            .weight(if (filter == VeldigoMarketFilter.All) .73f else 1.3f)
                            .height(40.dp)
                            .clip(CircleShape)
                            .background(if (selected) mint else surface)
                            .clickable(role = Role.Tab) {
                                viewModel.onSelectSortingField(filter.sortingField)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(when (filter) {
                                VeldigoMarketFilter.All -> R.string.Veldigo_Markets_All
                                VeldigoMarketFilter.TopGainers -> R.string.Veldigo_Markets_TopGainers
                                VeldigoMarketFilter.TopLosers -> R.string.Veldigo_Markets_TopLosers
                            }),
                            color = if (selected) navy else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("#", modifier = Modifier.width(23.dp), color = muted, fontSize = 11.sp)
                Text(
                    stringResource(R.string.Veldigo_Markets_Coin),
                    modifier = Modifier.weight(1f),
                    color = muted,
                    fontSize = 11.sp,
                )
                Text(
                    stringResource(R.string.Veldigo_Markets_Price),
                    modifier = Modifier.width(103.dp),
                    textAlign = TextAlign.End,
                    color = muted,
                    fontSize = 11.sp,
                )
                Text(
                    stringResource(R.string.Veldigo_Markets_Change),
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.End,
                    color = muted,
                    fontSize = 11.sp,
                )
            }

            when (state.viewState) {
                ViewState.Loading -> Loading()
                is ViewState.Error -> ListErrorView(
                    stringResource(R.string.SyncError), viewModel::refresh
                )
                ViewState.Success -> {
                    if (visible.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(R.string.Veldigo_Markets_NoResults),
                                color = muted,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.Veldigo_Markets_AllSearch),
                                color = mint,
                                modifier = Modifier.clickable {
                                    navigation.slideFromBottom(MarketSearchPage)
                                },
                                fontSize = 13.sp,
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(visible, key = { it.fullCoin.coin.uid }) { coin ->
                                VeldigoMarketRow(coin) {
                                    navigation.slideFromRight(
                                        CoinPage(CoinPage.Input(coin.fullCoin.coin.uid))
                                    )
                                }
                            }
                            item { Spacer(Modifier.height(14.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VeldigoMarketRow(item: MarketItem, onClick: () -> Unit) {
    val coin = item.fullCoin.coin
    val change = item.diff
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.rank?.toString() ?: "—",
            modifier = Modifier.width(23.dp),
            color = muted,
            fontSize = 12.sp,
        )
        Image(
            painter = rememberAsyncImagePainter(
                model = coin.imageUrl,
                error = item.fullCoin.coin.alternativeImageUrl?.let {
                    rememberAsyncImagePainter(
                        model = it,
                        error = painterResource(item.fullCoin.iconPlaceholder),
                    )
                } ?: painterResource(item.fullCoin.iconPlaceholder),
            ),
            contentDescription = null,
            modifier = Modifier.size(30.dp).clip(CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = coin.code,
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = if (VeldigoMarketRules.showPrice(item))
                App.numberFormatter.formatFiatFull(item.rate.value, item.rate.currency.symbol)
            else "—",
            modifier = Modifier.width(103.dp),
            textAlign = TextAlign.End,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
        )
        Text(
            text = VeldigoMarketRules.changeLabel(change) ?: "—",
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.End,
            color = when {
                change == null -> muted
                change.signum() >= 0 -> mint
                else -> negative
            },
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}
