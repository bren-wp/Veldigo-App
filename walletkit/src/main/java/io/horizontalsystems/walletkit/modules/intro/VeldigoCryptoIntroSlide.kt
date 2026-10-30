package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R

/**
 * Third visual panel of the approved mint Veldigo multi-screen concept.
 *
 * Decorative coin art is drawn locally; it never displays a wallet balance or
 * claims a network is enabled. Exact screenshot parity requires device capture.
 */
@Composable
internal fun VeldigoCryptoIntroSlide(
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val night = VeldigoWelcomeTokens.Navy
    val mint = VeldigoWelcomeTokens.Mint
    val muted = VeldigoWelcomeTokens.Muted

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF030B0C), night, Color(0xFF030F13))
                )
            )
    ) {
        // Avoid clipping the CTA and skip action on short or large-font devices.
        val compact = maxHeight < 710.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (compact) Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                )
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(38.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_veldigo_welcome_mark),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "Veldigo",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp,
                    letterSpacing = (-0.4f).sp,
                )
            }
            Spacer(Modifier.height(if (compact) 10.dp else 18.dp))
            CryptoCoinCluster(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (compact) Modifier.height(280.dp) else Modifier.weight(1f))
                    .clearAndSetSemantics { },
            )
            Spacer(Modifier.height(if (compact) 12.dp else 18.dp))
            Text(
                text = stringResource(R.string.Veldigo_Onboarding03_Title),
                color = Color.White,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 35.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.Veldigo_Onboarding03_Description),
                color = muted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(if (compact) 18.dp else 28.dp))

            VeldigoIntroProgress(completed = 1)

            Spacer(Modifier.height(25.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mint,
                    contentColor = night,
                ),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_Onboarding02_Next),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TextButton(
                onClick = onSkip,
                modifier = Modifier.height(48.dp),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_Onboarding02_Skip),
                    color = muted,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

private data class CoinPlacement(
    val x: Float,
    val y: Float,
    val diameter: Dp,
    val symbol: String,
    val isEthereum: Boolean = false,
    val isGreen: Boolean = false,
)

/** Positions are proportional to the artwork, with bounded physical coin sizes. */
private val coins = listOf(
    CoinPlacement(.42f, .06f, 68.dp, "₿"),
    CoinPlacement(.70f, .25f, 74.dp, "₿"),
    CoinPlacement(.15f, .35f, 83.dp, "₿"),
    CoinPlacement(.51f, .58f, 85.dp, "", isEthereum = true),
    CoinPlacement(.87f, .28f, 49.dp, "", isEthereum = true),
    CoinPlacement(.04f, .28f, 41.dp, "₿", isGreen = true),
    CoinPlacement(.08f, .70f, 39.dp, "♮", isGreen = true),
    CoinPlacement(.76f, .68f, 42.dp, "◇", isGreen = true),
    CoinPlacement(.84f, .08f, 24.dp, "₿"),
    CoinPlacement(.05f, .57f, 25.dp, "₿"),
    CoinPlacement(.61f, .12f, 22.dp, ""),
    CoinPlacement(.31f, .78f, 18.dp, ""),
)

@Composable
private fun CryptoCoinCluster(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF125447).copy(alpha = .34f),
                        Color(0xFF082820).copy(alpha = .11f),
                        Color.Transparent,
                    ),
                    center = Offset(w * .49f, h * .47f),
                    radius = w * .56f,
                ),
                center = Offset(w * .49f, h * .47f),
                radius = w * .56f,
            )
            listOf(
                .11f to .19f, .24f to .06f, .58f to .03f,
                .94f to .43f, .18f to .92f, .79f to .86f,
            ).forEach { (x, y) ->
                drawCircle(
                    color = Color(0xFF42D7BB).copy(alpha = .23f),
                    radius = 1.7.dp.toPx(),
                    center = Offset(w * x, h * y),
                )
            }
        }

        coins.forEach { coin ->
            val x = (maxWidth - coin.diameter) * coin.x
            val y = (maxHeight - coin.diameter) * coin.y
            CryptoIllustrationCoin(
                coin = coin,
                modifier = Modifier.offset(x = x, y = y),
            )
        }
    }
}

@Composable
private fun CryptoIllustrationCoin(
    coin: CoinPlacement,
    modifier: Modifier = Modifier,
) {
    val blue = coin.isEthereum
    val green = coin.isGreen
    val background = when {
        blue -> listOf(Color(0xFF347BE5), Color(0xFF213CBE), Color(0xFF09184D))
        green -> listOf(Color(0xFF217C6B), Color(0xFF0B483C), Color(0xFF062621))
        else -> listOf(Color(0xFF43545B), Color(0xFF202C32), Color(0xFF0D191E))
    }
    Box(
        modifier = modifier
            .size(coin.diameter)
            .shadow(6.dp, CircleShape)
            .background(Brush.linearGradient(background), CircleShape)
            .border(
                1.3.dp,
                if (blue) Color(0xFF5578EE) else Color(0xFF4B7072),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (blue) {
            EthereumDiamond(Modifier.size(coin.diameter * .46f))
        } else if (coin.symbol.isNotEmpty()) {
            Text(
                text = coin.symbol,
                color = if (green) Color(0xFF6CF0D0) else Color(0xFFF2F7F9),
                fontSize = (coin.diameter.value * .50f).sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EthereumDiamond(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val upper = Path().apply {
            moveTo(w * .50f, 0f)
            lineTo(w, h * .62f)
            lineTo(w * .50f, h * .48f)
            lineTo(0f, h * .62f)
            close()
        }
        drawPath(upper, Color.White)
        val lower = Path().apply {
            moveTo(0f, h * .70f)
            lineTo(w * .50f, h)
            lineTo(w, h * .70f)
            lineTo(w * .50f, h * .83f)
            close()
        }
        drawPath(lower, Color(0xFFC5DCFF))
        drawLine(
            color = Color(0xFFB9D6FF),
            start = Offset(w * .50f, 0f),
            end = Offset(w * .50f, h * .48f),
            strokeWidth = 1.dp.toPx(),
        )
    }
}
