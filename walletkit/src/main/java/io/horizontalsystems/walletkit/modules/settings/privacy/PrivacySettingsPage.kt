package io.horizontalsystems.walletkit.modules.settings.privacy

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.main.MainModule
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.settings.privacy.tor.SecurityTorSettingsModule
import io.horizontalsystems.walletkit.modules.settings.privacy.tor.SecurityTorSettingsViewModel
import io.horizontalsystems.walletkit.modules.settings.security.SecuritySettingsPage
import io.horizontalsystems.walletkit.modules.settings.security.ui.TorBlock
import kotlinx.serialization.Serializable

/**
 * Wallet-only privacy screen 29. Real usage-statistics preference and real
 * Tor setting are the only toggles. No imaginary VPN or tracker protection.
 */
@Serializable
data object PrivacySettingsPage : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val torViewModel = viewModel<SecurityTorSettingsViewModel>(
            factory = SecurityTorSettingsModule.Factory(),
        )
        val activity = LocalActivity.current
        PrivacyScreen(
            navigation = navigation,
            torViewModel = torViewModel,
            restartApp = { activity?.let { restartApplication(it) } },
        )
    }

    private fun restartApplication(activity: Activity) {
        MainModule.restartProcess(activity)
    }
}

@Composable
fun PrivacyScreen(
    navigation: HSNavigation,
    torViewModel: SecurityTorSettingsViewModel,
    restartApp: () -> Unit = {},
) {
    val viewModel = viewModel<PrivacyViewModel>(factory = PrivacyViewModel.Factory())
    val ui = viewModel.uiState

    LaunchedEffect(torViewModel.restartApp) {
        if (torViewModel.restartApp) {
            torViewModel.appRestarted()
            restartApp()
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
            IconButton(onClick = navigation::removeLastOrNull) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = VeldigoHomePalette.White,
                )
            }
            Text(
                text = stringResource(R.string.Settings_Privacy),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            VeldigoPrivacyHeader()
            VeldigoPrivacySectionLabel(R.string.Veldigo_Privacy_Data)
            VeldigoPrivacyPanel {
                ShareUiDataBlock(
                    checked = ui.uiStatsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.toggleUiStats(enabled)
                        // StatsManager also enforces the actual stored preference.
                        stat(page = StatPage.Privacy, event = StatEvent.EnableUiStats(enabled))
                    },
                )
            }

            VeldigoPrivacySectionLabel(R.string.Veldigo_Privacy_Network)
            VeldigoPrivacyPanel {
                TorBlock(torViewModel)
            }
            VeldigoPrivacyCaption(R.string.Veldigo_Privacy_TorExplanation)

            VeldigoPrivacySectionLabel(R.string.Veldigo_Privacy_Device)
            VeldigoPrivacyPanel {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 66.dp)
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield_24),
                        contentDescription = null,
                        tint = VeldigoHomePalette.Mint,
                        modifier = Modifier.size(22.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.Veldigo_Privacy_DeviceSecurity),
                            color = VeldigoHomePalette.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = stringResource(R.string.Veldigo_Privacy_DeviceSecurityHint),
                            color = VeldigoHomePalette.Muted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                        )
                    }
                    IconButton(onClick = { navigation.slideFromRight(SecuritySettingsPage) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = stringResource(R.string.Veldigo_Privacy_ManageSecurity),
                            tint = VeldigoHomePalette.Mint,
                        )
                    }
                }
            }

            Spacer(Modifier.size(24.dp))
            Row(
                modifier = Modifier.padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_20),
                    contentDescription = null,
                    tint = VeldigoHomePalette.Mint,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.Veldigo_Privacy_Notice),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.size(34.dp))
        }
    }
}

@Composable
private fun VeldigoPrivacyHeader() {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape)
            .padding(horizontal = 18.dp, vertical = 19.dp),
    ) {
        Text(
            text = stringResource(R.string.Veldigo_Privacy_Heading),
            color = VeldigoHomePalette.White,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
        )
        Spacer(Modifier.size(7.dp))
        Text(
            text = stringResource(R.string.Veldigo_Privacy_Subheading),
            color = VeldigoHomePalette.Muted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
    }
}

@Composable
private fun VeldigoPrivacySectionLabel(@androidx.annotation.StringRes label: Int) {
    Text(
        text = stringResource(label),
        color = VeldigoHomePalette.Muted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, top = 22.dp, bottom = 10.dp),
    )
}

@Composable
private fun VeldigoPrivacyPanel(content: @Composable ColumnScope.() -> Unit) {
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
private fun VeldigoPrivacyCaption(@androidx.annotation.StringRes text: Int) {
    Text(
        text = stringResource(text),
        modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 9.dp),
        color = VeldigoHomePalette.Muted,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )
}

/**
 * Kept public for callers using the previous privacy helper API. Only the
 * switch is interactive to prevent accidental double toggling.
 */
@Composable
fun ShareUiDataBlock(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_share_24px),
            tint = VeldigoHomePalette.Mint,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.ShareUiData),
                color = VeldigoHomePalette.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.ShareUiDataDescription),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = VeldigoHomePalette.Mint,
                checkedThumbColor = VeldigoHomePalette.Background,
                uncheckedTrackColor = VeldigoHomePalette.Border,
                uncheckedThumbColor = VeldigoHomePalette.Muted,
            ),
        )
    }
}
