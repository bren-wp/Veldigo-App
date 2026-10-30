package io.horizontalsystems.walletkit.modules.manageaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEntity
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.manageaccount.ManageAccountPage
import io.horizontalsystems.walletkit.modules.manageaccounts.ManageAccountsModule.AccountViewItem
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.serialization.Serializable

@Serializable
data class ManageAccountsPage(val input: ManageAccountsModule.Mode) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        ManageAccountsScreen(navigation, input)
    }
}

/**
 * Veldigo's wallet list uses real accounts only. Display names and account type
 * come from AccountManager; no mocked portfolio totals or wallet categories.
 *
 * The existing add/import/watch and management routes remain unchanged.
 */
@Composable
fun ManageAccountsScreen(navigation: HSNavigation, mode: ManageAccountsModule.Mode) {
    val viewModel = viewModel<ManageAccountsViewModel>(factory = ManageAccountsModule.Factory(mode))
    var searchQuery by rememberSaveable { mutableStateOf(viewModel.searchQuery) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf(VeldigoWalletFilter.All) }

    LaunchedEffect(viewModel.finish) {
        if (viewModel.finish) navigation.removeLastOrNull()
    }

    val nextInput = ManageAccountsModule.Input(
        popOffOnSuccess = ManageAccountsPage::class,
        popOffInclusive = mode == ManageAccountsModule.Mode.Switcher,
    )

    val viewItems = viewModel.viewItems
    val shownAccounts = remember(viewItems, selectedFilter) {
        viewItems?.let { (owned, watch) ->
            (owned + watch)
                .filter { selectedFilter.matches(isWatchAccount = it.isWatchAccount) }
                .sortedBy { it.title.lowercase() }
        }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(horizontal = 8.dp),
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
                text = stringResource(R.string.Veldigo_Wallets_Title),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            IconButton(
                onClick = {
                    if (searchOpen) {
                        searchQuery = ""
                        viewModel.updateFilter("")
                    }
                    searchOpen = !searchOpen
                },
            ) {
                Icon(
                    painter = painterResource(if (searchOpen) R.drawable.ic_close else R.drawable.ic_search),
                    contentDescription = stringResource(if (searchOpen) R.string.Button_Close else R.string.Veldigo_Wallets_Search),
                    tint = VeldigoHomePalette.White,
                )
            }
            IconButton(onClick = { navigation.slideFromRight(AddWalletPage(nextInput)) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_24),
                    contentDescription = stringResource(R.string.ManageAccounts_AddWallet),
                    tint = VeldigoHomePalette.Mint,
                )
            }
        }

        if (searchOpen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 15.dp, vertical = 14.dp),
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = stringResource(R.string.Veldigo_Wallets_Search),
                        color = VeldigoHomePalette.Muted,
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.updateFilter(it)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = VeldigoHomePalette.White,
                        fontSize = 14.sp,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            VeldigoAccountFilterChip(
                label = stringResource(R.string.Veldigo_Wallets_All),
                selected = selectedFilter == VeldigoWalletFilter.All,
                onClick = { selectedFilter = VeldigoWalletFilter.All },
            )
            VeldigoAccountFilterChip(
                label = stringResource(R.string.ManageAccount_Wallets),
                selected = selectedFilter == VeldigoWalletFilter.Owned,
                onClick = { selectedFilter = VeldigoWalletFilter.Owned },
            )
            VeldigoAccountFilterChip(
                label = stringResource(R.string.ManageAccount_WatchAddresses),
                selected = selectedFilter == VeldigoWalletFilter.Watch,
                onClick = { selectedFilter = VeldigoWalletFilter.Watch },
            )
        }

        if (shownAccounts.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_empty_wallet_24),
                    contentDescription = null,
                    tint = VeldigoHomePalette.Muted,
                    modifier = Modifier.size(42.dp),
                )
                Text(
                    text = stringResource(
                        if (searchQuery.isNotBlank() || viewItems?.let { it.first.isNotEmpty() || it.second.isNotEmpty() } == true)
                            R.string.EmptyResults
                        else R.string.ManageAccounts_NoActiveWallets
                    ),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
                if (searchQuery.isBlank() && shownAccounts.isEmpty()) {
                    TextButton(onClick = { navigation.slideFromRight(AddWalletPage(nextInput)) }) {
                        Text(
                            text = stringResource(R.string.ManageAccounts_AddWallet),
                            color = VeldigoHomePalette.Mint,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp),
            ) {
                items(shownAccounts, key = { it.accountId }) { item ->
                    AccountCellWrapper(item, viewModel, navigation)
                }
            }
        }
    }
}

@Composable
private fun VeldigoAccountFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50.dp)
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clip(shape)
            .background(if (selected) VeldigoHomePalette.Mint else VeldigoHomePalette.Surface)
            .border(1.dp, if (selected) VeldigoHomePalette.Mint else VeldigoHomePalette.Border, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) VeldigoHomePalette.Background else VeldigoHomePalette.White,
            maxLines = 1,
        )
    }
}

@Composable
fun AccountCellWrapper(
    account: AccountViewItem,
    viewModel: ManageAccountsViewModel,
    navigation: HSNavigation,
) {
    AccountCell(
        accountViewItem = account,
        onSelect = {
            viewModel.onSelect(account)
            stat(page = StatPage.ManageWallets, event = StatEvent.Select(StatEntity.Wallet))
        },
        onOptionIconClick = {
            navigation.slideFromRight(ManageAccountPage(ManageAccountPage.Input(account.accountId)))
            stat(page = StatPage.ManageWallets, event = StatEvent.Open(StatPage.ManageWallet))
        },
    )
}

@Composable
fun AccountCell(
    accountViewItem: AccountViewItem,
    onSelect: (AccountViewItem) -> Unit,
    onOptionIconClick: (AccountViewItem) -> Unit,
) {
    val cardShape = RoundedCornerShape(15.dp)
    val highlighted = accountViewItem.selected
    val needsBackup = accountViewItem.backupRequired
    val needsMigration = accountViewItem.migrationRequired
    val warning = needsBackup || needsMigration || accountViewItem.showAlertIcon
    val subtitle = when {
        needsMigration -> stringResource(R.string.ManageAccount_MigrationRequired_Title)
        needsBackup -> stringResource(R.string.ManageAccount_BackupRequired_Title)
        else -> accountViewItem.subtitle
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(cardShape)
            .background(VeldigoHomePalette.Surface)
            .border(
                1.dp,
                if (highlighted) VeldigoHomePalette.Mint else VeldigoHomePalette.Border,
                cardShape,
            )
            .clickable(role = Role.Button) { onSelect(accountViewItem) }
            .padding(start = 12.dp, top = 13.dp, bottom = 13.dp, end = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(iconShape)
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(
                    if (accountViewItem.isWatchAccount) R.drawable.binocular_24
                    else R.drawable.wallet_filled_24
                ),
                contentDescription = null,
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = accountViewItem.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = VeldigoHomePalette.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = if (warning) Color(0xFFFFC27A) else VeldigoHomePalette.Muted,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (highlighted) {
            Icon(
                painter = painterResource(R.drawable.ic_done_filled_20),
                contentDescription = stringResource(R.string.Veldigo_Wallets_Active),
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(20.dp),
            )
        } else if (warning) {
            Icon(
                painter = painterResource(R.drawable.warning_outline_24),
                contentDescription = stringResource(R.string.ManageAccounts_ManageWallet),
                tint = Color(0xFFFFC27A),
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = { onOptionIconClick(accountViewItem) }) {
            Icon(
                painter = painterResource(R.drawable.threedots_24),
                contentDescription = stringResource(R.string.ManageAccounts_ManageWallet),
                tint = VeldigoHomePalette.Muted,
            )
        }
    }
}
