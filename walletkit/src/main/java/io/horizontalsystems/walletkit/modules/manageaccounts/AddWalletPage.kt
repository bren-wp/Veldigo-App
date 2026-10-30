package io.horizontalsystems.walletkit.modules.manageaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.NavigationType
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.createaccount.CreateAccountStandardPage
import io.horizontalsystems.walletkit.modules.importwallet.ImportWalletPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import io.horizontalsystems.walletkit.modules.watchaddress.WatchAddressPage
import kotlinx.serialization.Serializable

/**
 * Wallet-only screen 23; modeled after the user's Add Wallet reference panel.
 * This screen never generates or renders keys. The actual create/import/watch
 * implementations own validation, signing permissions, backup and recovery.
 */
@Serializable
data class AddWalletPage(val input: ManageAccountsModule.Input?) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        AddWalletScreen(navigation, input)
    }
}

@Composable
fun AddWalletScreen(navigation: HSNavigation, input: ManageAccountsModule.Input?) {
    // A null input occurs when opened without an account-list origin. Always
    // supply a real pop-off target so success cannot strand the navigation stack.
    val continuation = VeldigoAddWalletRoute.resolveInput(input)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(start = 8.dp, end = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = navigation::removeLastOrNull) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = VeldigoHomePalette.White,
                )
            }
            Text(
                text = stringResource(R.string.ManageAccounts_AddWallet),
                modifier = Modifier.weight(1f),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VeldigoAddWalletChoice(
                icon = R.drawable.wallet_add_24,
                title = stringResource(R.string.Veldigo_AddWallet_CreateTitle),
                subtitle = stringResource(R.string.Veldigo_AddWallet_CreateDetail),
                onClick = {
                    // Go straight to the secure, existing wallet-creation form.
                    // Avoid showing the preceding setup chooser a second time.
                    navigation.navigateWithTermsAccepted(
                        screen = CreateAccountStandardPage(continuation),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.ManageWallets,
                        statPageTo = StatPage.NewWallet,
                    )
                },
            )
            VeldigoAddWalletChoice(
                icon = R.drawable.ic_download_24,
                title = stringResource(R.string.Veldigo_AddWallet_ImportTitle),
                subtitle = stringResource(R.string.Veldigo_AddWallet_ImportDetail),
                onClick = {
                    navigation.navigateWithTermsAccepted(
                        screen = ImportWalletPage(continuation),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.ManageWallets,
                        statPageTo = StatPage.ImportWallet,
                    )
                },
            )
            VeldigoAddWalletChoice(
                icon = R.drawable.user_24,
                title = stringResource(R.string.Veldigo_AddWallet_WatchTitle),
                subtitle = stringResource(R.string.Veldigo_AddWallet_WatchDetail),
                onClick = {
                    navigation.slideFromRight(WatchAddressPage(continuation))
                    stat(
                        page = StatPage.ManageWallets,
                        event = StatEvent.Open(StatPage.WatchWallet),
                    )
                },
            )
        }
    }
}

@Composable
private fun VeldigoAddWalletChoice(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val corner = RoundedCornerShape(17.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 94.dp)
            .clip(corner)
            .background(VeldigoHomePalette.Surface)
            .border(1.dp, VeldigoHomePalette.Border, corner)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 13.dp, top = 14.dp, bottom = 14.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(androidx.compose.ui.graphics.Color(0xFF103C3B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = VeldigoHomePalette.Mint,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = VeldigoHomePalette.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = VeldigoHomePalette.Muted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 5.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.arrow_m_right_24),
            contentDescription = null,
            tint = VeldigoHomePalette.Muted,
            modifier = Modifier.size(20.dp),
        )
    }
}
