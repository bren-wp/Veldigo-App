package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.core.NavigationType
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.modules.createaccount.CreateAccountPage
import io.horizontalsystems.walletkit.modules.importwallet.ImportWalletPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.settings.language.LanguageSettingsPage
import kotlinx.coroutines.launch

/**
 * The four introduction screens are implemented as native Compose content.
 * The wallet's existing account flows and durable completion state are retained.
 */
@Composable
fun IntroScreen(navigation: HSNavigation) {
    val model = viewModel<IntroViewModel>(factory = IntroModule.Factory())
    val pager = rememberPagerState(initialPage = 0) { 4 }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pager,
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoWelcomeTokens.Navy)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) { index ->
        when (index) {
            0 -> VeldigoWelcomeScreen(
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

            1 -> VeldigoSecuritySlide(
                onNext = { scope.launch { pager.animateScrollToPage(2) } },
                onSkip = model::onStartClicked,
            )

            2 -> VeldigoCryptoIntroSlide(
                onNext = { scope.launch { pager.animateScrollToPage(3) } },
                onSkip = model::onStartClicked,
            )

            else -> VeldigoFreedomSlide(
                onGetStarted = model::onStartClicked,
            )
        }
    }
}
