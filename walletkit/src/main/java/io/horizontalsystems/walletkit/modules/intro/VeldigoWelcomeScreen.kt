package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R

/** First-launch reference: first of four navy/mint screenshots (384 x 1024 panel).
 * This is a real interactive Compose screen, not an embedded screenshot.
 */
internal object VeldigoWelcomeTokens {
    val Navy = Color(0xFF071421)
    val Mint = Color(0xFF41E6C7)
    val Muted = Color(0xFFB5C6D7)
    val Border = Color(0xFF38566A)
    val Surface = Color(0xFF112337)
}

@Composable
internal fun VeldigoWelcomeScreen(
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit,
    onLanguage: () -> Unit,
) {
    val navy = VeldigoWelcomeTokens.Navy
    val mint = VeldigoWelcomeTokens.Mint
    val muted = VeldigoWelcomeTokens.Muted

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(navy, Color(0xFF071B2C), navy))),
    ) {
        // Source photograph is not a standalone asset. Keep scenery behind live UI
        // and report its image-level deviation until original artwork is available.
        WelcomeScenery(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f)
                .padding(top = 44.dp),
        )

        val compact = maxHeight < 805.dp
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(scroll) else Modifier)
                .padding(horizontal = 23.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onLanguage) {
                    GlobeGlyph()
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.Veldigo_Welcome_Language),
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("⌄", color = Color.White, fontSize = 17.sp)
                }
            }
            Spacer(Modifier.height(15.dp))
            Image(
                painter = painterResource(R.drawable.ic_veldigo_welcome_mark),
                contentDescription = null,
                modifier = Modifier.size(132.dp),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "VELDIGO",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "C R Y P T O  W A L L E T",
                color = muted,
                fontSize = 9.sp,
            )

            if (compact) {
                Spacer(Modifier.height(80.dp))
            } else {
                Spacer(Modifier.weight(1f))
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.Veldigo_Welcome_Title1),
                    color = Color.White,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8f).sp,
                )
                Text(
                    text = stringResource(R.string.Veldigo_Welcome_Title2),
                    color = mint,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8f).sp,
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Welcome_Description),
                    color = muted,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            }
            Spacer(Modifier.height(26.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                WelcomeFeature(
                    label = stringResource(R.string.Veldigo_Welcome_SelfCustody),
                    caption = stringResource(R.string.Veldigo_Welcome_HoldKeys),
                    index = 0,
                )
                WelcomeFeature(
                    label = stringResource(R.string.Veldigo_Welcome_MultiChain),
                    caption = stringResource(R.string.Veldigo_Welcome_AllAssets),
                    index = 1,
                )
                WelcomeFeature(
                    label = stringResource(R.string.Veldigo_Welcome_BuiltForYou),
                    caption = stringResource(R.string.Veldigo_Welcome_Simple),
                    index = 2,
                )
            }
            Spacer(Modifier.height(35.dp))
            Button(
                onClick = onCreateWallet,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mint, contentColor = navy),
            ) {
                Text(
                    text = stringResource(R.string.Veldigo_Welcome_CreateWallet),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(10.dp))
                Text("→", fontSize = 23.sp)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onImportWallet,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, VeldigoWelcomeTokens.Border),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = VeldigoWelcomeTokens.Surface.copy(alpha = 0.42f),
                    contentColor = Color.White,
                ),
            ) {
                Text("⇩", fontSize = 23.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.Veldigo_Welcome_ImportWallet),
                    fontSize = 14.sp,
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.Veldigo_Welcome_Legal),
                color = muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(if (compact) 25.dp else 56.dp))
        }
    }
}

@Composable
private fun WelcomeFeature(label: String, caption: String, index: Int) {
    Column(
        modifier = Modifier.width(105.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Canvas(Modifier.size(36.dp).background(Color(0xFF0B3440), RoundedCornerShape(12.dp))) {
            val c = VeldigoWelcomeTokens.Mint
            val w = size.width
            val h = size.height
            when (index) {
                0 -> {
                    val shield = Path().apply {
                        moveTo(w * .50f, h * .16f)
                        lineTo(w * .76f, h * .28f)
                        lineTo(w * .73f, h * .62f)
                        lineTo(w * .50f, h * .83f)
                        lineTo(w * .27f, h * .62f)
                        lineTo(w * .24f, h * .28f)
                        close()
                    }
                    drawPath(shield, c, style = Stroke(2.dp.toPx()))
                }
                1 -> {
                    drawLine(c, Offset(w * .30f, h * .67f), Offset(w * .53f, h * .44f), 2.5.dp.toPx())
                    drawCircle(c, w * .16f, Offset(w * .32f, h * .64f), style = Stroke(2.dp.toPx()))
                    drawCircle(c, w * .16f, Offset(w * .67f, h * .32f), style = Stroke(2.dp.toPx()))
                }
                else -> {
                    drawLine(c, Offset(w * .30f, h * .73f), Offset(w * .30f, h * .51f), 3.dp.toPx())
                    drawLine(c, Offset(w * .50f, h * .73f), Offset(w * .50f, h * .33f), 3.dp.toPx())
                    drawLine(c, Offset(w * .70f, h * .73f), Offset(w * .70f, h * .22f), 3.dp.toPx())
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(label, color = Color.White, fontSize = 12.sp, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(caption, color = VeldigoWelcomeTokens.Muted, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun GlobeGlyph() {
    Canvas(Modifier.size(19.dp)) {
        val stroke = 1.3.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color.White, radius = size.minDimension * .43f, center = center, style = Stroke(stroke))
        drawOval(
            color = Color.White,
            topLeft = Offset(size.width * .34f, size.height * .07f),
            size = androidx.compose.ui.geometry.Size(size.width * .32f, size.height * .86f),
            style = Stroke(stroke),
        )
        drawLine(Color.White, Offset(0f, center.y), Offset(size.width, center.y), stroke)
    }
}

@Composable
private fun WelcomeScenery(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        // Moonlight and distant ridges remain decorative and never carry wallet data.
        val moon = Offset(w * .97f, h * .39f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF42617B), Color(0xFF183246), Color.Transparent),
                center = moon,
                radius = w * .51f,
            ),
            radius = w * .51f,
            center = moon,
        )
        drawCircle(
            color = Color(0xFF829DAB).copy(alpha = .48f),
            radius = w * .42f,
            center = Offset(w * 1.17f, h * .50f),
            style = Stroke(1.2.dp.toPx()),
        )
        val distant = Path().apply {
            moveTo(0f, h * .75f)
            lineTo(w * .12f, h * .58f)
            lineTo(w * .24f, h * .76f)
            lineTo(w * .39f, h * .67f)
            lineTo(w * .56f, h * .79f)
            lineTo(w * .79f, h * .60f)
            lineTo(w, h * .84f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(distant, Color(0xFF173648))
        val front = Path().apply {
            moveTo(0f, h * .57f)
            lineTo(w * .04f, h * .46f)
            lineTo(w * .13f, h * .76f)
            lineTo(w * .24f, h * .86f)
            lineTo(w * .35f, h * .79f)
            lineTo(w * .52f, h * .91f)
            lineTo(w * .73f, h * .78f)
            lineTo(w * .87f, h * .65f)
            lineTo(w, h * .91f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(front, Color(0xFF091E2C))
        drawLine(
            color = VeldigoWelcomeTokens.Mint.copy(alpha = .62f),
            start = Offset(w * .50f, h * .78f),
            end = Offset(w * .50f, h * .97f),
            strokeWidth = 1.8.dp.toPx(),
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(VeldigoWelcomeTokens.Mint.copy(alpha = .40f), Color.Transparent),
                center = Offset(w * .50f, h * .96f),
                radius = w * .28f,
            ),
            radius = w * .28f,
            center = Offset(w * .50f, h * .96f),
        )
    }
}
