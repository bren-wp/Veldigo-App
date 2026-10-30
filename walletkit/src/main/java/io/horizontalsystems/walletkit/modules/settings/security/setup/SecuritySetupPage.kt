package io.horizontalsystems.walletkit.modules.settings.security.setup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.pin.SetPinPage
import io.horizontalsystems.walletkit.modules.settings.security.SecuritySettingsPage
import kotlinx.serialization.Serializable

/**
 * Reference screen 8: a pure Compose chooser, not a fabricated credentials form.
 * Real PIN and biometric configuration stay in existing security-protected flows.
 */
@Serializable
data object SecuritySetupPage : HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        SecuritySetupScreen(
            onBack = navigation::removeLastOrNull,
            biometricAvailable = App.systemInfoManager.biometricAuthSupported,
            pinAlreadySet = App.pinComponent.isPinSet,
            onBiometric = { navigation.slideFromRight(EnableFingerprintPage(finishOnSuccess = true)) },
            onPin = {
                if (App.pinComponent.isPinSet) {
                    navigation.slideFromRight(SecuritySettingsPage)
                } else {
                    navigation.slideFromRight(SetPinPage())
                }
            },
        )
    }
}

private val background = Color(0xFF030D0E)
private val tile = Color(0xFF152223)
private val mint = Color(0xFF41E6C7)
private val muted = Color(0xFF9FB5B9)
private val iconTile = Color(0xFF174840)

private enum class SetupIcon { Biometric, Pin, Password }

@Composable
internal fun SecuritySetupScreen(
    onBack: () -> Unit,
    biometricAvailable: Boolean,
    pinAlreadySet: Boolean,
    onBiometric: () -> Unit,
    onPin: () -> Unit,
) {
    var notice by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(background, Color(0xFF041010), background)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = Color.White,
                )
            }
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_veldigo_welcome_mark),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                )
                Text(
                    text = "Veldigo",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4f).sp,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.Veldigo_SecuritySetup_Title),
            color = Color.White,
            fontSize = 26.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.Veldigo_SecuritySetup_Description),
            color = muted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        Spacer(Modifier.height(26.dp))

        SecuritySetupChoice(
            icon = SetupIcon.Biometric,
            title = stringResource(
                if (biometricAvailable) R.string.Veldigo_SecuritySetup_Biometric
                else R.string.Veldigo_SecuritySetup_BiometricUnavailable
            ),
            description = stringResource(R.string.Veldigo_SecuritySetup_BiometricDetails),
            onClick = {
                if (biometricAvailable) onBiometric()
                else notice = R.string.Veldigo_SecuritySetup_NoBiometrics
            },
        )
        Spacer(Modifier.height(12.dp))
        SecuritySetupChoice(
            icon = SetupIcon.Pin,
            title = stringResource(R.string.Veldigo_SecuritySetup_Pin),
            description = stringResource(
                if (pinAlreadySet) R.string.Veldigo_SecuritySetup_PinAlreadySet
                else R.string.Veldigo_SecuritySetup_PinDetails
            ),
            onClick = onPin,
        )
        Spacer(Modifier.height(12.dp))
        SecuritySetupChoice(
            icon = SetupIcon.Password,
            title = stringResource(R.string.Veldigo_SecuritySetup_Password),
            description = stringResource(R.string.Veldigo_SecuritySetup_PasswordDetails),
            onClick = { notice = R.string.Veldigo_SecuritySetup_NoPassword },
        )
        Spacer(Modifier.height(22.dp))
    }

    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.Veldigo_SecuritySetup_NoticeTitle)) },
            text = { Text(stringResource(message)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}

@Composable
private fun SecuritySetupChoice(
    icon: SetupIcon,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .background(tile, RoundedCornerShape(13.dp))
            .border(1.dp, Color(0xFF213031), RoundedCornerShape(13.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(47.dp)
                .background(iconTile, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            SecurityChoiceGlyph(icon)
        }
        Spacer(Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = description,
                color = muted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Canvas(Modifier.size(18.dp)) {
            val line = 1.8.dp.toPx()
            drawLine(Color.White, Offset(size.width * .32f, size.height * .22f), Offset(size.width * .65f, size.height * .50f), line, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(size.width * .65f, size.height * .50f), Offset(size.width * .32f, size.height * .78f), line, cap = StrokeCap.Round)
        }
    }
}

/** Fully local vector iconography; real authentication remains in the existing PIN subsystem. */
@Composable
private fun SecurityChoiceGlyph(kind: SetupIcon) {
    Canvas(Modifier.size(27.dp)) {
        val w = size.width
        val h = size.height
        val line = 1.65.dp.toPx()
        when (kind) {
            SetupIcon.Biometric -> {
                // Nested elliptical arcs approximate the fingerprint glyph in the reference.
                for (factor in listOf(.88f, .69f, .50f, .31f)) {
                    val arcW = w * factor
                    val arcH = h * factor
                    drawArc(
                        color = mint,
                        startAngle = 195f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset((w - arcW) / 2f, (h - arcH) / 2f),
                        size = Size(arcW, arcH),
                        style = Stroke(line, cap = StrokeCap.Round),
                    )
                }
                drawLine(mint, Offset(w * .52f, h * .5f), Offset(w * .52f, h * .86f), line, cap = StrokeCap.Round)
            }
            SetupIcon.Pin -> {
                for (col in 0..2) for (row in 0..2) {
                    drawCircle(
                        color = mint,
                        radius = 2.dp.toPx(),
                        center = Offset(w * (.25f + col * .25f), h * (.25f + row * .25f)),
                    )
                }
            }
            SetupIcon.Password -> {
                drawArc(
                    color = mint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * .26f, h * .06f),
                    size = Size(w * .48f, h * .69f),
                    style = Stroke(line),
                )
                drawRoundRect(
                    color = mint,
                    topLeft = Offset(w * .16f, h * .44f),
                    size = Size(w * .68f, h * .46f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                    style = Stroke(line),
                )
                drawCircle(mint, radius = 1.65.dp.toPx(), center = Offset(w * .50f, h * .66f))
            }
        }
    }
}
