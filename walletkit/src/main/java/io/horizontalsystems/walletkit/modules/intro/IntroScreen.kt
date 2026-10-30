package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.NavigationType
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.modules.createaccount.CreateAccountPage
import io.horizontalsystems.walletkit.modules.importwallet.ImportWalletPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.settings.language.LanguageSettingsPage
import kotlinx.coroutines.launch

private val Night = Color(0xFF071421)
private val Teal = Color(0xFF41E6C7)
private val Muted = Color(0xFFB5C6D7)

/**
 * Four-panel Veldigo reference: welcome, security, crypto and freedom.
 * The fourth page keeps its existing content pending its dedicated visual pass.
 */
@Composable
fun IntroScreen(navigation: HSNavigation) {
    val model = viewModel<IntroViewModel>(factory = IntroModule.Factory())
    val pager = rememberPagerState(initialPage = 0) { model.slides.size + 1 }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pager,
        modifier = Modifier
            .fillMaxSize()
            .background(Night)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) { index ->
        if (index == 0) {
            VeldigoWelcomeScreen(
                onCreateWallet = {
                    navigation.navigateWithTermsAccepted(
                        screen = CreateAccountPage(),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.Balance,
                        statPageTo = StatPage.NewWallet,
                    )
                    model.onStartClicked()
                },
                onImportWallet = {
                    navigation.navigateWithTermsAccepted(
                        screen = ImportWalletPage(),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.Balance,
                        statPageTo = StatPage.ImportWallet,
                    )
                    model.onStartClicked()
                },
                onLanguage = { navigation.slideFromRight(LanguageSettingsPage) },
            )
        } else if (index == 1) {
            VeldigoSecuritySlide(
                onNext = { scope.launch { pager.animateScrollToPage(2) } },
                onSkip = model::onStartClicked,
            )
        } else if (index == 2) {
            VeldigoCryptoIntroSlide(
                onNext = { scope.launch { pager.animateScrollToPage(3) } },
                onSkip = model::onStartClicked,
            )
        } else {
            FollowupIntroSlide(
                index = index,
                model = model,
                onNext = {
                    if (pager.currentPage == pager.pageCount - 1) {
                        model.onStartClicked()
                    } else {
                        scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    }
                },
                onSkip = model::onStartClicked,
            )
        }
    }
}

@Composable
private fun FollowupIntroSlide(
    index: Int,
    model: IntroViewModel,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Night, Color(0xFF0B2031), Night)))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
            )
            Text(
                text = "VELDIGO",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
        }
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            SpaceIllustration(slide = index, modifier = Modifier.weight(1f).fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(model.slides[index - 1].title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(model.slides[index - 1].subtitle),
                color = Muted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(30.dp))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(model.slides.size + 1) { indicator ->
                Spacer(
                    Modifier
                        .size(if (index == indicator) 9.dp else 6.dp)
                        .background(
                            if (index == indicator) Teal else Color(0xFF3E5969),
                            CircleShape,
                        ),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Night),
        ) {
            Text(
                text = if (index == model.slides.size) "Get started" else "Continue",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFF38546B)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        ) {
            Text("Skip introduction", fontSize = 14.sp)
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun SpaceIllustration(slide: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Teal.copy(alpha = 0.21f), Teal.copy(alpha = 0.035f), Color.Transparent),
                center = Offset(w / 2f, h * 0.70f),
                radius = w * 0.53f,
            ),
            radius = w * 0.53f,
            center = Offset(w / 2f, h * 0.70f),
        )
        // Distant planet and a subtle atmosphere.
        val planet = Offset(w * 0.78f, h * 0.34f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF1D4865), Color(0xFF0B233C), Night),
                center = planet,
                radius = w * 0.24f,
            ),
            radius = w * 0.24f,
            center = planet,
        )
        drawCircle(
            color = Teal.copy(alpha = 0.22f),
            radius = w * 0.245f,
            center = planet,
            style = Stroke(width = 1.2.dp.toPx()),
        )
        val stars = listOf(
            .10f to .15f, .19f to .31f, .36f to .09f, .64f to .20f,
            .86f to .14f, .91f to .47f, .08f to .51f,
        )
        for ((x, y) in stars) {
            drawCircle(Color.White.copy(alpha = 0.45f), 1.2.dp.toPx(), Offset(w * x, h * y))
        }
        val rearMountains = Path().apply {
            moveTo(0f, h * .78f)
            lineTo(w * .17f, h * .58f)
            lineTo(w * .29f, h * .72f)
            lineTo(w * .45f, h * .49f)
            lineTo(w * .63f, h * .76f)
            lineTo(w * .80f, h * .55f)
            lineTo(w, h * .77f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(rearMountains, Color(0xFF123A4D))
        val foreground = Path().apply {
            moveTo(0f, h * .94f)
            lineTo(w * .18f, h * .71f)
            lineTo(w * .31f, h * .88f)
            lineTo(w * .46f, h * .70f)
            lineTo(w * .61f, h * .91f)
            lineTo(w * .77f, h * .75f)
            lineTo(w, h * .96f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(foreground, Color(0xFF071C2D))
        drawCircle(
            brush = Brush.radialGradient(listOf(Teal.copy(alpha = .80f), Color.Transparent)),
            radius = w * .12f,
            center = Offset(w * .51f, h * .80f),
        )
        if (slide == 1) {
            drawCircle(Teal.copy(alpha = .35f), w * .08f, Offset(w * .48f, h * .42f), style = Stroke(2.dp.toPx()))
        }
    }
}
