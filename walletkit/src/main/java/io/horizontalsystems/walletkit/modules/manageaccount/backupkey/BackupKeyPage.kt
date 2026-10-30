package io.horizontalsystems.walletkit.modules.manageaccount.backupkey

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.entities.Account
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
data class BackupKeyPage(val account: Account) : HSPage(screenshotEnabled = false) {
    @Composable
    override fun GetContent(navigation: HSNavigation) {
        RecoveryPhraseScreen(navigation, account)
    }
}

private val seedNavy = Color(0xFF030E0F)
private val seedPanel = Color(0xFF121E20)
private val seedMint = Color(0xFF41E6C7)
private val seedMuted = Color(0xFF9EB3B7)
private val seedBorder = Color(0xFF26383A)

@Composable
fun RecoveryPhraseScreen(navigation: HSNavigation, account: Account) {
    val model = viewModel<BackupKeyViewModel>(factory = BackupKeyModule.Factory(account))
    var revealed by remember { mutableStateOf(false) }
    var showDifferentWordsNotice by remember { mutableStateOf(false) }

    // Keep the words out of the composition on backgrounding. FLAG_SECURE in
    // BackupKeyPage also prevents screenshots/recents thumbnails.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        revealed = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(seedNavy, Color(0xFF061717), seedNavy)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = navigation::removeLastOrNull,
                modifier = Modifier.semantics { contentDescription = "Back" },
            ) {
                Canvas(Modifier.size(24.dp)) {
                    val w = size.width
                    val h = size.height
                    val stroke = 2.dp.toPx()
                    drawLine(Color.White, Offset(w * .75f, h * .5f), Offset(w * .24f, h * .5f), stroke, cap = StrokeCap.Round)
                    drawLine(Color.White, Offset(w * .24f, h * .5f), Offset(w * .46f, h * .27f), stroke, cap = StrokeCap.Round)
                    drawLine(Color.White, Offset(w * .24f, h * .5f), Offset(w * .46f, h * .73f), stroke, cap = StrokeCap.Round)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.Veldigo_Backup_Title),
                color = Color.White,
                fontSize = 27.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = stringResource(R.string.Veldigo_Backup_Description, model.wordsNumbered.size),
                color = seedMuted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
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
                    shape = RoundedCornerShape(10.dp),
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
                    shape = RoundedCornerShape(10.dp),
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

@Composable
private fun SeedWordsGrid(
    words: List<RecoveryPhraseModule.WordNumbered>,
    revealed: Boolean,
) {
    // 3 columns x 4 rows for 12-word mnemonics, expanding naturally for 15–25 words.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(seedPanel, RoundedCornerShape(14.dp))
            .border(1.dp, seedBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        words.chunked(3).forEach { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                rowWords.forEach { numbered ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = numbered.number.toString(),
                            color = seedMuted,
                            fontSize = 11.sp,
                        )
                        Text(
                            text = if (revealed) numbered.word else "••••",
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                repeat(3 - rowWords.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
