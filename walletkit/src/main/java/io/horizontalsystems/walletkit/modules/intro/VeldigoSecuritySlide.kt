package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R

/**
 * Veldigo Onboarding 2: security slide in the midnight-navy/mint family.
 * Geometry follows panel 2 of "Veldigo Crypto Wallet UI Mockup.png".
 * The original 3-D shield asset is not available as a standalone file; the
 * scenery below is original, locally drawn Compose geometry. It has not
 * passed a screenshot-diff review and must not be labelled 1:1 verified.
 */
@Composable
internal fun VeldigoSecuritySlide(
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val navy = VeldigoWelcomeTokens.Navy
    val mint = VeldigoWelcomeTokens.Mint
    val muted = VeldigoWelcomeTokens.Muted

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF061210), navy, Color(0xFF061A1A), navy)
                )
            )
    ) {
        val compact = maxHeight < 690.dp
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
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4).sp,
                )
            }

            Spacer(Modifier.height(if (compact) 12.dp else 20.dp))
            SecurityCardsArtwork(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (compact) Modifier.height(270.dp) else Modifier.weight(1f)),
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.Veldigo_Onboarding02_Title),
                color = Color.White,
                fontSize = 30.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.Veldigo_Onboarding02_Description),
                color = muted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(if (compact) 22.dp else 28.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics {
                    contentDescription = "Onboarding slide 2 of 3"
                },
            ) {
                repeat(3) { index ->
                    Spacer(
                        Modifier
                            .size(if (index == 1) 8.dp else 6.dp)
                            .background(
                                if (index == 1) mint else Color(0xFF405760),
                                RoundedCornerShape(100.dp),
                            )
                    )
                }
            }
            Spacer(Modifier.height(25.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mint,
                    contentColor = navy,
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

@Composable
private fun SecurityCardsArtwork(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val cx = w * 0.50f
        val cy = h * 0.53f
        val cw = minOf(w * 0.47f, h * 0.48f)
        val ch = cw * 1.30f
        val radius = 17.dp.toPx()
        val stroke = 1.3.dp.toPx()

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0C9377).copy(alpha = 0.24f),
                    Color(0xFF08382F).copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = w * 0.54f,
            ),
            radius = w * 0.54f,
            center = Offset(cx, cy),
        )

        // Back-left and back-right panels. Transformations are local to Canvas,
        // so they cannot influence screen hit targets or real wallet navigation.
        rotate(degrees = -19f, pivot = Offset(cx - cw * .57f, cy + ch * .02f)) {
            val origin = Offset(cx - cw * 1.12f, cy - ch * .52f)
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF092B2B), Color(0xFF061919))
                ),
                topLeft = origin,
                size = Size(cw, ch),
                cornerRadius = CornerRadius(radius),
            )
            drawRoundRect(
                color = Color(0xFF2B8D7D).copy(alpha = .58f),
                topLeft = origin,
                size = Size(cw, ch),
                cornerRadius = CornerRadius(radius),
                style = Stroke(stroke),
            )
        }
        rotate(degrees = 16f, pivot = Offset(cx + cw * .57f, cy + ch * .07f)) {
            val origin = Offset(cx + cw * .10f, cy - ch * .50f)
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF102F35), Color(0xFF051F23))
                ),
                topLeft = origin,
                size = Size(cw, ch),
                cornerRadius = CornerRadius(radius),
            )
            drawRoundRect(
                color = Color(0xFF348C84).copy(alpha = .64f),
                topLeft = origin,
                size = Size(cw, ch),
                cornerRadius = CornerRadius(radius),
                style = Stroke(stroke),
            )
        }

        val mainWidth = cw * 1.12f
        val mainHeight = ch * 1.07f
        val origin = Offset(cx - mainWidth * .50f, cy - mainHeight * .49f)
        drawRoundRect(
            color = Color.Black.copy(alpha = .30f),
            topLeft = origin + Offset(5.dp.toPx(), 9.dp.toPx()),
            size = Size(mainWidth, mainHeight),
            cornerRadius = CornerRadius(radius),
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                listOf(Color(0xFF183D39), Color(0xFF071C1F), Color(0xFF092A2A))
            ),
            topLeft = origin,
            size = Size(mainWidth, mainHeight),
            cornerRadius = CornerRadius(radius),
        )
        drawRoundRect(
            color = Color(0xFF4BD4B5).copy(alpha = .62f),
            topLeft = origin,
            size = Size(mainWidth, mainHeight),
            cornerRadius = CornerRadius(radius),
            style = Stroke(1.6.dp.toPx()),
        )

        val shieldWidth = mainWidth * .55f
        val shieldHeight = mainHeight * .54f
        val top = cy - shieldHeight * .55f
        val path = Path().apply {
            moveTo(cx, top)
            lineTo(cx + shieldWidth * .48f, top + shieldHeight * .21f)
            lineTo(cx + shieldWidth * .42f, top + shieldHeight * .64f)
            quadraticTo(cx + shieldWidth * .30f, top + shieldHeight * .91f, cx, top + shieldHeight)
            quadraticTo(cx - shieldWidth * .30f, top + shieldHeight * .91f, cx - shieldWidth * .42f, top + shieldHeight * .64f)
            lineTo(cx - shieldWidth * .48f, top + shieldHeight * .21f)
            close()
        }
        drawPath(
            path = path,
            color = Color(0xFF0A735F),
        )
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF71FFE0), Color(0xFF20BC95), Color(0xFF005A4C)),
                startY = top,
                endY = top + shieldHeight,
            ),
            style = Stroke(width = 5.dp.toPx()),
        )
        drawPath(
            path = path,
            color = Color(0xFFA0FFE9).copy(alpha = .35f),
            style = Stroke(width = 1.3.dp.toPx()),
        )
    }
}
