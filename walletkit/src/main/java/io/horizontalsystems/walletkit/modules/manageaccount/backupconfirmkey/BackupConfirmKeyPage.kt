package io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.manageaccount.backupkey.RecoveryPhraseGridRules
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.modules.manageaccount.backupkey.BackupKeyPage
import io.horizontalsystems.walletkit.modules.settings.security.setup.SecuritySetupPage
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

/**
 * 7th screen: Confirm Recovery Phrase. Secrets never enter serializable nav state.
 * Both this page and BackupKeyPage retain FLAG_SECURE at the navigation layer.
 */
@Serializable
data class BackupConfirmKeyPage(val accountId: String) : HSPage(screenshotEnabled = false) {
    constructor(account: Account) : this(account.id)

    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val account = App.accountManager.account(accountId)
        if (account == null) {
            LaunchedEffect(accountId) { navigation.removeLastOrNull() }
        } else {
            RecoveryPhraseVerifyScreen(navigation, account)
        }
    }
}

private val verifyBackground = VeldigoHomePalette.Background
private val verifyPanel = VeldigoHomePalette.Surface
private val verifyBorder = VeldigoHomePalette.Border
private val verifyMint = VeldigoHomePalette.Mint
private val verifyMuted = VeldigoHomePalette.Muted

@Composable
fun RecoveryPhraseVerifyScreen(navigation: HSNavigation, account: Account) {
    val viewModel = viewModel<BackupConfirmKeyViewModel>(
        factory = BackupConfirmKeyModule.Factory(account)
    )
    val state = viewModel.uiState
    val contentView = LocalView.current
    var inForeground by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        inForeground = false
        viewModel.clearSelectionOnBackground()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        inForeground = true
    }

    LaunchedEffect(state.confirmed) {
        if (state.confirmed) {
            HudHelper.showSuccessMessage(
                contenView = contentView,
                resId = R.string.Hud_Text_Verified,
                icon = R.drawable.icon_check_1_24,
                iconTint = R.color.white,
            )
            delay(300)
            navigation.removeLastUntil(BackupKeyPage::class, true)
            // Offer the actual PIN / biometric setup after verification, never
            // before the original full mnemonic was confirmed. Existing protected
            // wallets can return directly to their previous screen.
            if (!App.pinComponent.isPinSet) {
                navigation.slideFromRight(SecuritySetupPage)
            }
        }
    }
    LaunchedEffect(state.error) {
        state.error?.message?.let { message ->
            HudHelper.showErrorMessage(contentView, message)
            viewModel.onErrorShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(verifyBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = navigation::removeLastOrNull) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp),
        ) {
            Spacer(Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.Veldigo_Verify_Title),
                fontSize = 23.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.Veldigo_Verify_Description),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = verifyMuted,
            )
            Spacer(Modifier.height(12.dp))
            if (state.hiddenWordItems.isNotEmpty()) {
                Text(
                    text = if (state.readyToConfirm) {
                        stringResource(R.string.Veldigo_Verify_Ready)
                    } else {
                        stringResource(
                            R.string.Veldigo_Verify_NextWord,
                            state.currentHiddenWordItemIndex + 1,
                            state.hiddenWordItems.size,
                        )
                    },
                    color = verifyMint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(20.dp))

            // Display shuffled choices from the saved phrase. On pause the
            // words are masked, the challenge resets, and FLAG_SECURE prevents
            // screenshots and Android Recents thumbnails.
            val selectedDescription = stringResource(R.string.Veldigo_Verify_Selected)
            val notSelectedDescription = stringResource(R.string.Veldigo_Verify_NotSelected)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val columns = RecoveryPhraseGridRules.columns(maxWidth.value)
                Column(modifier = Modifier.fillMaxWidth()) {
                    state.wordOptions.chunked(columns).forEachIndexed { rowIndex, rowItems ->
                        if (rowIndex > 0) Spacer(Modifier.height(9.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                        ) {
                            rowItems.forEach { option ->
                                val selected = !option.enabled
                                val shape = RoundedCornerShape(10.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .background(
                                            if (selected) Color(0xFF123E39) else verifyPanel,
                                            shape,
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) verifyMint else verifyBorder,
                                            shape,
                                        )
                                        .clickable(
                                            enabled = option.enabled && inForeground && !state.readyToConfirm,
                                            role = Role.Button,
                                        ) { viewModel.onSelectWord(option) }
                                        .semantics {
                                            stateDescription = if (selected) selectedDescription
                                                else notSelectedDescription
                                        }
                                        .padding(horizontal = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = if (inForeground) option.word else "••••",
                                        color = if (selected) verifyMint else Color.White,
                                        fontSize = 13.sp,
                                        lineHeight = 17.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
            if (state.hiddenWordItems.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = stringResource(
                        R.string.Veldigo_Verify_Progress,
                        state.hiddenWordItems.count { it.isRevealed },
                        state.hiddenWordItems.size,
                    ),
                    color = verifyMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Button(
            onClick = viewModel::confirmBackup,
            enabled = inForeground && state.readyToConfirm && !state.confirmed,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = verifyMint,
                contentColor = verifyBackground,
                disabledContainerColor = Color(0xFF1A2A2C),
                disabledContentColor = Color(0xFF899DA0),
            ),
        ) {
            Text(
                text = stringResource(R.string.Veldigo_Verify_Confirm),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
