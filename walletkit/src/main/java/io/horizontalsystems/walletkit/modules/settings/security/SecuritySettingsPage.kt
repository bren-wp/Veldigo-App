package io.horizontalsystems.walletkit.modules.settings.security

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.pin.EditDuressPinPage
import io.horizontalsystems.walletkit.modules.pin.EditPinPage
import io.horizontalsystems.walletkit.modules.pin.SetDuressPinIntroPage
import io.horizontalsystems.walletkit.modules.pin.SetPinPage
import io.horizontalsystems.walletkit.modules.settings.security.autolock.AutoLockIntervalsPage
import io.horizontalsystems.walletkit.modules.settings.security.passcode.SecurityPasscodeSettingsModule
import io.horizontalsystems.walletkit.modules.settings.security.passcode.SecuritySettingsViewModel
import io.horizontalsystems.walletkit.modules.settings.security.setup.EnableFingerprintPage
import io.horizontalsystems.walletkit.ui.compose.components.RowUniversal
import kotlinx.serialization.Serializable

/**
 * Settings screen 28. All security actions call the existing PIN/biometric
 * subsystem; no device authentication or persistent settings are simulated.
 */
@Serializable
data object SecuritySettingsPage : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        SecurityCenterScreen(
            securitySettingsViewModel = viewModel(factory = SecurityPasscodeSettingsModule.Factory()),
            navigation = navigation,
        )
    }
}

@Composable
private fun SecurityCenterScreen(
    securitySettingsViewModel: SecuritySettingsViewModel,
    navigation: HSNavigation,
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        securitySettingsViewModel.update()
    }

    val ui = securitySettingsViewModel.uiState
    val authorizedEditPin = navigation.authorizedAction {
        navigation.slideFromRight(EditPinPage)
    }
    val authorizedDisablePin = navigation.authorizedAction {
        securitySettingsViewModel.disablePin()
    }
    val authorizedDisableBiometrics = navigation.authorizedAction {
        securitySettingsViewModel.disableBiometrics()
    }
    val enableBiometrics = navigation.ensurePinSet(R.string.PinSet_ForBiometrics) {
        // The OS prompt must succeed before this preference is persisted.
        navigation.slideFromRight(EnableFingerprintPage())
    }
    val authorizedDuressPin = navigation.authorizedAction {
        if (ui.duressPinEnabled) navigation.slideFromRight(EditDuressPinPage)
        else navigation.slideFromRight(SetDuressPinIntroPage)
    }
    val setDuressPin = navigation.ensurePinSet(R.string.PinSet_ForDuress) {
        navigation.slideFromRight(SetDuressPinIntroPage)
    }
    val authorizedDisableDuress = navigation.authorizedAction {
        securitySettingsViewModel.disableDuressPin()
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
                text = stringResource(R.string.Settings_SecurityCenter),
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
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
            VeldigoSecuritySummary(
                pinEnabled = ui.pinEnabled,
                biometricEnabled = ui.pinEnabled && ui.biometricsEnabled,
            )
            VeldigoSecuritySectionTitle(R.string.Veldigo_SecurityCenter_Access)
            VeldigoSecurityGroup {
                VeldigoSecurityNavRow(
                    title = stringResource(
                        if (ui.pinEnabled) R.string.SettingsSecurity_EditPin
                        else R.string.SettingsSecurity_EnablePin,
                    ),
                    subtitle = stringResource(
                        if (ui.pinEnabled) R.string.Veldigo_SecurityCenter_PinSet
                        else R.string.Veldigo_SecurityCenter_PinRequired,
                    ),
                    icon = R.drawable.key_24,
                    value = stringResource(
                        if (ui.pinEnabled) R.string.Veldigo_Settings_Enabled
                        else R.string.Veldigo_Settings_Disabled,
                    ),
                    warn = !ui.pinEnabled,
                    onClick = {
                        if (ui.pinEnabled) authorizedEditPin()
                        else navigation.slideFromRight(SetPinPage())
                    },
                )
                if (securitySettingsViewModel.biometricSettingsVisible) {
                    VeldigoSecurityDivider()
                    VeldigoSecuritySwitchRow(
                        title = stringResource(R.string.SettingsSecurity_Biometric_Authentication),
                        subtitle = stringResource(R.string.Veldigo_SecurityCenter_BiometricHint),
                        icon = R.drawable.icon_touch_id_24,
                        checked = ui.pinEnabled && ui.biometricsEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) enableBiometrics()
                            else authorizedDisableBiometrics()
                        },
                    )
                }
                if (ui.pinEnabled) {
                    VeldigoSecurityDivider()
                    VeldigoSecurityNavRow(
                        title = stringResource(R.string.Settings_AutoLock),
                        subtitle = stringResource(R.string.Settings_AutoLock_Description),
                        icon = R.drawable.lock_24,
                        value = stringResource(ui.autoLockIntervalName),
                        onClick = { navigation.slideFromRight(AutoLockIntervalsPage) },
                    )
                }
            }

            VeldigoSecuritySectionTitle(R.string.Veldigo_SecurityCenter_Privacy)
            VeldigoSecurityGroup {
                VeldigoSecuritySwitchRow(
                    title = stringResource(R.string.Appearance_BalanceAutoHide),
                    subtitle = stringResource(R.string.Appearance_BalanceAutoHide_Description),
                    icon = R.drawable.eye_on_24,
                    checked = ui.balanceAutoHideEnabled,
                    onCheckedChange = securitySettingsViewModel::onSetBalanceAutoHidden,
                )
                VeldigoSecurityDivider()
                VeldigoSecuritySwitchRow(
                    title = stringResource(R.string.SettingsSecurity_HideSuspiciousTxs),
                    subtitle = stringResource(R.string.SettingsSecurity_HideSuspiciousTxs_Description),
                    icon = R.drawable.shield_24,
                    checked = ui.hideSuspiciousTxs,
                    onCheckedChange = securitySettingsViewModel::hideSuspiciousTxs,
                )
            }

            VeldigoSecuritySectionTitle(R.string.Veldigo_SecurityCenter_Advanced)
            VeldigoSecurityGroup {
                VeldigoSecurityNavRow(
                    title = stringResource(R.string.Veldigo_SecurityCenter_DuressPin),
                    subtitle = stringResource(
                        if (ui.duressPinEnabled) R.string.Veldigo_SecurityCenter_DuressActive
                        else R.string.Veldigo_SecurityCenter_DuressHint,
                    ),
                    icon = R.drawable.shield_check_filled_24,
                    value = stringResource(
                        if (ui.duressPinEnabled) R.string.Veldigo_Settings_Enabled
                        else R.string.Veldigo_Settings_Disabled,
                    ),
                    onClick = {
                        if (ui.pinEnabled) authorizedDuressPin()
                        else setDuressPin()
                    },
                )
                if (ui.duressPinEnabled) {
                    VeldigoSecurityDivider()
                    VeldigoSecurityNavRow(
                        title = stringResource(R.string.Veldigo_SecurityCenter_RemoveDuress),
                        subtitle = stringResource(R.string.Veldigo_SecurityCenter_RemoveDuressHint),
                        icon = R.drawable.trash_24,
                        iconTint = Color(0xFFFFB38A),
                        onClick = authorizedDisableDuress,
                    )
                }
            }

            if (ui.pinEnabled) {
                VeldigoSecuritySectionTitle(R.string.Veldigo_SecurityCenter_Critical)
                VeldigoSecurityGroup {
                    VeldigoSecurityNavRow(
                        title = stringResource(R.string.SettingsSecurity_DisablePin),
                        subtitle = stringResource(R.string.Veldigo_SecurityCenter_DisablePinHint),
                        icon = R.drawable.lock_24,
                        iconTint = Color(0xFFFFB38A),
                        onClick = authorizedDisablePin,
                    )
                }
            }

            Spacer(Modifier.height(34.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    tint = VeldigoHomePalette.Mint,
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                )
                Text(
                    text = stringResource(R.string.Veldigo_SecurityCenter_Safety),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = VeldigoHomePalette.Muted,
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun VeldigoSecuritySummary(pinEnabled: Boolean, biometricEnabled: Boolean) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(shape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, shape)
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.shield_check_filled_24),
            tint = if (pinEnabled) VeldigoHomePalette.Mint else Color(0xFFFFB38A),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    if (pinEnabled) R.string.Veldigo_SecurityCenter_Protected
                    else R.string.Veldigo_SecurityCenter_NeedsPin
                ),
                color = VeldigoHomePalette.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = stringResource(
                    if (biometricEnabled) R.string.Veldigo_SecurityCenter_ActiveWithBiometrics
                    else if (pinEnabled) R.string.Veldigo_SecurityCenter_PinOnly
                    else R.string.Veldigo_SecurityCenter_PromptPin
                ),
                color = VeldigoHomePalette.Muted,
                lineHeight = 18.sp,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun VeldigoSecuritySectionTitle(@androidx.annotation.StringRes id: Int) {
    Text(
        text = stringResource(id),
        modifier = Modifier.padding(start = 4.dp, top = 21.dp, bottom = 10.dp),
        color = VeldigoHomePalette.Muted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun VeldigoSecurityGroup(content: @Composable ColumnScope.() -> Unit) {
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
private fun VeldigoSecurityDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 53.dp, end = 14.dp),
        thickness = 1.dp,
        color = VeldigoHomePalette.Border,
    )
}

@Composable
private fun VeldigoSecurityNavRow(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    value: String? = null,
    warn: Boolean = false,
    iconTint: Color = VeldigoHomePalette.Mint,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(21.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 4.dp),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        value?.let {
            Text(
                text = it,
                color = if (warn) Color(0xFFFFB38A) else VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                maxLines = 2,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = VeldigoHomePalette.Muted,
            modifier = Modifier.size(17.dp),
        )
    }
}

@Composable
private fun VeldigoSecuritySwitchRow(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = VeldigoHomePalette.Mint,
            modifier = Modifier.size(21.dp),
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
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 4.dp),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
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

/** Existing privacy settings still depend on this public RowScope-based cell. */
@Composable
fun SecurityCenterCell(
    start: @Composable RowScope.() -> Unit,
    center: @Composable RowScope.() -> Unit,
    end: @Composable() (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp).heightIn(min = 48.dp),
        verticalPadding = 0.dp,
        onClick = onClick,
    ) {
        start.invoke(this)
        Spacer(Modifier.size(16.dp))
        center.invoke(this)
        end?.let {
            Spacer(Modifier.weight(1f))
            end.invoke(this)
        }
    }
}
