package io.horizontalsystems.walletkit.modules.settings.security.setup

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.nav3.EntryPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.pin.SetPinPage
import kotlinx.serialization.Serializable

/**
 * Screen 9 of the Veldigo Mint/Navy reference.
 * The fingerprint illustration is decorative; enabling biometrics requires a
 * genuine successful Android BiometricPrompt, a secure device, and an existing PIN.
 */
@Serializable
data class EnableFingerprintPage(val finishOnSuccess: Boolean = false) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        EnableFingerprintScreen(
            onBack = navigation::removeLastOrNull,
            onPinRequired = { navigation.slideFromRight(SetPinPage()) },
            onAuthenticated = {
                if (finishOnSuccess) navigation.removeLastUntil(EntryPage::class, inclusive = false)
                else navigation.removeLastOrNull()
            },
        )
    }
}

internal enum class BiometricSetupPreflight {
    READY, DEVICE_LOCK_REQUIRED, PIN_REQUIRED, BIOMETRIC_UNAVAILABLE
}

/** Pure guard used both before Android's prompt and again in its success callback. */
internal object BiometricSetupGate {
    fun preflight(
        deviceSecure: Boolean,
        pinSet: Boolean,
        biometricAvailable: Boolean,
    ): BiometricSetupPreflight = when {
        !deviceSecure -> BiometricSetupPreflight.DEVICE_LOCK_REQUIRED
        !pinSet -> BiometricSetupPreflight.PIN_REQUIRED
        !biometricAvailable -> BiometricSetupPreflight.BIOMETRIC_UNAVAILABLE
        else -> BiometricSetupPreflight.READY
    }

    fun mayEnableAfterResult(
        deviceSecure: Boolean,
        pinSet: Boolean,
        biometricAvailable: Boolean,
        authenticated: Boolean,
    ): Boolean = authenticated &&
        preflight(deviceSecure, pinSet, biometricAvailable) == BiometricSetupPreflight.READY
}

private val bg = VeldigoHomePalette.Background
private val mint = VeldigoHomePalette.Mint
private val muted = VeldigoHomePalette.Muted

private fun Context.findFragmentActivity(): FragmentActivity? {
    var node: Context? = this
    while (node != null) {
        if (node is FragmentActivity) return node
        node = (node as? ContextWrapper)?.baseContext
    }
    return null
}

@Composable
internal fun EnableFingerprintScreen(
    onBack: () -> Unit,
    onPinRequired: () -> Unit,
    onAuthenticated: () -> Unit,
) {
    val activity = LocalContext.current.findFragmentActivity()
    val completed by rememberUpdatedState(onAuthenticated)
    var authenticating by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<Int?>(null) }

    // Do not store a successful biometric result as a boolean navigation argument.
    // On new compositions the registered OS callback uses the current completion action.
    val prompt = remember(activity) {
        activity?.let { host ->
            BiometricPrompt(
                host,
                ContextCompat.getMainExecutor(host),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        // Ignore a late callback after the page was dismissed or a
                        // previous request was cancelled during configuration change.
                        if (!authenticating) return
                        authenticating = false
                        val accepted = BiometricSetupGate.mayEnableAfterResult(
                            deviceSecure = !App.systemInfoManager.isSystemLockOff,
                            pinSet = App.pinComponent.isPinSet,
                            biometricAvailable = App.systemInfoManager.biometricAuthSupported,
                            authenticated = true,
                        )
                        if (accepted) {
                            App.pinComponent.isBiometricAuthEnabled = true
                            completed()
                        } else {
                            notice = R.string.Veldigo_Fingerprint_Unavailable
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        authenticating = false
                        // Cancel or back does not enable the setting and is not an error.
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                            errorCode != BiometricPrompt.ERROR_CANCELED
                        ) {
                            notice = R.string.Veldigo_Fingerprint_Failed
                        }
                    }
                },
            )
        }
    }
    DisposableEffect(prompt) {
        onDispose {
            prompt?.cancelAuthentication()
            authenticating = false
        }
    }

    val promptTitle = stringResource(R.string.Veldigo_Fingerprint_PromptTitle)
    val cancelLabel = stringResource(R.string.Button_Cancel)
    val promptInfo = remember(promptTitle, cancelLabel) {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(promptTitle)
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .setNegativeButtonText(cancelLabel)
            .build()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        val compact = maxHeight < 650.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
            }

            if (compact) Spacer(Modifier.height(42.dp))
            else Spacer(Modifier.weight(0.44f))

            FingerprintArt()

            Spacer(Modifier.height(25.dp))
            Text(
                text = stringResource(R.string.Veldigo_Fingerprint_Title),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                lineHeight = 31.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(11.dp))
            Text(
                text = stringResource(R.string.Veldigo_Fingerprint_Description),
                color = muted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            if (compact) Spacer(Modifier.height(60.dp))
            else Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (authenticating) return@Button
                    val state = BiometricSetupGate.preflight(
                        deviceSecure = !App.systemInfoManager.isSystemLockOff,
                        pinSet = App.pinComponent.isPinSet,
                        biometricAvailable = App.systemInfoManager.biometricAuthSupported,
                    )
                    when (state) {
                        BiometricSetupPreflight.DEVICE_LOCK_REQUIRED ->
                            notice = R.string.Veldigo_Fingerprint_DeviceLock
                        BiometricSetupPreflight.PIN_REQUIRED -> onPinRequired()
                        BiometricSetupPreflight.BIOMETRIC_UNAVAILABLE ->
                            notice = R.string.Veldigo_Fingerprint_Unavailable
                        BiometricSetupPreflight.READY -> {
                            if (prompt == null) {
                                notice = R.string.Veldigo_Fingerprint_Unavailable
                            } else {
                                authenticating = true
                                try {
                                    prompt.authenticate(promptInfo)
                                } catch (_: RuntimeException) {
                                    authenticating = false
                                    notice = R.string.Veldigo_Fingerprint_Failed
                                }
                            }
                        }
                    }
                },
                enabled = !authenticating,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mint,
                    contentColor = bg,
                    disabledContainerColor = VeldigoHomePalette.Border,
                ),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_Fingerprint_Continue),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    notice?.let { messageId ->
        AlertDialog(
            onDismissRequest = { notice = null },
            title = { Text(stringResource(R.string.Veldigo_Fingerprint_NoticeTitle)) },
            text = { Text(stringResource(messageId)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}

/** Original local vector paths approximate reference art; no biometric image data is read. */
@Composable
private fun FingerprintArt() {
    Canvas(
        modifier = Modifier
            .size(175.dp)
            .semantics { contentDescription = "Fingerprint authentication illustration" },
    ) {
        val side = size.width
        val stroke = 2.6.dp.toPx()
        drawCircle(
            color = mint.copy(alpha = .80f),
            radius = side * .465f,
            center = Offset(side / 2f, side / 2f),
            style = Stroke(1.2.dp.toPx()),
        )
        // Smooth nested U-curves and their lower trailing ridges, centered in the ring.
        for (i in 0..4) {
            val inset = i * .073f
            val left = side * (.20f + inset)
            val right = side * (.80f - inset)
            val top = side * (.24f + inset * .32f)
            val bottom = side * (.76f - inset * .28f)
            val ridge = Path().apply {
                moveTo(left, side * (.62f + inset * .1f))
                cubicTo(left, top + side * .05f, left + side * .09f, top, side * .50f, top)
                cubicTo(right - side * .09f, top, right, top + side * .08f, right, side * .61f)
                cubicTo(right, bottom - side * .03f, right - side * .05f, bottom, right - side * .10f, bottom + side * .04f)
            }
            drawPath(ridge, mint, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        drawLine(
            color = mint,
            start = Offset(side * .50f, side * .57f),
            end = Offset(side * .50f, side * .85f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}
