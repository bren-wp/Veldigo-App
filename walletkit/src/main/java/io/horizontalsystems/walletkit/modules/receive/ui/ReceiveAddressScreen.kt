package io.horizontalsystems.walletkit.modules.receive.ui

import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEntity
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.entities.ViewState
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.modules.coin.overview.ui.Loading
import io.horizontalsystems.walletkit.modules.receive.ReceiveModule
import io.horizontalsystems.walletkit.ui.compose.ColoredTextStyle
import io.horizontalsystems.walletkit.ui.compose.ComposeAppTheme
import io.horizontalsystems.walletkit.ui.compose.TranslatableString
import io.horizontalsystems.walletkit.ui.compose.components.ButtonSecondaryCircle
import io.horizontalsystems.walletkit.ui.compose.components.ErrorScreenWithAction
import io.horizontalsystems.walletkit.ui.compose.components.HSpacer
import io.horizontalsystems.walletkit.ui.compose.components.HsDivider
import io.horizontalsystems.walletkit.ui.compose.components.HsIconButton
import io.horizontalsystems.walletkit.ui.compose.components.HsTextButton
import io.horizontalsystems.walletkit.ui.compose.components.MenuItem
import io.horizontalsystems.walletkit.ui.compose.components.RowUniversal
import io.horizontalsystems.walletkit.ui.compose.components.TextImportantError
import io.horizontalsystems.walletkit.ui.compose.components.TextImportantWarning
import io.horizontalsystems.walletkit.ui.compose.components.VSpacer
import io.horizontalsystems.walletkit.ui.compose.components.body_grey50
import io.horizontalsystems.walletkit.ui.compose.components.body_jacob
import io.horizontalsystems.walletkit.ui.compose.components.captionSB_jacob
import io.horizontalsystems.walletkit.ui.compose.components.caption_grey
import io.horizontalsystems.walletkit.ui.compose.components.subhead1_leah
import io.horizontalsystems.walletkit.ui.compose.components.subhead2_grey
import io.horizontalsystems.walletkit.ui.compose.components.subhead2_leah
import io.horizontalsystems.walletkit.ui.compose.components.subhead_grey
import io.horizontalsystems.walletkit.ui.compose.components.title3_leah
import io.horizontalsystems.walletkit.ui.helpers.TextHelper
import io.horizontalsystems.walletkit.uiv3.components.HSScaffold
import io.horizontalsystems.walletkit.uiv3.components.controls.ButtonVariant
import io.horizontalsystems.walletkit.uiv3.components.controls.HSIconButton
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveAddressScreen(
    title: String,
    uiState: ReceiveModule.AbstractUiState,
    setAmount: (BigDecimal?) -> Unit,
    onErrorClick: () -> Unit = {},
    slot1: @Composable () -> Unit = {},
    onBackPress: () -> Unit,
    closeModule: (() -> Unit)? = null,
) {
    val localView = LocalView.current
    val openAmountDialog = remember { mutableStateOf(false) }

    HSScaffold(
        title = title,
        onBack = onBackPress,
        menuItems = if (closeModule == null) emptyList() else listOf(
            MenuItem(
                title = TranslatableString.ResString(R.string.Button_Done),
                icon = R.drawable.ic_close,
                onClick = closeModule
            )
        )
    ) {
        Crossfade(uiState.viewState, label = "") { viewState ->
            Column {
                when (viewState) {
                    is ViewState.Error -> {
                        ErrorScreenWithAction(
                            text = stringResource(R.string.SyncError),
                            icon = R.drawable.ic_warning_64,
                        ) {
                            HsTextButton(
                                onClick = onErrorClick,
                            ) {
                                captionSB_jacob(
                                    text = stringResource(R.string.Button_Retry),
                                )
                            }
                        }
                    }

                    ViewState.Loading -> {
                        Loading()
                    }

                    ViewState.Success -> {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            VSpacer(12.dp)
                            uiState.alertText?.let {
                                WarningTextView(it)
                            }

                            if (uiState.watchAccount) {
                                TextImportantWarning(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    text = stringResource(R.string.Balance_Receive_WatchAddressAlert),
                                )
                            }

                            VSpacer(12.dp)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, Color(0xFF244155), RoundedCornerShape(20.dp))
                                    .background(Color(0xFF112337)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            TextHelper.copyText(uiState.uri)
                                            HudHelper.showSuccessMessage(
                                                localView,
                                                R.string.Hud_Text_Copied
                                            )

                                            stat(
                                                page = StatPage.Receive,
                                                event = StatEvent.Copy(StatEntity.ReceiveAddress)
                                            )
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    VSpacer(32.dp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ComposeAppTheme.colors.white)
                                            .size(224.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        QrCodeImage(uiState.uri)
                                    }
                                    VSpacer(24.dp)
                                    subhead2_leah(
                                        modifier = Modifier.padding(horizontal = 46.dp),
                                        text = uiState.address,
                                        textAlign = TextAlign.Center,
                                    )

                                    val testNetBadge =
                                        if (!uiState.mainNet) " (TestNet)" else ""
                                    uiState.blockchainName?.let { blockchainName ->
                                        VSpacer(12.dp)
                                        subhead_grey(
                                            modifier = Modifier.padding(horizontal = 46.dp),
                                            text = stringResource(R.string.Balance_Network) + ": " + blockchainName + testNetBadge,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    uiState.addressFormat?.let { addressFormat ->
                                        VSpacer(12.dp)
                                        subhead_grey(
                                            modifier = Modifier.padding(horizontal = 46.dp),
                                            text = stringResource(R.string.Balance_Format) + ": " + addressFormat + testNetBadge,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    uiState.addressType?.let { addressType ->
                                        VSpacer(12.dp)
                                        subhead_grey(
                                            modifier = Modifier.padding(horizontal = 46.dp),
                                            text = stringResource(R.string.Balance_Receive_AddressType) + ": " + addressType + testNetBadge,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                    VSpacer(32.dp)
                                }
                                val additionalItems = buildList {
                                    uiState.amountString?.let { amount ->
                                        add(ReceiveModule.AdditionalData.Amount(amount))
                                    }
                                }

                                if (additionalItems.isNotEmpty()) {
                                    AdditionalDataSection(
                                        items = additionalItems,
                                        onClearAmount = {
                                            setAmount(null)

                                            stat(
                                                page = StatPage.Receive,
                                                event = StatEvent.RemoveAmount
                                            )
                                        },
                                    )
                                }

                                slot1.invoke()
                            }

                            VSpacer(24.dp)

                            ActionButtonsRow(
                                uri = uiState.uri,
                                watchAccount = uiState.watchAccount,
                                openAmountDialog = openAmountDialog,
                            )

                            VSpacer(32.dp)
                        }
                    }
                }
            }
        }
        if (openAmountDialog.value) {
            AmountInputDialog(
                initialAmount = uiState.amount,
                onDismissRequest = { openAmountDialog.value = false },
                onAmountConfirm = { amount ->
                    setAmount(amount)
                    openAmountDialog.value = false

                    stat(page = StatPage.Receive, event = StatEvent.SetAmount)
                }
            )
        }
    }
}

@Composable
private fun QrCodeImage(address: String) {
    // A large central logo can obscure encoded modules on low-quality cameras.
    // A high-contrast, unobstructed QR is safer for irreversible wallet deposits.
    val qrPainter = rememberQrCodePainter(address) {
        errorCorrectionLevel = QrErrorCorrectionLevel.MediumHigh
    }
    Image(
        painter = qrPainter,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        contentScale = ContentScale.Fit,
        contentDescription = "QR code for the selected wallet receiving address"
    )
}

@Composable
private fun WarningTextView(
    alertText: ReceiveModule.AlertText
) {
    when (alertText) {
        is ReceiveModule.AlertText.Critical -> {
            TextImportantError(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = alertText.content,
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    uri: String,
    watchAccount: Boolean,
    openAmountDialog: MutableState<Boolean>,
) {
    val localView = LocalView.current
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = uri.isNotBlank(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF41E6C7),
                contentColor = Color(0xFF071421),
            ),
            onClick = {
                TextHelper.copyText(uri)
                HudHelper.showSuccessMessage(localView, R.string.Hud_Text_Copied)
                stat(page = StatPage.Receive, event = StatEvent.Copy(StatEntity.ReceiveAddress))
            },
        ) {
            Text(
                text = stringResource(R.string.Button_Copy),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = uri.isNotBlank(),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFF34516A)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            onClick = {
                val share = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, uri)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(share, null))
                stat(page = StatPage.Receive, event = StatEvent.Share(StatEntity.ReceiveAddress))
            },
        ) {
            Text(text = stringResource(R.string.Button_Share), fontSize = 15.sp)
        }
        if (!watchAccount) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.dp, Color.Transparent),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFB5C6D7)
                ),
                onClick = { openAmountDialog.value = true },
            ) {
                Text(text = stringResource(R.string.Button_SetAmount), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AdditionalDataSection(
    items: List<ReceiveModule.AdditionalData>,
    onClearAmount: () -> Unit,
) {
    val localView = LocalView.current

    items.forEach { item ->
        HsDivider(modifier = Modifier.fillMaxWidth())
        RowUniversal(
            modifier = Modifier.height(52.dp),
        ) {
            when (item) {
                is ReceiveModule.AdditionalData.Amount -> {
                    subhead2_grey(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        text = stringResource(R.string.Balance_Receive_Amount),
                    )
                    subhead1_leah(
                        text = item.value,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HsIconButton(
                        onClick = {
                            onClearAmount.invoke()
                            stat(page = StatPage.TransactionInfo, event = StatEvent.TogglePrice)
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(28.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_trash_filled_20),
                            contentDescription = null,
                            tint = ComposeAppTheme.colors.leah,
                        )
                    }
                }

                is ReceiveModule.AdditionalData.Memo -> {
                    subhead2_grey(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        text = stringResource(R.string.Balance_Receive_Memo),
                    )
                    subhead1_leah(
                        text = item.value,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    ButtonSecondaryCircle(
                        modifier = Modifier
                            .height(28.dp)
                            .padding(end = 16.dp),
                        icon = R.drawable.ic_copy_20,
                        onClick = {
                            TextHelper.copyText(item.value)
                            HudHelper.showSuccessMessage(localView, R.string.Hud_Text_Copied)
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun AmountInputDialog(
    initialAmount: BigDecimal? = null,
    onDismissRequest: () -> Unit,
    onAmountConfirm: (BigDecimal?) -> Unit
) {
    val textState =
        remember { mutableStateOf(TextFieldValue(text = initialAmount?.toString() ?: "")) }
    val focusRequester = remember { FocusRequester() }
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(ComposeAppTheme.colors.lawrence)
        ) {
            VSpacer(24.dp)
            title3_leah(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.Balance_Receive_SetAmount),
            )
            VSpacer(16.dp)

            BasicTextField(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                value = textState.value,
                onValueChange = { value ->
                    textState.value = value
                },
                singleLine = true,
                textStyle = ColoredTextStyle(
                    color = ComposeAppTheme.colors.leah,
                    textStyle = ComposeAppTheme.typography.body
                ),
                decorationBox = { innerTextField ->
                    if (textState.value.text.isEmpty()) {
                        body_grey50("0")
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(ComposeAppTheme.colors.leah),
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                textState.value = textState.value.copy(
                    selection = TextRange(textState.value.text.length)
                )
            }

            HsDivider(modifier = Modifier.padding(horizontal = 24.dp))

            Row(
                modifier = Modifier
                    .padding(all = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                HsTextButton(
                    onClick = onDismissRequest
                ) {
                    body_jacob(stringResource(R.string.Button_Cancel).uppercase())
                }
                HSpacer(8.dp)
                HsTextButton(
                    onClick = { onAmountConfirm.invoke(textState.value.text.toBigDecimalOrNull()) }
                ) {
                    body_jacob(stringResource(R.string.Button_Confirm).uppercase())
                }
            }
        }
    }
}

@Composable
fun adaptiveIconPainterResource(@DrawableRes id: Int, @DrawableRes fallbackDrawable: Int): Painter {
    val res = LocalContext.current.resources
    val theme = LocalContext.current.theme

    val adaptiveIcon = ResourcesCompat.getDrawable(res, id, theme) as? AdaptiveIconDrawable
    return if (adaptiveIcon != null) {
        BitmapPainter(adaptiveIcon.toBitmap().asImageBitmap())
    } else {
        painterResource(fallbackDrawable)
    }
}