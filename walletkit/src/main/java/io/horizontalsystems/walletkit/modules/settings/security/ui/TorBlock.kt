package io.horizontalsystems.walletkit.modules.settings.security.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.settings.privacy.tor.SecurityTorSettingsViewModel
import io.horizontalsystems.walletkit.modules.settings.privacy.tor.TorStatus

/**
 * The native switch changes only a pending preference. A separate explicit
 * confirmation persists it and restarts the application. Do not create a
 * parent-row toggle as that can fire twice when Switch is tapped.
 */
@Composable
fun TorBlock(viewModel: SecurityTorSettingsViewModel) {
    val torStatus by viewModel.torStatusFlow.collectAsStateWithLifecycle()

    if (viewModel.showRestartAlert) {
        val enable = viewModel.pendingTorEnabled == true
        AlertDialog(
            onDismissRequest = viewModel::resetSwitch,
            title = { Text(stringResource(R.string.Tor_Alert_Title)) },
            text = {
                Text(
                    text = stringResource(
                        if (enable) R.string.Veldigo_Privacy_EnableTorWarning
                        else R.string.Veldigo_Privacy_DisableTorWarning,
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::setTorEnabled) {
                    Text(
                        stringResource(
                            if (enable) R.string.Button_Enable else R.string.Button_Disable,
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::resetSwitch) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 77.dp)
            .padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tor_connection_24),
            contentDescription = null,
            tint = VeldigoHomePalette.Mint,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.Tor_Title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VeldigoHomePalette.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(
                    when {
                        !viewModel.torCheckEnabled -> R.string.Veldigo_Privacy_TorDisabled
                        torStatus == TorStatus.Connected -> R.string.Veldigo_Privacy_TorConnected
                        torStatus == TorStatus.Connecting -> R.string.Veldigo_Privacy_TorConnecting
                        torStatus == TorStatus.Failed -> R.string.Veldigo_Privacy_TorFailed
                        else -> R.string.Veldigo_Privacy_TorNotConnected
                    },
                ),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = VeldigoHomePalette.Muted,
                maxLines = 3,
            )
        }
        Switch(
            checked = viewModel.torCheckEnabled,
            onCheckedChange = viewModel::setTorEnabledWithChecks,
            enabled = !viewModel.showRestartAlert && !viewModel.restartApp,
            colors = SwitchDefaults.colors(
                checkedTrackColor = VeldigoHomePalette.Mint,
                checkedThumbColor = VeldigoHomePalette.Background,
                uncheckedTrackColor = VeldigoHomePalette.Border,
                uncheckedThumbColor = VeldigoHomePalette.Muted,
            ),
        )
    }
}
