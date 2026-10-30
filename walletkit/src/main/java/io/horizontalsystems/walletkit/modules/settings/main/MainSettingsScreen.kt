package io.horizontalsystems.walletkit.modules.settings.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.backuplocal.fullbackup.BackupManagerPage
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.basecurrency.BaseCurrencySettingsPage
import io.horizontalsystems.walletkit.modules.blockchainsettings.BlockchainSettingsPage
import io.horizontalsystems.walletkit.modules.contacts.ContactsRouterPage
import io.horizontalsystems.walletkit.modules.contacts.Mode
import io.horizontalsystems.walletkit.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.walletkit.modules.manageaccounts.ManageAccountsPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.settings.about.AboutPage
import io.horizontalsystems.walletkit.modules.settings.appearance.AppearancePage
import io.horizontalsystems.walletkit.modules.settings.faq.FaqListPage
import io.horizontalsystems.walletkit.modules.settings.guides.GuidesPage
import io.horizontalsystems.walletkit.modules.settings.language.LanguageSettingsPage
import io.horizontalsystems.walletkit.modules.settings.privacy.PrivacySettingsPage
import io.horizontalsystems.walletkit.modules.settings.security.SecuritySettingsPage

/**
 * Wallet-only Settings. The reference design is a compact Midnight Navy list
 * with real native routes; no market, NFT, swap, dApp, WalletConnect or
 * subscription actions are reachable from this screen.
 *
 * Recovery phrases always require wallet selection and the existing secure
 * account management flow; this screen never materializes seed words.
 */
@Composable
fun SettingsScreen(
    navigation: HSNavigation,
    viewModel: MainSettingsViewModel = viewModel(factory = MainSettingsModule.Factory()),
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.Settings_Title),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(start = 20.dp, top = 17.dp, end = 16.dp, bottom = 10.dp),
            color = VeldigoHomePalette.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            VeldigoSettingsSectionHeading(R.string.Veldigo_Settings_Security)
            VeldigoSettingsGroup {
                VeldigoSettingsRow(
                    title = stringResource(R.string.Settings_SecurityCenter),
                    icon = R.drawable.shield_24,
                    value = stringResource(
                        if (state.pinEnabled) R.string.Veldigo_Settings_PinActive
                        else R.string.Veldigo_Settings_PinMissing
                    ),
                    showAlert = state.securityCenterShowAlert,
                    onClick = {
                        navigation.slideFromRight(SecuritySettingsPage)
                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Security))
                    },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_Biometric),
                    icon = R.drawable.lock_24,
                    value = stringResource(
                        if (state.biometricEnabled) R.string.Veldigo_Settings_Enabled
                        else R.string.Veldigo_Settings_Disabled
                    ),
                    onClick = { navigation.slideFromRight(SecuritySettingsPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_ChangePin),
                    icon = R.drawable.key_24,
                    subtitle = stringResource(
                        if (state.pinEnabled) R.string.Veldigo_Settings_ChangePinHint
                        else R.string.Veldigo_Settings_SetPinHint
                    ),
                    onClick = { navigation.slideFromRight(SecuritySettingsPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_RecoveryPhrase),
                    icon = R.drawable.icon_key_24,
                    subtitle = stringResource(R.string.Veldigo_Settings_RecoveryHint),
                    showAlert = state.manageWalletShowAlert,
                    onClick = {
                        // Never open a seed directly from unprotected Settings.
                        // Existing account management validates the selected wallet
                        // and authorizes any protected recovery-phrase action.
                        navigation.slideFromRight(
                            ManageAccountsPage(ManageAccountsModule.Mode.Manage)
                        )
                    },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Settings_Privacy),
                    icon = R.drawable.lock_24,
                    onClick = { navigation.slideFromRight(PrivacySettingsPage) },
                )
            }

            VeldigoSettingsSectionHeading(R.string.Veldigo_Settings_Wallet)
            VeldigoSettingsGroup {
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_WalletManagement),
                    icon = R.drawable.wallet_24,
                    showAlert = state.manageWalletShowAlert,
                    onClick = {
                        navigation.slideFromRight(ManageAccountsPage(ManageAccountsModule.Mode.Manage))
                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.ManageWallets))
                    },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_BackupRestore),
                    icon = R.drawable.file_24,
                    onClick = { navigation.slideFromRight(BackupManagerPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_Networks),
                    icon = R.drawable.box_24,
                    onClick = { navigation.slideFromRight(BlockchainSettingsPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Contacts),
                    icon = R.drawable.user_24,
                    onClick = {
                        navigation.slideFromRight(
                            ContactsRouterPage(ContactsRouterPage.Input(Mode.Full))
                        )
                    },
                )
            }

            VeldigoSettingsSectionHeading(R.string.Veldigo_Settings_Preferences)
            VeldigoSettingsGroup {
                VeldigoSettingsRow(
                    title = stringResource(R.string.Veldigo_Settings_Appearance),
                    icon = R.drawable.ic_settings_24,
                    onClick = { navigation.slideFromRight(AppearancePage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.SettingsCurrency_Title),
                    icon = R.drawable.globe_24,
                    onClick = { navigation.slideFromRight(BaseCurrencySettingsPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Settings_Language),
                    icon = R.drawable.globe_24,
                    onClick = { navigation.slideFromRight(LanguageSettingsPage) },
                )
            }

            VeldigoSettingsSectionHeading(R.string.Veldigo_Settings_Information)
            VeldigoSettingsGroup {
                VeldigoSettingsRow(
                    title = stringResource(R.string.SettingsAboutApp_Title),
                    icon = R.drawable.ic_info_20,
                    showAlert = state.aboutAppShowAlert,
                    onClick = { navigation.slideFromRight(AboutPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Settings_Faq),
                    icon = R.drawable.message_24,
                    onClick = { navigation.slideFromRight(FaqListPage) },
                )
                VeldigoSettingsSeparator()
                VeldigoSettingsRow(
                    title = stringResource(R.string.Guides_Title),
                    icon = R.drawable.book_24,
                    onClick = { navigation.slideFromRight(GuidesPage) },
                )
            }

            Text(
                text = stringResource(R.string.Veldigo_Settings_Version, viewModel.appVersion),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, bottom = 96.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

/** Section order and action availability reflect wallet-only functionality. */
@Composable
private fun VeldigoSettingsSectionHeading(@StringRes title: Int) {
    Text(
        text = stringResource(title),
        color = VeldigoHomePalette.Muted,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 10.dp),
    )
}

@Composable
private fun VeldigoSettingsGroup(content: @Composable ColumnScope.() -> Unit) {
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
private fun VeldigoSettingsSeparator() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 55.dp, end = 14.dp),
        thickness = 1.dp,
        color = VeldigoHomePalette.Border,
    )
}

@Composable
private fun VeldigoSettingsRow(
    title: String,
    @DrawableRes icon: Int,
    subtitle: String? = null,
    value: String? = null,
    showAlert: Boolean = false,
    iconTint: Color = VeldigoHomePalette.Mint,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = iconTint,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = VeldigoHomePalette.Muted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 3.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (value != null) {
            Text(
                text = value,
                fontSize = 11.sp,
                color = if (showAlert) Color(0xFFFFB38A) else VeldigoHomePalette.Muted,
                maxLines = 1,
            )
        }
        if (showAlert) {
            Icon(
                painter = painterResource(R.drawable.ic_attention_red_20),
                contentDescription = stringResource(R.string.Veldigo_Wallets_Warning),
                tint = Color(0xFFFFB38A),
                modifier = Modifier.size(17.dp),
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = VeldigoHomePalette.Muted,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Kept for other current UI users, including the About Veldigo page. */
@Composable
fun HsSettingCell(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    iconTint: Color? = null,
    value: String? = null,
    counterBadge: String? = null,
    showAlert: Boolean = false,
    onClick: () -> Unit,
) {
    VeldigoSettingsRow(
        title = stringResource(title),
        icon = icon,
        value = value ?: counterBadge,
        showAlert = showAlert,
        iconTint = iconTint ?: VeldigoHomePalette.Mint,
        onClick = onClick,
    )
}
