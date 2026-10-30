package io.horizontalsystems.walletkit.modules.buy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.uiv3.components.cell.CellLeftCoinIcon
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * Veldigo reference screen 15. This app does not currently integrate a
 * fiat on/off-ramp or PCI-compliant stored card/payment service.
 *
 * The UI must not fabricate card details, a live exchange quote, or a charge.
 * Only the owned wallet asset selection and local amount editing are active.
 */
@Serializable
data object BuyCryptoPage : HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val tokens = App.walletManager.activeWallets
            .map { it.token }
            .distinctBy { it.coin.uid to it.blockchainType }

        BuyCryptoScreen(
            tokens = tokens,
            fiatCode = App.currencyManager.baseCurrency.code,
            fiatDecimals = App.currencyManager.baseCurrency.decimal,
            onBack = navigation::removeLastOrNull,
        )
    }
}

internal object VeldigoFiatPurchaseGate {
    const val providerConnected = false

    fun validAmount(value: String, decimals: Int): Boolean {
        if (value.isBlank() || decimals < 0 || !value.all { it.isDigit() || it == '.' } || value.count { it == '.' } > 1) return false
        val number = value.toBigDecimalOrNull() ?: return false
        return number > BigDecimal.ZERO && number.scale().coerceAtLeast(0) <= decimals
    }

    fun canExecute(
        amountValid: Boolean,
        paymentMethodVerified: Boolean,
        liveProviderQuote: Boolean,
    ): Boolean = providerConnected && amountValid &&
        paymentMethodVerified && liveProviderQuote
}

private val navy = Color(0xFF030E0D)
private val darkCard = Color(0xFF122020)
private val cardBorder = Color(0xFF28403E)
private val mint = Color(0xFF41E6C7)
private val muted = Color(0xFF9FB4B6)

@Composable
internal fun BuyCryptoScreen(
    tokens: List<Token>,
    fiatCode: String,
    fiatDecimals: Int,
    onBack: () -> Unit,
) {
    var selling by rememberSaveable { mutableStateOf(false) }
    var amount by rememberSaveable { mutableStateOf("") }
    var selected by remember(tokens) {
        mutableStateOf(tokens.firstOrNull { it.coin.code.equals("BTC", ignoreCase = true) } ?: tokens.firstOrNull())
    }
    var chooseAsset by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<Int?>(null) }
    val focus = LocalFocusManager.current
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navy)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = Color.White,
                )
            }
            Text(
                text = stringResource(R.string.Veldigo_Buy_Title),
                modifier = Modifier.weight(1f).padding(end = 48.dp),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp)
                    .background(darkCard, shape),
            ) {
                listOf(false, true).forEach { isSell ->
                    val active = selling == isSell
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(49.dp)
                            .background(if (active) mint else Color.Transparent, shape)
                            .clickable(role = Role.Tab) {
                                selling = isSell
                                focus.clearFocus()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(
                                if (isSell) R.string.Veldigo_Buy_Sell else R.string.Veldigo_Buy_Buy
                            ),
                            color = if (active) navy else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
            Spacer(Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.Veldigo_Buy_Asset),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(9.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(darkCard, shape)
                    .border(1.dp, cardBorder, shape)
                    .clickable(role = Role.Button, enabled = tokens.isNotEmpty()) { chooseAsset = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                selected?.let { token ->
                    CellLeftCoinIcon(token = token)
                    Spacer(Modifier.width(13.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = token.coin.code,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = token.coin.name,
                            color = muted,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } ?: Text(
                    text = stringResource(R.string.Veldigo_Buy_NoAsset),
                    modifier = Modifier.weight(1f),
                    color = muted,
                    fontSize = 13.sp,
                )
                Icon(
                    painter = painterResource(R.drawable.arrow_s_down_24),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            Spacer(Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.Veldigo_Buy_Amount),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(9.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(darkCard, shape)
                    .border(1.dp, cardBorder, shape)
                    .padding(start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (amount.isEmpty()) {
                        Text("0", color = muted, fontSize = 22.sp)
                    }
                    BasicTextField(
                        value = amount,
                        onValueChange = { candidate ->
                            val normal = candidate.replace(',', '.')
                            if (normal.length <= 14 &&
                                normal.all { it.isDigit() || it == '.' } &&
                                normal.count { it == '.' } <= 1 &&
                                normal.substringAfter('.', "").length <= fiatDecimals.coerceAtLeast(0)
                            ) amount = normal
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(color = Color.White, fontSize = 22.sp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .background(Color(0xFF1B2A29), RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(fiatCode, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.Veldigo_Buy_QuoteUnavailable),
                color = muted,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.Veldigo_Buy_PaymentMethod),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(9.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(darkCard, shape)
                    .border(1.dp, cardBorder, shape)
                    .clickable(role = Role.Button) {
                        notice = R.string.Veldigo_Buy_NoPaymentProvider
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_wallet_24),
                    contentDescription = null,
                    tint = mint,
                    modifier = Modifier.size(23.dp),
                )
                Spacer(Modifier.width(13.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Buy_PaymentDisconnected),
                    color = muted,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painter = painterResource(R.drawable.arrow_s_down_24),
                    contentDescription = null,
                    tint = muted,
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.Veldigo_Buy_NoPaymentProvider),
                color = muted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(18.dp))
        }

        Button(
            onClick = {
                focus.clearFocus()
                notice = if (selected == null) R.string.Veldigo_Buy_NoAsset
                else if (!VeldigoFiatPurchaseGate.validAmount(amount, fiatDecimals)) R.string.Veldigo_Buy_InvalidAmount
                else R.string.Veldigo_Buy_NoPaymentProvider
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = mint, contentColor = navy),
        ) {
            Text(
                text = stringResource(
                    if (selling) R.string.Veldigo_Buy_SellAsset else R.string.Veldigo_Buy_BuyAsset,
                    selected?.coin?.code ?: stringResource(R.string.Veldigo_Buy_Buy),
                ),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(16.dp))
    }

    if (chooseAsset) {
        AlertDialog(
            onDismissRequest = { chooseAsset = false },
            title = { Text(stringResource(R.string.Veldigo_Buy_ChooseAsset)) },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                ) {
                    tokens.forEach { token ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button) {
                                    selected = token
                                    chooseAsset = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CellLeftCoinIcon(token = token)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(token.coin.code, fontWeight = FontWeight.SemiBold)
                                Text(token.coin.name, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { chooseAsset = false }) {
                    Text(stringResource(R.string.Button_Cancel))
                }
            },
        )
    }

    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.Veldigo_Buy_UnavailableTitle)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}
