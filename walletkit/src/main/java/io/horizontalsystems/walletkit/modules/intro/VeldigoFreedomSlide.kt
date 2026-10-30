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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R

/**
 * Veldigo's fourth introduction screen (Freedom Without Borders).
 *
 * Layout follows the approved fourth handset in "Veldigo Crypto Wallet UI
 * Mockup.png": wordmark, large globe, centered heading and supporting copy,
 * four dots, one mint Get Started action. No reference bitmap is used as UI.
 * Globe contours are locally drawn approximations, pending real device diff.
 */
@Composable
internal fun VeldigoFreedomSlide(onGetStarted: () -> Unit) {
    val navy = VeldigoWelcomeTokens.Navy
    val mint = VeldigoWelcomeTokens.Mint
    val muted = VeldigoWelcomeTokens.Muted

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF030D0D), navy, Color(0xFF051311))
                )
            )
    ) {
        // On small/large-font devices scroll rather than overlap the final CTA.
        val compact = maxHeight < 695.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 16.dp),
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
            FreedomGlobe(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (compact) Modifier.height(260.dp) else Modifier.weight(1f))
                    .clearAndSetSemantics { },
            )
            Spacer(Modifier.height(if (compact) 8.dp else 12.dp))
            Text(
                text = stringResource(R.string.Veldigo_Onboarding04_Title),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 29.sp,
                lineHeight = 35.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(13.dp))
            Text(
                text = stringResource(R.string.Veldigo_Onboarding04_Description),
                color = muted,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(if (compact) 16.dp else 24.dp))

            // The visual reference for panel 4 lights only the first dot.
            // Semantics still correctly announce the final fourth step.
            VeldigoIntroProgress(completed = 0, semanticStep = 4)
            Spacer(Modifier.height(25.dp))
            Button(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mint,
                    contentColor = navy,
                ),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_Onboarding04_GetStarted),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FreedomGlobe(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val center = Offset(w * .50f, h * .53f)
        val radius = minOf(w * .43f, h * .42f)

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF0A7467).copy(alpha = .25f), Color.Transparent),
                center = Offset(center.x - radius * .42f, center.y - radius * .38f),
                radius = radius * 1.65f,
            ),
            radius = radius * 1.65f,
            center = Offset(center.x - radius * .42f, center.y - radius * .38f),
        )

        val sphere = Path().apply {
            addOval(Rect(
                Offset(center.x - radius, center.y - radius),
                Offset(center.x + radius, center.y + radius)
            ))
        }

        // Normalize geographical features to 0..1 of the visible circular disk.
        fun contour(vararg xy: Float): Path = Path().apply {
            if (xy.size < 4) return@apply
            fun point(i: Int) = Offset(
                center.x + (xy[i] - .5f) * radius * 2f,
                center.y + (xy[i + 1] - .5f) * radius * 2f,
            )
            val first = point(0)
            moveTo(first.x, first.y)
            for (i in 2 until xy.size step 2) {
                val p = point(i)
                lineTo(p.x, p.y)
            }
            close()
        }

        clipPath(sphere) {
            drawCircle(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF0B7267), Color(0xFF032C39), Color(0xFF06151A)),
                    start = Offset(center.x - radius, center.y - radius),
                    end = Offset(center.x + radius, center.y + radius),
                ),
                radius = radius,
                center = center,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF9BFBE0).copy(alpha = .48f), Color.Transparent),
                    center = Offset(center.x - radius * .68f, center.y - radius * .86f),
                    radius = radius * 1.12f,
                ),
                radius = radius * 1.12f,
                center = Offset(center.x - radius * .68f, center.y - radius * .86f),
            )

            val grid = Color(0xFF4DB3AB).copy(alpha = .18f)
            val line = 1.dp.toPx()

            // Meridians curved into the sphere. Each ellipse is clipped by the globe.
            for (fraction in listOf(.35f, .66f, .92f)) {
                drawOval(
                    color = grid,
                    topLeft = Offset(center.x - radius * fraction, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(
                        radius * fraction * 2f, radius * 2f
                    ),
                    style = Stroke(line),
                )
            }
            for (fraction in listOf(-.65f, -.30f, .05f, .39f, .72f)) {
                val latitudeY = center.y + fraction * radius
                val lat = Path().apply {
                    moveTo(center.x - radius, latitudeY)
                    quadraticTo(
                        center.x, latitudeY + radius * .13f * (1 - fraction * fraction),
                        center.x + radius, latitudeY,
                    )
                }
                drawPath(lat, grid, style = Stroke(line))
            }

            // Stylized continents, rendered as original vector shapes rather than a
            // screenshot. Accurate coastline mapping is a future visual QA item.
            drawPath(
                contour(
                    .02f,.12f, .22f,.07f, .36f,.11f, .43f,.17f,
                    .35f,.24f, .29f,.35f, .24f,.43f, .17f,.42f,
                    .12f,.53f, .04f,.47f,
                ),
                color = Color(0xFF48BEAB).copy(alpha = .70f),
            )
            drawPath(
                contour(
                    .10f,.48f, .20f,.48f, .25f,.60f, .29f,.69f,
                    .24f,.81f, .22f,.96f, .17f,.82f, .12f,.66f,
                ),
                color = Color(0xFF1B796F).copy(alpha = .67f),
            )
            drawPath(
                contour(
                    .43f,.12f, .60f,.08f, .67f,.14f, .76f,.13f,
                    .89f,.24f, .96f,.37f, .88f,.47f, .77f,.44f,
                    .70f,.54f, .60f,.44f, .52f,.41f, .48f,.31f,
                    .40f,.27f,
                ),
                color = Color(0xFF165E60).copy(alpha = .80f),
            )
            drawPath(
                contour(
                    .48f,.46f, .60f,.44f, .71f,.52f, .68f,.67f,
                    .61f,.79f, .57f,.91f, .47f,.74f, .43f,.59f,
                ),
                color = Color(0xFF0A4C55).copy(alpha = .88f),
            )
            drawPath(
                contour(
                    .80f,.75f, .94f,.77f, .99f,.87f, .90f,.94f,
                    .80f,.87f,
                ),
                color = Color(0xFF146368).copy(alpha = .65f),
            )

            // Subtle terminator shadow, much darker on the right/bottom edge.
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.Transparent, Color(0xFF020E13).copy(alpha = .88f)),
                    center = Offset(center.x - radius * .32f, center.y - radius * .40f),
                    radius = radius * 1.62f,
                ),
                center = center,
                radius = radius * 1.75f,
            )
        }

        drawCircle(
            color = Color(0xFF2C8991).copy(alpha = .38f),
            center = center,
            radius = radius,
            style = Stroke(1.4.dp.toPx()),
        )
    }
}
