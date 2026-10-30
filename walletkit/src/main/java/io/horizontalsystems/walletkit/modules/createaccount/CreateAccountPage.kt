package io.horizontalsystems.walletkit.modules.createaccount

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.NavigationType
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.modules.importwallet.ImportWalletPage
import io.horizontalsystems.walletkit.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.walletkit.modules.manageaccounts.PassKeyTermsPage
import io.horizontalsystems.walletkit.modules.nav3.EntryPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.ui.compose.ComposeAppTheme
import io.horizontalsystems.walletkit.uiv3.components.BoxBordered
import io.horizontalsystems.walletkit.uiv3.components.cell.CellLeftImage
import io.horizontalsystems.walletkit.uiv3.components.cell.CellMiddleInfo
import io.horizontalsystems.walletkit.uiv3.components.cell.CellPrimary
import io.horizontalsystems.walletkit.uiv3.components.cell.CellRightNavigation
import io.horizontalsystems.walletkit.uiv3.components.cell.HSString
import io.horizontalsystems.walletkit.uiv3.components.cell.ImageType
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountPage(val input: ManageAccountsModule.Input? = null) : HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        CreateAccountScreen(navigation, input)
    }
}

private val walletBackground = Color(0xFF030F0F)
private val walletMint = Color(0xFF41E6C7)
private val walletMuted = Color(0xFF99AFB3)
private val walletPanel = Color(0xFF101C1D)

/**
 * Screen 5 in the approved Veldigo 21-screen reference.
 * All account and key operations remain in their existing secure flows.
 */
@Composable
fun CreateAccountScreen(navigation: HSNavigation, input: ManageAccountsModule.Input?) {
    var showHardwareNotice by remember { mutableStateOf(false) }
    var showExtraMethods by remember { mutableStateOf(false) }

    val openPasskey: () -> Unit = {
        if (!App.localStorage.passkeyTermsAccepted) {
            navigation.slideFromRight(PassKeyTermsPage(CreateAccountPasskeyPage(input)))
        } else {
            navigation.slideFromRight(CreateAccountPasskeyPage(input))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(walletBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        // The reference contains a left back arrow and centered brand mark.
        Box(modifier = Modifier.fillMaxWidth().height(54.dp)) {
            IconButton(
                onClick = navigation::removeLastOrNull,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                WalletSetupIcon(kind = WalletSetupIconKind.Back, tint = Color.White)
            }
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_veldigo_welcome_mark),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = stringResource(R.string.App_Name),
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4f).sp,
                )
            }
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconButton(
                    onClick = { showExtraMethods = true },
                    modifier = Modifier.semantics {
                        contentDescription = "More wallet creation methods"
                    },
                ) {
                    WalletSetupIcon(kind = WalletSetupIconKind.More, tint = walletMuted)
                }
                DropdownMenu(
                    expanded = showExtraMethods,
                    onDismissRequest = { showExtraMethods = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.CreateNewWallet_Passkey)) },
                        onClick = {
                            showExtraMethods = false
                            openPasskey()
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.Veldigo_CreateWallet_Title),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 31.sp,
            color = Color.White,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.Veldigo_CreateWallet_Subtitle),
            color = walletMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
        Spacer(Modifier.height(23.dp))

        WalletSetupChoice(
            title = stringResource(R.string.Veldigo_CreateWallet_New),
            subtitle = stringResource(R.string.Veldigo_CreateWallet_NewDetails),
            kind = WalletSetupIconKind.New,
            onClick = { navigation.slideFromRight(CreateAccountStandardPage(input)) },
        )
        Spacer(Modifier.height(12.dp))
        WalletSetupChoice(
            title = stringResource(R.string.Veldigo_CreateWallet_Import),
            subtitle = stringResource(R.string.Veldigo_CreateWallet_ImportDetails),
            kind = WalletSetupIconKind.Import,
            onClick = {
                navigation.navigateWithTermsAccepted(
                    screen = ImportWalletPage(input),
                    navigationType = NavigationType.SlideFromRight,
                    statPageFrom = StatPage.NewWallet,
                    statPageTo = StatPage.ImportWallet,
                )
            },
        )
        Spacer(Modifier.height(12.dp))
        WalletSetupChoice(
            title = stringResource(R.string.Veldigo_CreateWallet_Hardware),
            subtitle = stringResource(R.string.Veldigo_CreateWallet_HardwareDetails),
            kind = WalletSetupIconKind.Hardware,
            onClick = { showHardwareNotice = true },
        )
    }

    if (showHardwareNotice) {
        AlertDialog(
            onDismissRequest = { showHardwareNotice = false },
            title = { Text(stringResource(R.string.Veldigo_CreateWallet_HardwareUnavailableTitle)) },
            text = { Text(stringResource(R.string.Veldigo_CreateWallet_HardwareUnavailableBody)) },
            confirmButton = {
                TextButton(onClick = { showHardwareNotice = false }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}

private enum class WalletSetupIconKind { Back, More, New, Import, Hardware, Chevron }

@Composable
private fun WalletSetupChoice(
    title: String,
    subtitle: String,
    kind: WalletSetupIconKind,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0D1918), walletPanel, Color(0xFF0D1918))
                ),
                RoundedCornerShape(15.dp),
            )
            .border(1.dp, Color(0xFF1F3734), RoundedCornerShape(15.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF123F38), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            WalletSetupIcon(kind = kind, tint = walletMint)
        }
        Spacer(Modifier.width(17.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = walletMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(6.dp))
        WalletSetupIcon(kind = WalletSetupIconKind.Chevron, tint = Color(0xFFD4E8E3))
    }
}

/** Local vector instructions prevent emoji font differences across Android devices. */
@Composable
private fun WalletSetupIcon(
    kind: WalletSetupIconKind,
    tint: Color,
) {
    Canvas(Modifier.size(if (kind == WalletSetupIconKind.Back) 23.dp else 22.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.7.dp.toPx()
        when (kind) {
            WalletSetupIconKind.Back -> {
                drawLine(tint, Offset(w * .72f, h * .50f), Offset(w * .25f, h * .50f), sw, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * .25f, h * .50f), Offset(w * .48f, h * .27f), sw, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * .25f, h * .50f), Offset(w * .48f, h * .73f), sw, cap = StrokeCap.Round)
            }
            WalletSetupIconKind.More -> {
                for (v in listOf(.23f, .5f, .77f)) {
                    drawCircle(tint, 1.8.dp.toPx(), Offset(w * v, h * .5f))
                }
            }
            WalletSetupIconKind.Chevron -> {
                drawLine(tint, Offset(w * .38f, h * .26f), Offset(w * .62f, h * .50f), sw, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * .62f, h * .50f), Offset(w * .38f, h * .74f), sw, cap = StrokeCap.Round)
            }
            WalletSetupIconKind.New -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * .27f, h * .43f),
                    size = Size(w * .46f, h * .42f),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = Stroke(sw),
                )
                drawLine(tint, Offset(w * .37f, h * .43f), Offset(w * .37f, h * .27f), sw)
                drawLine(tint, Offset(w * .37f, h * .27f), Offset(w * .62f, h * .27f), sw)
                drawLine(tint, Offset(w * .62f, h * .27f), Offset(w * .62f, h * .43f), sw)
                drawCircle(tint, 1.4.dp.toPx(), Offset(w * .5f, h * .60f))
            }
            WalletSetupIconKind.Import -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * .15f, h * .28f),
                    size = Size(w * .70f, h * .60f),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = Stroke(sw),
                )
                drawLine(tint, Offset(w * .52f, h * .12f), Offset(w * .52f, h * .53f), sw, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * .39f, h * .40f), Offset(w * .52f, h * .53f), sw, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * .52f, h * .53f), Offset(w * .65f, h * .40f), sw, cap = StrokeCap.Round)
            }
            WalletSetupIconKind.Hardware -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * .30f, h * .10f),
                    size = Size(w * .40f, h * .80f),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = Stroke(sw),
                )
                drawLine(tint, Offset(w * .39f, h * .70f), Offset(w * .61f, h * .70f), sw, cap = StrokeCap.Round)
                drawCircle(tint, 1.2.dp.toPx(), Offset(w * .5f, h * .20f))
            }
        }
    }
}

// This existing helper is intentionally retained: ImportWalletPage uses it.
@Composable
fun WalletType(
    icon: Painter,
    title: HSString,
    subtitle: HSString,
    borderTop: Boolean,
    onClick: () -> Unit
) {
    BoxBordered(top = borderTop) {
        CellPrimary(
            left = {
                CellLeftImage(
                    painter = icon,
                    type = ImageType.Rectangle,
                    size = 24
                )
            },
            middle = {
                CellMiddleInfo(
                    title = title,
                    subtitle = subtitle,
                )
            },
            right = {
                CellRightNavigation()
            },
            onClick = onClick
        )
    }
}

@Composable
@Preview
fun Preview_CreateAccountScreen() {
    ComposeAppTheme {
        CreateAccountScreen(HSNavigation(NavBackStack(EntryPage)), null)
    }
}
