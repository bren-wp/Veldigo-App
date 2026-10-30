package io.horizontalsystems.walletkit.modules.pin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.pin.PinModule
import io.horizontalsystems.walletkit.modules.pin.set.PinSetModule
import io.horizontalsystems.walletkit.modules.pin.set.PinSetViewModel

/**
 * The visual keypad is Veldigo-native, but the original PIN state machine,
 * uniqueness checks, double entry and secure pinComponent.setPin operation
 * remain authoritative. No PIN digits are ever displayed as plain text.
 */
@Composable
fun PinSet(
    title: String,
    description: String,
    dismissWithSuccess: () -> Unit,
    onBackPress: () -> Unit,
    forDuress: Boolean = false,
    viewModel: PinSetViewModel = viewModel(factory = PinSetModule.Factory(forDuress))
) {
    val state = viewModel.uiState
    LaunchedEffect(state.finished) {
        if (state.finished) {
            viewModel.finished()
            dismissWithSuccess()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        val compact = maxHeight < 680.dp
        val keySize = if (compact) 58.dp else 66.dp
        val keyGap = if (compact) 9.dp else 12.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackPress) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_m_left_24),
                        contentDescription = stringResource(R.string.Button_Back),
                        tint = VeldigoHomePalette.White,
                    )
                }
                Text(
                    text = title,
                    modifier = Modifier.weight(1f).padding(end = 48.dp),
                    color = VeldigoHomePalette.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    maxLines = 2,
                )
            }

            Spacer(Modifier.height(if (compact) 17.dp else 35.dp))
            Box(
                modifier = Modifier
                    .size(if (compact) 66.dp else 80.dp)
                    .background(Color(0xFF103C3B), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.lock_24),
                    contentDescription = null,
                    tint = VeldigoHomePalette.Mint,
                    modifier = Modifier.size(35.dp),
                )
            }
            Spacer(Modifier.height(if (compact) 13.dp else 19.dp))
            Text(
                text = if (state.stage == PinSetModule.SetStage.Enter)
                    description else stringResource(R.string.PinSet_ConfirmInfo),
                color = VeldigoHomePalette.White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            )
            Spacer(Modifier.height(if (compact) 19.dp else 25.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                repeat(PinModule.PIN_COUNT) { index ->
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < state.enteredCount) VeldigoHomePalette.Mint
                                else VeldigoHomePalette.Border
                            )
                            .border(1.dp, VeldigoHomePalette.Muted.copy(alpha = 0.45f), CircleShape),
                    )
                }
            }
            if (state.error != null) {
                Spacer(Modifier.height(11.dp))
                Text(
                    text = state.error,
                    color = Color(0xFFFFB38A),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(if (compact) 22.dp else 38.dp))
            VeldigoPinSetKeypad(
                keySize = keySize,
                gap = keyGap,
                enabled = !state.finished,
                onNumberClick = viewModel::onKeyClick,
                onDeleteClick = viewModel::onDelete,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun VeldigoPinSetKeypad(
    keySize: Dp,
    gap: Dp,
    enabled: Boolean,
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
) {
    val context = LocalContext.current
    val digits = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        digits.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { digit ->
                    VeldigoPinSetKey(
                        text = digit.toString(),
                        size = keySize,
                        enabled = enabled,
                        onClick = {
                            HudHelper.vibrate(context)
                            onNumberClick(digit)
                        },
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.size(keySize))
            VeldigoPinSetKey(
                text = "0",
                size = keySize,
                enabled = enabled,
                onClick = {
                    HudHelper.vibrate(context)
                    onNumberClick(0)
                },
            )
            Box(
                modifier = Modifier
                    .size(keySize)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, enabled = enabled) {
                        HudHelper.vibrate(context)
                        onDeleteClick()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_backspace),
                    contentDescription = stringResource(R.string.Button_Delete),
                    tint = VeldigoHomePalette.Muted,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun VeldigoPinSetKey(text: String, size: Dp, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, CircleShape)
            .clickable(role = Role.Button, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) VeldigoHomePalette.White else VeldigoHomePalette.Muted,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
