package io.horizontalsystems.walletkit.modules.btcblockchainsettings

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.btcblockchainsettings.BtcBlockchainSettingsModule.BlockchainSettingsIcon
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.marketkit.models.Blockchain
import kotlinx.serialization.Serializable

/**
 * Screen 33 — wallet-only Bitcoin-family source settings. The available
 * choices are supplied by BtcBlockchainManager, not hard-coded node URLs.
 */
@Serializable
data class BtcBlockchainSettingsPage(val blockchain: Blockchain) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val viewModel = viewModel<BtcBlockchainSettingsViewModel>(
            factory = BtcBlockchainSettingsModule.Factory(blockchain),
        )
        BtcBlockchainSettingsScreen(viewModel, navigation)
    }
}

@Composable
private fun BtcBlockchainSettingsScreen(
    viewModel: BtcBlockchainSettingsViewModel,
    navigation: HSNavigation,
) {
    var confirmSave by remember { mutableStateOf(false) }
    var confirmDiscard by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.closeScreen) {
        if (viewModel.closeScreen) navigation.removeLastOrNull()
    }

    val onBack: () -> Unit = {
        if (viewModel.saveButtonEnabled) {
            confirmDiscard = true
        } else {
            navigation.removeLastOrNull()
        }
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
                .padding(start = 8.dp, end = 56.dp),
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
                text = stringResource(R.string.Veldigo_BtcSync_Title),
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
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            VeldigoBtcSyncHeader(viewModel.title)
            Text(
                text = stringResource(R.string.Veldigo_BtcSync_Description),
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 16.dp),
            )

            Row(
                modifier = Modifier.padding(start = 5.dp, top = 9.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_BtcSync_AvailableModes),
                    fontSize = 12.sp,
                    color = VeldigoHomePalette.Muted,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = viewModel.restoreSources.size.toString(),
                    color = VeldigoHomePalette.Mint,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }

            VeldigoBtcSyncGroup {
                if (viewModel.restoreSources.isEmpty()) {
                    Text(
                        text = stringResource(R.string.Veldigo_BtcSync_NoSources),
                        modifier = Modifier.padding(16.dp),
                        color = VeldigoHomePalette.Muted,
                        fontSize = 13.sp,
                    )
                }
                viewModel.restoreSources.forEachIndexed { index, source ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = VeldigoHomePalette.Border,
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 66.dp, end = 12.dp),
                        )
                    }
                    BlockchainSettingCell(
                        title = source.title,
                        subtitle = source.subtitle,
                        checked = source.selected,
                        icon = source.icon,
                        onClick = { viewModel.onSelectRestoreMode(source) },
                    )
                }
            }

            if (viewModel.saveButtonEnabled) {
                Text(
                    text = stringResource(R.string.Veldigo_BtcSync_Unsaved),
                    color = VeldigoHomePalette.Mint,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 5.dp, top = 11.dp),
                )
            }

            Row(
                modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 27.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    tint = VeldigoHomePalette.Mint,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = stringResource(R.string.BtcBlockchainSettings_RestoreSourceChangeWarning),
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    color = VeldigoHomePalette.Muted,
                )
            }
            Text(
                text = stringResource(R.string.Veldigo_BtcSync_PrivacyNotice),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 28.dp),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            if (viewModel.saveError) {
                Text(
                    text = stringResource(R.string.Veldigo_BtcSync_SaveError),
                    color = Color(0xFFFFB38A),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 9.dp),
                )
            }
            Button(
                onClick = { confirmSave = true },
                enabled = viewModel.saveButtonEnabled && !viewModel.saving,
                modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VeldigoHomePalette.Mint,
                    contentColor = VeldigoHomePalette.Background,
                    disabledContainerColor = VeldigoHomePalette.Border,
                    disabledContentColor = VeldigoHomePalette.Muted,
                ),
            ) {
                Text(
                    text = stringResource(R.string.Button_Save),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }

    if (confirmSave) {
        AlertDialog(
            onDismissRequest = { confirmSave = false },
            title = { Text(stringResource(R.string.Veldigo_BtcSync_ConfirmTitle)) },
            text = { Text(stringResource(R.string.Veldigo_BtcSync_ConfirmBody)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmSave = false
                    viewModel.onSaveClick()
                }) {
                    Text(stringResource(R.string.Button_Save))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSave = false }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }

    if (confirmDiscard) {
        AlertDialog(
            onDismissRequest = { confirmDiscard = false },
            title = { Text(stringResource(R.string.Veldigo_BtcSync_DiscardTitle)) },
            text = { Text(stringResource(R.string.Veldigo_BtcSync_DiscardBody)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDiscard = false
                    navigation.removeLastOrNull()
                }) {
                    Text(stringResource(R.string.Veldigo_BtcSync_Discard))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDiscard = false }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }
}

@Composable
private fun VeldigoBtcSyncHeader(chainName: String) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                contentDescription = null,
                tint = VeldigoHomePalette.Mint,
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
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.Veldigo_BtcSync_ChainSubtitle),
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun VeldigoBtcSyncGroup(content: @Composable ColumnScope.() -> Unit) {
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

/** Public legacy helper preserved; blockchain icon URLs are never downloaded. */
@Composable
fun BlockchainSettingCell(
    title: String,
    subtitle: String,
    checked: Boolean,
    icon: BlockchainSettingsIcon?,
    onClick: () -> Unit,
) {
    val imageRes = when (icon) {
        is BlockchainSettingsIcon.ApiIcon -> icon.resId
        is BlockchainSettingsIcon.BlockchainIcon, null -> R.drawable.globe_24
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 74.dp)
            .semantics { selected = checked }
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(start = 13.dp, end = 13.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(imageRes),
                contentDescription = null,
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(23.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = VeldigoHomePalette.Muted,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.ic_checkmark_20),
                contentDescription = stringResource(R.string.Veldigo_BtcSync_Selected),
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Spacer(Modifier.size(20.dp))
        }
    }
}
