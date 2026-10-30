package io.horizontalsystems.walletkit.modules.multiswap

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.entities.Currency
import io.horizontalsystems.walletkit.uiv3.components.controls.AvailableBalanceRow
import io.horizontalsystems.walletkit.uiv3.components.controls.TokenAmountInput
import io.horizontalsystems.walletkit.uiv3.components.controls.TokenSelector
import io.horizontalsystems.marketkit.models.Token
import java.math.BigDecimal

private val night = Color(0xFF030E0D)
private val panel = Color(0xFF142121)
private val mint = Color(0xFF41E6C7)
private val muted = Color(0xFFA4B6B9)
private val rim = Color(0xFF25413C)

/**
 * Reproduces the structure of reference phone 14 with genuine swap quote data.
 * The original TokenAmountInput and token-selector actions are left authoritative;
 * this is strictly a presentation component and never places/sends a transaction.
 */
@Composable
internal fun VeldigoSwapForm(
    amountIn: BigDecimal?,
    fiatAmountIn: BigDecimal?,
    fiatAmountInputEnabled: Boolean,
    onSwitchPairs: () -> Unit,
    switchPairsEnabled: Boolean,
    amountOut: BigDecimal?,
    fiatAmountOut: BigDecimal?,
    hasRateOut: Boolean,
    onValueChange: (BigDecimal?) -> Unit,
    onFiatValueChange: (BigDecimal?) -> Unit,
    onClickCoinFrom: () -> Unit,
    onClickCoinTo: () -> Unit,
    tokenIn: Token?,
    tokenOut: Token?,
    currency: Currency,
    focusRequester: FocusRequester,
    availableBalance: BigDecimal?,
    showBalancePlaceholder: Boolean,
    onAvailableBalanceClick: (() -> Unit)?,
    onPercentClick: (Int) -> Unit,
    onClearClick: () -> Unit,
    quote: SwapProviderQuote?,
) {
    var explainSlippage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(night)
            .padding(start = 17.dp, end = 17.dp, top = 17.dp),
    ) {
        Text(
            text = stringResource(R.string.Veldigo_Swap_From),
            color = muted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(9.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(panel, RoundedCornerShape(12.dp))
                .border(1.dp, rim, RoundedCornerShape(12.dp)),
        ) {
            TokenAmountInput(
                token = tokenIn,
                amount = amountIn,
                fiatAmount = fiatAmountIn,
                fiatAmountInputEnabled = fiatAmountInputEnabled,
                currency = currency,
                focusRequester = focusRequester,
                onValueChange = onValueChange,
                onFiatValueChange = onFiatValueChange,
                amountExceedsBalance = false,
                onTokenClick = onClickCoinFrom,
            )
        }
        AvailableBalanceRow(
            balanceToken = tokenIn,
            availableBalance = availableBalance,
            showPlaceholder = showBalancePlaceholder,
            onAvailableBalanceClick = onAvailableBalanceClick,
            onPercentClick = onPercentClick,
            onClearClick = onClearClick,
            showClear = amountIn != null,
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(63.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF213C37), CircleShape)
                    .border(1.dp, rim, CircleShape)
                    .clickable(enabled = switchPairsEnabled, role = Role.Button, onClick = onSwitchPairs),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_swap_circle_24),
                    contentDescription = stringResource(R.string.Veldigo_Swap_Switch),
                    tint = if (switchPairsEnabled) mint else muted,
                    modifier = Modifier.size(23.dp),
                )
            }
        }

        Text(
            text = stringResource(R.string.Veldigo_Swap_To),
            color = muted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(9.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(panel, RoundedCornerShape(12.dp))
                .border(1.dp, rim, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenSelector(token = tokenOut, onClick = onClickCoinTo)
            Spacer(Modifier.width(7.dp))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = amountOut?.stripTrailingZeros()?.toPlainString()
                        ?: stringResource(R.string.Veldigo_Swap_NoQuote),
                    color = if (amountOut == null) muted else Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = if (hasRateOut && fiatAmountOut != null)
                        "${currency.symbol}${fiatAmountOut.stripTrailingZeros().toPlainString()}"
                    else stringResource(R.string.Veldigo_Swap_NoFiatPrice),
                    color = muted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = if (VeldigoSwapReferenceRules.quotedRateAvailable(quote))
                SwapPriceUIHelper(quote.tokenIn, quote.tokenOut, quote.amountIn, quote.amountOut).priceStr
            else stringResource(R.string.Veldigo_Swap_NoRate),
            color = if (VeldigoSwapReferenceRules.quotedRateAvailable(quote)) Color.White else muted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(47.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.Veldigo_Swap_Slippage),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .background(panel, RoundedCornerShape(9.dp))
                    .border(1.dp, rim, RoundedCornerShape(9.dp))
                    .clickable(role = Role.Button, onClick = { explainSlippage = true })
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.Veldigo_Swap_SlippageOnReview),
                        color = muted,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(R.drawable.arrow_s_down_24),
                        contentDescription = null,
                        tint = muted,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))
    }

    if (explainSlippage) {
        AlertDialog(
            onDismissRequest = { explainSlippage = false },
            title = { Text(stringResource(R.string.Veldigo_Swap_Slippage)) },
            text = { Text(stringResource(R.string.Veldigo_Swap_SlippageExplanation)) },
            confirmButton = {
                TextButton(onClick = { explainSlippage = false }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}

/** UI helper: a quote must be present before a real price can be shown. */
internal object VeldigoSwapReferenceRules {
    fun quotedRateAvailable(quote: SwapProviderQuote?): Boolean =
        quote != null && quote.amountIn > BigDecimal.ZERO && quote.amountOut >= BigDecimal.ZERO

    fun canProceed(step: SwapStep, isAlreadyProceeding: Boolean, hasQuote: Boolean): Boolean =
        step == SwapStep.Proceed && !isAlreadyProceeding && hasQuote
}
