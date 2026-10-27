package io.horizontalsystems.walletkit.modules.balance.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.NavigationType
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.modules.createaccount.CreateAccountPage
import io.horizontalsystems.walletkit.modules.importwallet.ImportWalletPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.watchaddress.WatchAddressPage
import io.horizontalsystems.walletkit.uiv3.components.HSScaffold

private val BrandTeal = Color(0xFF41E6C7)
private val BrandNight = Color(0xFF071421)

/** Functional Veldigo empty-wallet state; all actions keep the original secure flow. */
@Composable
fun BalanceNoAccount(navigation: HSNavigation) {
    HSScaffold(title = stringResource(R.string.App_Name)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BrandNight, Color(0xFF0C2233), BrandNight)))
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier.size(172.dp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "VELDIGO",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Text(
                "CRYPTO WALLET",
                color = Color(0xFF9CB2C6),
                fontSize = 10.sp,
                letterSpacing = 4.sp,
            )
            Spacer(Modifier.weight(1f))
            Text(
                "Your crypto.\nYour control.",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 37.sp,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "A non-custodial wallet to manage crypto with your own private keys.",
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFB5C6D7),
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Self-custody", color = BrandTeal, fontSize = 12.sp)
                Text("Multi-chain", color = BrandTeal, fontSize = 12.sp)
                Text("Private keys", color = BrandTeal, fontSize = 12.sp)
            }
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = {
                    navigation.navigateWithTermsAccepted(
                        screen = CreateAccountPage(),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.Balance,
                        statPageTo = StatPage.NewWallet,
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal, contentColor = BrandNight),
            ) {
                Text("Create a new wallet", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    navigation.navigateWithTermsAccepted(
                        screen = ImportWalletPage(),
                        navigationType = NavigationType.SlideFromRight,
                        statPageFrom = StatPage.Balance,
                        statPageTo = StatPage.ImportWallet,
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFF3B526A)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Text("Import existing wallet", fontSize = 15.sp)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    navigation.slideFromRight(WatchAddressPage())
                    stat(page = StatPage.Balance, event = StatEvent.Open(StatPage.WatchWallet))
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(0.dp, Color.Transparent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB5C6D7)),
            ) {
                Text("Watch an address", fontSize = 13.sp)
            }
        }
    }
}
