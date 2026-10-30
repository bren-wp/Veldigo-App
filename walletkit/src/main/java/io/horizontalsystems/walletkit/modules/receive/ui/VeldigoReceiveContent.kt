package io.horizontalsystems.walletkit.modules.receive.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEntity
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.modules.receive.ReceiveModule
import io.horizontalsystems.walletkit.ui.helpers.TextHelper
import io.horizontalsystems.walletkit.ui.compose.components.TextImportantError
import io.horizontalsystems.walletkit.ui.compose.components.TextImportantWarning
import java.math.BigDecimal

private val receiveNavy = Color(0xFF030E0D)
private val receiveMint = Color(0xFF41E6C7)
private val receiveSurface = Color(0xFF122020)
private val receiveBorder = Color(0xFF2B4947)
private val receiveMuted = Color(0xFFA9BCBF)

/**
 * The QR and copy actions must represent the selected real wallet account.
 * A payment request may additionally include an amount; sharing then uses
 * the original blockchain-specific AddressUriService result.
 */
internal object VeldigoReceivePayloadRules {
    fun hasValidPayload(address: String, uri: String): Boolean =
        address.isNotBlank() && uri.isNotBlank()

    fun copyAddress(address: String): String? =
        address.takeIf { it.isNotBlank() }

    fun sharePayload(address: String, uri: String, amount: BigDecimal?): String? =
        if (!hasValidPayload(address, uri)) null
        else if (amount != null && amount > BigDecimal.ZERO) uri
        else address
}

@Composable
internal fun VeldigoReceiveContent(
    uiState: ReceiveModule.AbstractUiState,
    title: String,
    slot1: @Composable () -> Unit,
    onSetAmount: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val realAddress = uiState.address
    val paymentUri = uiState.uri
    val safeToDisplay = VeldigoReceivePayloadRules.hasValidPayload(realAddress, paymentUri)
    val share = VeldigoReceivePayloadRules.sharePayload(realAddress, paymentUri, uiState.amount)
    val isPaymentRequest = uiState.amount != null && uiState.amount!! > BigDecimal.ZERO

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(receiveNavy),
    ) {
        val compact = maxHeight < 615.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uiState.alertText?.let {
                if (it is ReceiveModule.AlertText.Critical) {
                    Spacer(Modifier.height(8.dp))
                    TextImportantError(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.content,
                    )
                }
            }
            if (uiState.watchAccount) {
                Spacer(Modifier.height(10.dp))
                TextImportantWarning(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.Balance_Receive_WatchAddressAlert),
                )
            }

            Spacer(Modifier.height(if (compact) 20.dp else 48.dp))
            if (safeToDisplay) {
                val qrPainter = rememberQrCodePainter(paymentUri) {
                    errorCorrectionLevel = QrErrorCorrectionLevel.MediumHigh
                }
                Box(
                    modifier = Modifier
                        .size(if (compact) 174.dp else 205.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = qrPainter,
                        contentDescription = stringResource(R.string.Veldigo_Receive_QrDescription, title),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(if (compact) 174.dp else 205.dp)
                        .border(1.dp, receiveBorder, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.Veldigo_Receive_AddressUnavailable),
                        textAlign = TextAlign.Center,
                        color = receiveMuted,
                        fontSize = 13.sp,
                    )
                }
            }
            Spacer(Modifier.height(if (compact) 34.dp else 61.dp))
            Text(
                text = stringResource(R.string.Veldigo_Receive_YourAddress, title),
                color = receiveMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(11.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(receiveSurface, RoundedCornerShape(9.dp))
                    .border(1.dp, receiveBorder, RoundedCornerShape(9.dp))
                    .clickable(
                        role = Role.Button,
                        enabled = safeToDisplay,
                    ) {
                        VeldigoReceivePayloadRules.copyAddress(realAddress)?.let {
                            TextHelper.copyText(it)
                            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                            stat(page = StatPage.Receive, event = StatEvent.Copy(StatEntity.ReceiveAddress))
                        }
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = realAddress.ifBlank { "—" },
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_copy_20),
                    contentDescription = stringResource(R.string.Veldigo_Receive_CopyAddress),
                    tint = if (safeToDisplay) receiveMint else receiveMuted,
                    modifier = Modifier.size(21.dp),
                )
            }

            Spacer(Modifier.height(17.dp))
            Button(
                onClick = {
                    if (share != null) {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, share)
                            type = "text/plain"
                        }
                        try {
                            context.startActivity(Intent.createChooser(intent, null))
                            stat(page = StatPage.Receive, event = StatEvent.Share(StatEntity.ReceiveAddress))
                        } catch (_: ActivityNotFoundException) {
                            // No compatible app: no address is altered and no send occurs.
                        }
                    }
                },
                enabled = share != null,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D3B38),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF172321),
                    disabledContentColor = receiveMuted,
                ),
            ) {
                Text(
                    text = stringResource(
                        if (isPaymentRequest) R.string.Veldigo_Receive_ShareRequest
                        else R.string.Veldigo_Receive_ShareAddress
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            val network = uiState.blockchainName
            if (network != null || !uiState.mainNet) {
                Spacer(Modifier.height(15.dp))
                Text(
                    text = buildString {
                        append(network ?: title)
                        if (!uiState.mainNet) append(" · TestNet")
                    },
                    color = receiveMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }

            if (uiState.addressFormat != null || uiState.addressType != null) {
                Spacer(Modifier.height(7.dp))
                Text(
                    text = listOfNotNull(uiState.addressFormat, uiState.addressType).joinToString(" · "),
                    color = receiveMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                )
            }

            uiState.amountString?.let { amount ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Receive_RequestAmount, amount),
                    color = receiveMint,
                    fontSize = 12.sp,
                )
            }
            // BTC address rotation/history and chain-specific warning/actions stay functional.
            slot1()

            if (!uiState.watchAccount) {
                TextButton(onClick = onSetAmount) {
                    Text(
                        text = stringResource(R.string.Button_SetAmount),
                        color = receiveMint,
                        fontSize = 12.sp,
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
        }
    }
}
