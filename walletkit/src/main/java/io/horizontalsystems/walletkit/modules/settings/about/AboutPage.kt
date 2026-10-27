package io.horizontalsystems.walletkit.modules.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.releasenotes.ReleaseNotesScreen
import io.horizontalsystems.walletkit.modules.settings.appstatus.AppStatusScreen
import io.horizontalsystems.walletkit.modules.settings.main.HsSettingCell
import io.horizontalsystems.walletkit.modules.settings.terms.TermsPage
import io.horizontalsystems.walletkit.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.walletkit.ui.compose.components.VSpacer
import io.horizontalsystems.walletkit.ui.helpers.LinkHelper
import io.horizontalsystems.walletkit.uiv3.components.HSScaffold
import kotlinx.serialization.Serializable

@Serializable
data object AboutPage : HSPage() {

    @Composable
    override fun GetContent(navigation: HSNavigation) {
        AboutScreen(
            navigation,
            { navigation.removeLastOrNull() }
        )
    }
}

@Serializable
data object ReleaseNotesPage: HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        ReleaseNotesScreen(false, { navigation.removeLastOrNull() })
    }
}

@Serializable
data object AppStatusPage: HSPage() {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        AppStatusScreen(navigation)
    }
}

@Composable
private fun AboutScreen(
    navigation: HSNavigation,
    onBackPress: () -> Unit,
    aboutViewModel: AboutViewModel = viewModel(factory = AboutModule.Factory()),
) {
    HSScaffold(
        title = stringResource(R.string.SettingsAboutApp_Title),
        onBack = onBackPress,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF071421))
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_splash_logo),
                    contentDescription = null,
                    modifier = Modifier.size(112.dp)
                )
                Text("VELDIGO", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                     letterSpacing = 3.sp, color = Color.White)
                VSpacer(6.dp)
                Text("Your crypto. Your control.", color = Color(0xFF41E6C7),
                     fontSize = 14.sp, textAlign = TextAlign.Center)
                VSpacer(12.dp)
                Text("Built with purpose by Brendigo", color = Color(0xFFB5C6D7),
                     fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            SettingSections(aboutViewModel, navigation)
            VSpacer(36.dp)
        }
    }
}

@Composable
private fun SettingSections(
    viewModel: AboutViewModel,
    navigation: HSNavigation
) {

    val context = LocalContext.current

    CellUniversalLawrenceSection(
        listOf {
            HsSettingCell(
                title = R.string.SettingsAboutApp_AppVersion,
                icon = R.drawable.ic_info_20,
                value = viewModel.appVersion,
                onClick = {
                    navigation.add(ReleaseNotesPage)

                    stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.WhatsNew))
                }
            )
        }
    )

    Spacer(Modifier.height(32.dp))

    CellUniversalLawrenceSection(
        listOf({
            HsSettingCell(
                R.string.Settings_AppStatus,
                R.drawable.ic_app_status,
                onClick = {
                    navigation.add(AppStatusPage)

                    stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.AppStatus))
                }
            )
        }, {
            HsSettingCell(
                R.string.Settings_Terms,
                R.drawable.ic_terms_20,
                showAlert = viewModel.termsShowAlert,
                onClick = {
                    navigation.add(TermsPage())

                    stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.Terms))
                }
            )
        })
    )

    Spacer(Modifier.height(32.dp))

    CellUniversalLawrenceSection(
        listOf({
            HsSettingCell(
                R.string.SettingsAboutApp_Github,
                R.drawable.ic_github_20,
                onClick = {
                    LinkHelper.openLinkInAppBrowser(context, viewModel.githubLink)

                    stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.ExternalGithub))
                }
            )
        }, {
            HsSettingCell(
                R.string.SettingsAboutApp_Site,
                R.drawable.ic_globe,
                onClick = {
                    LinkHelper.openLinkInAppBrowser(context, viewModel.appWebPageLink)

                    stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.ExternalWebsite))
                }
            )
        })
    )

    VSpacer(32.dp)
}
