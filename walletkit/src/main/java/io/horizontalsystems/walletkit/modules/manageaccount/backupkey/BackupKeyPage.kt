package io.horizontalsystems.walletkit.modules.manageaccount.backupkey

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey.BackupConfirmKeyPage
import io.horizontalsystems.walletkit.modules.manageaccount.recoveryphrase.RecoveryPhraseModule
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.serialization.Serializable

/**
 * Existing saved account's real mnemonic, never a sample BIP39 phrase.
 * The navigation host enforces FLAG_SECURE for this page and the confirmation page.
 */
@Serializable
data class BackupKeyPage(val accountId: String) : HSPage(screenshotEnabled = false) {
    // Never serialize the Account (which contains the mnemonic) in the nav back stack.
    constructor(account: Account) : this(account.id)

    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val account = App.accountManager.account(accountId)
        if (account != null) {
            RecoveryPhraseScreen(navigation, account)
        } else {
            LaunchedEffect(accountId) { navigation.removeLastOrNull() }
        }
    }
}

private val seedNavy = VeldigoHomePalette.Background
private val seedPanel = VeldigoHomePalette.Surface
private val seedMint = VeldigoHomePalette.Mint
private val seedMuted = VeldigoHomePalette.Muted
private val seedBorder = VeldigoHomePalette.Border

@Composable
fun RecoveryPhraseScreen(navigation: HSNavigation, account: Account) {
    val model = viewModel<BackupKeyViewModel>(factory = BackupKeyModule.Factory(account))
    var revealed by remember { mutableStateOf(false) }
    var showDifferentWordsNotice by remember { mutableStateOf(false) }

    // Keep the words out of the composition on backgrounding. FLAG_SECURE in
    // BackupKeyPage also prevents screenshots/recents thumbnails.
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        revealed = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(seedNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp),
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
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.Veldigo_Backup_Title),
                color = Color.White,
                fontSize = 25.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = stringResource(R.string.Veldigo_Backup_Description, model.wordsNumbered.size),
                color = seedMuted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
            Spacer(Modifier.height(22.dp))

            if (model.wordsNumbered.isNotEmpty()) {
                SeedWordsGrid(model.wordsNumbered, revealed)

                if (model.passphrase.isNotEmpty()) {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.Veldigo_Backup_PassphraseWarning),
                        color = seedMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (revealed) model.passphrase else "••••••••",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(seedPanel, RoundedCornerShape(10.dp))
                            .border(1.dp, seedBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp),
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.Veldigo_Backup_NoWords),
                    color = seedMuted,
                    fontSize = 14.sp,
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        if (model.wordsNumbered.isNotEmpty()) {
            if (!revealed) {
                Button(
                    onClick = { revealed = true },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = seedMint, contentColor = seedNavy),
                ) {
                    Text(
                        text = stringResource(R.string.Veldigo_Backup_Reveal),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }
            } else {
                Button(
                    onClick = {
                        // Acknowledge viewing is NOT verification; the following
                        // page verifies real randomly selected words before backup status changes.
                        revealed = false
                        navigation.slideFromRight(BackupConfirmKeyPage(model.account))
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = seedMint, contentColor = seedNavy),
                ) {
                    Text(
                        text = stringResource(R.string.Veldigo_Backup_WrittenDown),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }
            }

            TextButton(
                onClick = {
                    if (revealed) showDifferentWordsNotice = true
                    else revealed = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            ) {
                Text(
                    text = stringResource(
                        if (revealed) R.string.Veldigo_Backup_DifferentWords
                        else R.string.Veldigo_Backup_Reveal
                    ),
                    color = seedMint,
                    fontSize = 13.sp,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }

    if (showDifferentWordsNotice) {
        AlertDialog(
            onDismissRequest = { showDifferentWordsNotice = false },
            title = { Text(stringResource(R.string.Veldigo_Backup_FixedTitle)) },
            text = { Text(stringResource(R.string.Veldigo_Backup_FixedDescription)) },
            confirmButton = {
                TextButton(onClick = { showDifferentWordsNotice = false }) {
                    Text(stringResource(R.string.Button_Ok))
                }
            },
        )
    }
}

/**
 * Words have their own full-width, non-clipped cells rather than being packed
 * into a three-column row on narrow phones. Real mnemonic words remain masked
 * until explicit reveal and FLAG_SECURE is active on the page.
 */
@Composable
private fun SeedWordsGrid(
    words: List<RecoveryPhraseModule.WordNumbered>,
    revealed: Boolean,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = RecoveryPhraseGridRules.columns(maxWidth.value)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            words.chunked(columns).forEach { rowWords ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    rowWords.forEach { numbered ->
                        val shape = RoundedCornerShape(10.dp)
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .background(seedPanel, shape)
                                .border(1.dp, seedBorder, shape)
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = numbered.number.toString(),
                                color = seedMuted,
                                fontSize = 11.sp,
                            )
                            Text(
                                text = if (revealed) numbered.word else "••••",
                                color = Color.White,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    repeat(columns - rowWords.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
