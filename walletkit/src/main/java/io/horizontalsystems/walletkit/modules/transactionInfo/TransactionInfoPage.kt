package io.horizontalsystems.walletkit.modules.transactionInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.managers.ActiveAccountState
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEntity
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.entities.transactionrecords.TransactionRecord
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.ui.compose.components.DescriptionCell
import io.horizontalsystems.walletkit.ui.compose.components.PriceWithToggleCell
import io.horizontalsystems.walletkit.ui.compose.components.SectionTitleCell
import io.horizontalsystems.walletkit.ui.compose.components.TitleAndValueCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionAmountCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoAddressCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoBtcLockCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoCancelCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoContactCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoDoubleSpendCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoExplorerCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoRawTransaction
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoSecretKeyCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoSentToSelfCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoSpeedUpCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoStatusCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionInfoTransactionHashCell
import io.horizontalsystems.walletkit.ui.compose.components.TransactionNftAmountCell
import io.horizontalsystems.walletkit.ui.compose.components.WarningMessageCell
import kotlinx.serialization.Serializable

@Serializable
data object TransactionInfoPage : HSPage(screenshotEnabled = false) {

    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val transactionRecord = remember { TransactionInfoPayload.record }
        if (transactionRecord == null) {
            navigation.removeLastUntil(TransactionInfoPage::class, true)
            return
        }

        val accountState by App.accountManager.activeAccountStateFlow.collectAsStateWithLifecycle()
        when (val state = accountState) {
            ActiveAccountState.NotLoaded -> Unit
            is ActiveAccountState.ActiveAccount -> {
                if (TransactionInfoAccountAccess.canDisplay(
                        recordAccountId = transactionRecord.source.account.id,
                        activeAccountId = state.account?.id,
                    )
                ) {
                    TransactionInfoScreen(navigation, transactionRecord)
                } else {
                    // Hide immediately; pop in a side effect to avoid leaking the prior
                    // account's transaction fields during a wallet switch.
                    LaunchedEffect(transactionRecord.uid, state.account?.id) {
                        navigation.removeLastOrNull()
                    }
                }
            }
        }
    }

}

@Composable
fun TransactionInfoScreen(
    navigation: HSNavigation,
    transactionRecord: TransactionRecord
) {
    val viewModel = viewModel<TransactionInfoViewModel>(factory = TransactionInfoModule.Factory(transactionRecord))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VeldigoTransactionTopBar(onBack = navigation::removeLastOrNull)
        TransactionInfo(
            viewModel = viewModel,
            navigation = navigation,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Header matches the wallet's navy/mint visual language, not the upstream
 * market screens. Actual transfer values, fees and status remain rendered by
 * the chain-aware TransactionInfoViewItemFactory cells below.
 */
@Composable
private fun VeldigoTransactionTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.arrow_m_left_24),
                contentDescription = stringResource(R.string.Button_Back),
                tint = VeldigoHomePalette.White,
            )
        }
        Text(
            text = stringResource(R.string.TransactionInfo_Title),
            modifier = Modifier.weight(1f),
            color = VeldigoHomePalette.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
fun TransactionInfo(
    viewModel: TransactionInfoViewModel,
    navigation: HSNavigation,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(VeldigoHomePalette.Background),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        items(viewModel.viewItems) { section ->
            TransactionInfoSection(section, navigation, viewModel::getRawTransaction)
        }
    }
}

/**
 * Each source-backed group receives a raised navy surface, mint-compatible
 * borders and rounded corners; its existing chain-specific controls stay
 * unchanged. Never introduce mockup transaction amounts or statuses here.
 */
@Composable
private fun VeldigoTransactionCard(content: List<@Composable () -> Unit>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(18.dp))
            .padding(vertical = 4.dp),
    ) {
        content.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = VeldigoHomePalette.Border,
                    thickness = 1.dp,
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                item()
            }
        }
    }
}

@Composable
fun TransactionInfoSection(
    section: List<TransactionInfoViewItem>,
    navigation: HSNavigation,
    getRawTransaction: () -> String?
) {
    //items without background
    if (section.size == 1) {
        when (val item = section[0]) {
            is TransactionInfoViewItem.WarningMessage -> {
                WarningMessageCell(item.message)
                return
            }

            is TransactionInfoViewItem.Description -> {
                DescriptionCell(text = item.text)
                return
            }

            else -> {
                //do nothing
            }
        }
    }

    VeldigoTransactionCard(
        buildList {
            for (viewItem in section) {
                when (viewItem) {
                    is TransactionInfoViewItem.Transaction -> {
                        add {
                            SectionTitleCell(
                                title = viewItem.leftValue,
                                value = viewItem.rightValue,
                                iconResId = viewItem.icon
                            )
                        }
                    }

                    is TransactionInfoViewItem.Amount -> {
                        add {
                            TransactionAmountCell(
                                amountType = viewItem.amountType,
                                fiatAmount = viewItem.fiatValue,
                                coinAmount = viewItem.coinValue,
                                coinIconUrl = viewItem.coinIconUrl,
                                alternativeCoinIconUrl = viewItem.alternativeCoinIconUrl,
                                badge = viewItem.badge,
                                coinIconPlaceholder = viewItem.coinIconPlaceholder,
                                // Asset values stay read-only in wallet-only mode.
                                // CoinPage is a market-information destination.
                                onClick = null
                            )
                        }
                    }

                    is TransactionInfoViewItem.NftAmount -> {
                        add {
                            TransactionNftAmountCell(
                                viewItem.title,
                                viewItem.nftValue,
                                viewItem.nftName,
                                viewItem.iconUrl,
                                viewItem.iconPlaceholder,
                                viewItem.badge,
                            )
                        }
                    }

                    is TransactionInfoViewItem.Value -> {
                        add {
                            TitleAndValueCell(
                                title = viewItem.title,
                                value = viewItem.value,
                                copyable = viewItem.copyable,
                            )
                        }
                    }

                    is TransactionInfoViewItem.PriceWithToggle -> {
                        add {
                            PriceWithToggleCell(
                                title = viewItem.title,
                                valueOne = viewItem.valueTwo,
                                valueTwo = viewItem.valueOne
                            )
                        }
                    }

                    is TransactionInfoViewItem.Address -> {
                        add {
                            TransactionInfoAddressCell(
                                title = viewItem.title,
                                value = viewItem.value,
                                showAdd = viewItem.showAdd,
                                blockchainType = viewItem.blockchainType,
                                navigation = navigation,
                                onCopy = {
                                    stat(
                                        page = StatPage.TransactionInfo,
                                        event = StatEvent.Copy(StatEntity.Address),
                                        section = viewItem.statSection
                                    )
                                },
                                onAddToExisting = {
                                    stat(
                                        page = StatPage.TransactionInfo,
                                        event = StatEvent.Open(StatPage.ContactAddToExisting),
                                        section = viewItem.statSection
                                    )
                                },
                                onAddToNew = {
                                    stat(
                                        page = StatPage.TransactionInfo,
                                        event = StatEvent.Open(StatPage.ContactNew),
                                        section = viewItem.statSection
                                    )
                                }
                            )
                        }
                    }

                    is TransactionInfoViewItem.ContactItem -> {
                        add {
                            TransactionInfoContactCell(viewItem.contact.name)
                        }
                    }

                    is TransactionInfoViewItem.Status -> {
                        add {
                            TransactionInfoStatusCell(
                                status = viewItem.status,
                                notProcessed = viewItem.notProcessed,
                                navigation = navigation
                            )
                        }
                    }

                    is TransactionInfoViewItem.SpeedUpCancel -> {
                        add {
                            TransactionInfoSpeedUpCell(
                                transactionHash = viewItem.transactionHash,
                                blockchainType = viewItem.blockchainType,
                                navigation = navigation
                            )
                        }
                        add {
                            TransactionInfoCancelCell(
                                transactionHash = viewItem.transactionHash,
                                blockchainType = viewItem.blockchainType,
                                navigation = navigation
                            )
                        }
                    }

                    is TransactionInfoViewItem.TransactionHash -> {
                        add {
                            TransactionInfoTransactionHashCell(transactionHash = viewItem.transactionHash)
                        }
                    }

                    is TransactionInfoViewItem.Explorer -> {
                        viewItem.url?.let {
                            add {
                                TransactionInfoExplorerCell(
                                    title = viewItem.title,
                                    url = viewItem.url
                                )
                            }
                        }
                    }

                    is TransactionInfoViewItem.RawTransaction -> {
                        add {
                            TransactionInfoRawTransaction(rawTransaction = getRawTransaction)
                        }
                    }

                    is TransactionInfoViewItem.LockState -> {
                        add {
                            TransactionInfoBtcLockCell(
                                lockState = viewItem,
                                navigation = navigation
                            )
                        }
                    }

                    is TransactionInfoViewItem.DoubleSpend -> {
                        add {
                            TransactionInfoDoubleSpendCell(
                                transactionHash = viewItem.transactionHash,
                                conflictingHash = viewItem.conflictingHash,
                                navigation = navigation
                            )
                        }
                    }

                    is TransactionInfoViewItem.SentToSelf -> {
                        add {
                            TransactionInfoSentToSelfCell()
                        }
                    }

                    is TransactionInfoViewItem.TransactionSecretKey -> {
                        add {
                            TransactionInfoSecretKeyCell(secretKey = viewItem.key)
                        }
                    }

                    is TransactionInfoViewItem.Description -> {

                    }

                    is TransactionInfoViewItem.WarningMessage -> {

                    }
                }
            }
        }
    )
}

