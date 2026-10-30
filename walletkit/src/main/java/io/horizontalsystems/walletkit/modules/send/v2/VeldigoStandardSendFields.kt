package io.horizontalsystems.walletkit.modules.send.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.shorten
import io.horizontalsystems.walletkit.entities.Wallet
import io.horizontalsystems.walletkit.uiv3.components.controls.AvailableBalanceRow
import io.horizontalsystems.walletkit.uiv3.components.controls.TokenAmountInput
import java.math.BigDecimal

/**
 * Screen 12's Veldigo skin. All editable values are backed by SendFormViewModel;
 * recipient lookup, fiat conversion, memo support and send validation are unchanged.
 *
 * A live network fee is deliberately NOT invented here: the chain's authoritative
 * fee calculation lives on SendV2ConfirmPage after recipient and amount are valid.
 */
@Composable
internal fun VeldigoStandardSendFields(
    wallet: Wallet,
    state: SendFormUiState,
    focusRequester: FocusRequester,
    onOpenRecipient: () -> Unit,
    onEnterAmount: (BigDecimal?) -> Unit,
    onEnterFiatAmount: (BigDecimal?) -> Unit,
    onMax: () -> Unit,
    onFeeSettings: (() -> Unit)?,
) {
    val dark = Color(0xFF030D0E)
    val panel = Color(0xFF111D1E)
    val border = Color(0xFF324849)
    val muted = Color(0xFF9EB5B6)
    val mint = Color(0xFF41E6C7)
    val fieldShape = RoundedCornerShape(9.dp)

    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 19.dp),
    ) {
        if (!state.hideAddress) {
            Text(
                text = stringResource(R.string.Veldigo_Send_To),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(9.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                        .height(53.dp)
                        .background(panel, fieldShape)
                        .border(1.dp, border, fieldShape)
                        .clickable(role = Role.Button, onClick = onOpenRecipient)
                        .padding(horizontal = 13.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = state.contactName ?: state.address?.hex?.shorten()
                            ?: stringResource(R.string.Veldigo_Send_SelectRecipient),
                        color = if (state.riskyAddress) Color(0xFFFFA5A5)
                            else if (state.address != null) Color.White else muted,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(
                    modifier = Modifier.size(53.dp)
                        .background(panel, fieldShape)
                        .border(1.dp, border, fieldShape)
                        .clickable(role = Role.Button, onClick = onOpenRecipient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_qr_scan_20),
                        contentDescription = stringResource(R.string.Veldigo_Send_ScanOrSelect),
                        tint = mint,
                        modifier = Modifier.size(23.dp),
                    )
                }
            }
            if (state.riskyAddress) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Send_RecipientRisk),
                    color = Color(0xFFFFA5A5), fontSize = 12.sp, lineHeight = 17.sp,
                )
            }
            Spacer(Modifier.height(27.dp))
        }

        Text(
            text = stringResource(R.string.Veldigo_Send_Amount),
            color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(9.dp))
        // Reuse the application's production amount editor. It enforces precision,
        // locale/IME, fiat rate and amount validation through the original ViewModel.
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(panel, fieldShape)
                .border(1.dp, border, fieldShape),
        ) {
            TokenAmountInput(
                token = wallet.token,
                amount = state.amount,
                fiatAmount = state.fiatAmount,
                fiatAmountInputEnabled = state.fiatAmountInputEnabled,
                currency = state.currency,
                focusRequester = focusRequester,
                onValueChange = onEnterAmount,
                onFiatValueChange = onEnterFiatAmount,
                amountExceedsBalance = state.step is SendStep.Error &&
                    state.step.error == io.horizontalsystems.walletkit.modules.multiswap.SwapError.InsufficientBalanceFrom,
                onTokenClick = null,
            )
        }
        AvailableBalanceRow(
            balanceToken = wallet.token,
            availableBalance = state.availableBalance,
            showPlaceholder = true,
            onAvailableBalanceClick = if (100 in state.disabledPercents) null else onMax,
        )

        Spacer(Modifier.height(27.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.Veldigo_Send_NetworkFee),
                    color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Send_FeeOnReview),
                    color = muted, fontSize = 12.sp, lineHeight = 17.sp,
                )
            }
            if (onFeeSettings != null) {
                Box(
                    modifier = Modifier
                        .background(panel, fieldShape)
                        .border(1.dp, border, fieldShape)
                        .clickable(role = Role.Button, onClick = onFeeSettings)
                        .padding(horizontal = 13.dp, vertical = 11.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.Veldigo_Send_FeeSettings),
                            color = Color.White, fontSize = 12.sp,
                        )
                        Spacer(Modifier.width(7.dp))
                        Icon(
                            painter = painterResource(R.drawable.arrow_s_down_20),
                            contentDescription = null,
                            tint = muted, modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(26.dp))
    }
}

/** UI-only readiness. The original ViewModel, fee estimator and signer retain authority. */
internal object VeldigoSendUiRules {
    fun canContinue(step: SendStep): Boolean = step is SendStep.Proceed
}
