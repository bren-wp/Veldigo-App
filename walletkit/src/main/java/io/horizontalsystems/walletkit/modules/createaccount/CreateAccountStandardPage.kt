package io.horizontalsystems.walletkit.modules.createaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.core.stats.statAccountType
import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.manageaccount.backupkey.BackupKeyPage
import io.horizontalsystems.walletkit.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.walletkit.modules.nav3.HSPage
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

/**
 * Functional wallet-only screen 24. The existing CreateAccountViewModel
 * generates and saves the real mnemonic, which is not passed in navigation
 * arguments. The following BackupKeyPage resolves the saved account by ID.
 */
@Serializable
data class CreateAccountStandardPage(val input: ManageAccountsModule.Input?) :
    HSPage(screenshotEnabled = false) {

    @Composable
    override fun GetContent(navigation: HSNavigation) {
        val popTarget = input?.popOffOnSuccess ?: CreateAccountPage::class
        val inclusive = input?.popOffInclusive ?: true
        VeldigoCreateWalletForm(
            onBack = navigation::removeLastOrNull,
            onCreated = { account ->
                navigation.removeLastUntil(popTarget, inclusive)
                navigation.slideFromRight(BackupKeyPage(account))
            },
        )
    }
}

@Composable
private fun VeldigoCreateWalletForm(
    onBack: () -> Unit,
    onCreated: (Account) -> Unit,
) {
    val viewModel = viewModel<CreateAccountViewModel>(factory = CreateAccountModule.Factory())
    val view = LocalView.current
    var showPhraseLengthMenu by remember { mutableStateOf(false) }
    var passphraseVisible by remember { mutableStateOf(false) }

    // On app backgrounding, never leave a BIP39 passphrase visibly rendered.
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        passphraseVisible = false
    }

    LaunchedEffect(viewModel.success) {
        viewModel.success?.let { accountType ->
            val createdAccount = viewModel.createdAccount
            if (createdAccount != null) {
                HudHelper.showSuccessMessage(
                    contenView = view,
                    resId = R.string.Hud_Text_Created,
                    icon = R.drawable.icon_add_to_wallet_24,
                    iconTint = R.color.white,
                )
                delay(300)
                onCreated(createdAccount)
                stat(
                    page = if (viewModel.passphraseEnabled) StatPage.NewWalletAdvanced else StatPage.NewWallet,
                    event = StatEvent.CreateWallet(accountType.statAccountType),
                )
            }
            viewModel.onSuccessMessageShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VeldigoHomePalette.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.arrow_m_left_24),
                    contentDescription = stringResource(R.string.Button_Back),
                    tint = VeldigoHomePalette.White,
                )
            }
            Text(
                text = stringResource(R.string.Veldigo_CreateForm_Title),
                color = VeldigoHomePalette.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.Veldigo_CreateForm_Heading),
                color = VeldigoHomePalette.White,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.Veldigo_CreateForm_Description),
                color = VeldigoHomePalette.Muted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            Spacer(Modifier.height(27.dp))

            Text(
                text = stringResource(R.string.ManageAccount_WalletName),
                color = VeldigoHomePalette.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(9.dp))
            VeldigoCreateTextField(
                value = viewModel.accountName,
                onValueChange = viewModel::onChangeAccountName,
                placeholder = viewModel.defaultAccountName,
                trailingContent = {
                    IconButton(onClick = viewModel::generateRandomAccountName) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = stringResource(R.string.Veldigo_CreateForm_RandomName),
                            tint = VeldigoHomePalette.Mint,
                        )
                    }
                },
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = stringResource(
                    R.string.Veldigo_CreateForm_NameMax,
                    VeldigoWalletNameRules.MaxLength
                ),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(24.dp))

            val card = RoundedCornerShape(17.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(card)
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, card)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.CreateWallet_AdvancedOptions),
                            color = VeldigoHomePalette.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.Veldigo_CreateForm_AdvancedHint),
                            color = VeldigoHomePalette.Muted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Switch(
                        checked = viewModel.passphraseEnabled,
                        onCheckedChange = viewModel::setAdvancedOptionsEnabled,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = VeldigoHomePalette.Mint,
                            checkedThumbColor = VeldigoHomePalette.Background,
                            uncheckedTrackColor = VeldigoHomePalette.Border,
                            uncheckedThumbColor = VeldigoHomePalette.Muted,
                        ),
                    )
                }
            }

            if (viewModel.passphraseEnabled) {
                Spacer(Modifier.height(21.dp))
                Text(
                    text = stringResource(R.string.CreateWallet_Mnemonic),
                    color = VeldigoHomePalette.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(9.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(VeldigoHomePalette.Surface)
                            .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(14.dp))
                            .clickable(role = Role.Button) { showPhraseLengthMenu = true }
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = viewModel.selectedKind.titleLong,
                            color = VeldigoHomePalette.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            painter = painterResource(R.drawable.arrow_s_down_24),
                            contentDescription = null,
                            tint = VeldigoHomePalette.Mint,
                        )
                    }
                    DropdownMenu(
                        expanded = showPhraseLengthMenu,
                        onDismissRequest = { showPhraseLengthMenu = false },
                    ) {
                        viewModel.mnemonicKinds.forEach { kind ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = kind.titleLong,
                                        color = if (kind == viewModel.selectedKind)
                                            VeldigoHomePalette.Mint else VeldigoHomePalette.White,
                                    )
                                },
                                onClick = {
                                    viewModel.setMnemonicKind(kind)
                                    showPhraseLengthMenu = false
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(21.dp))
                Text(
                    text = stringResource(R.string.CreateWallet_PassphraseOptional),
                    color = VeldigoHomePalette.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.Veldigo_CreateForm_PassphraseWarning),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(11.dp))
                VeldigoCreateTextField(
                    value = viewModel.passphrase,
                    onValueChange = viewModel::onChangePassphrase,
                    placeholder = stringResource(R.string.CreateWallet_AddPassphrase),
                    password = !passphraseVisible,
                    isError = viewModel.passphraseState != null,
                    trailingContent = {
                        TextButton(onClick = { passphraseVisible = !passphraseVisible }) {
                            Text(
                                text = stringResource(
                                    if (passphraseVisible) R.string.Veldigo_CreateForm_Hide
                                    else R.string.Veldigo_CreateForm_Show,
                                ),
                                color = VeldigoHomePalette.Mint,
                            )
                        }
                    },
                )
                viewModel.passphraseState?.error?.localizedMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFFFB38A),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                VeldigoCreateTextField(
                    value = viewModel.passphraseConfirmation,
                    onValueChange = viewModel::onChangePassphraseConfirmation,
                    placeholder = stringResource(R.string.ConfirmPassphrase),
                    password = !passphraseVisible,
                    isError = viewModel.passphraseConfirmState != null,
                )
                viewModel.passphraseConfirmState?.error?.localizedMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFFFB38A),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VeldigoHomePalette.Surface)
                    .border(1.dp, VeldigoHomePalette.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    contentDescription = null,
                    tint = VeldigoHomePalette.Mint,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(11.dp))
                Text(
                    text = stringResource(R.string.Veldigo_CreateForm_Safety),
                    color = VeldigoHomePalette.Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.height(25.dp))
        }

        Button(
            onClick = viewModel::createAccount,
            enabled = viewModel.success == null && viewModel.createdAccount == null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .heightIn(min = 54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VeldigoHomePalette.Mint,
                contentColor = VeldigoHomePalette.Background,
                disabledContainerColor = VeldigoHomePalette.Border,
                disabledContentColor = VeldigoHomePalette.Muted,
            ),
        ) {
            Text(
                text = stringResource(R.string.Veldigo_CreateForm_Create),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun VeldigoCreateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    password: Boolean = false,
    isError: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder, color = VeldigoHomePalette.Muted, fontSize = 14.sp) },
        trailingIcon = trailingContent,
        singleLine = true,
        isError = isError,
        textStyle = TextStyle(color = VeldigoHomePalette.White, fontSize = 15.sp),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (password) KeyboardType.Password else KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = VeldigoHomePalette.White,
            unfocusedTextColor = VeldigoHomePalette.White,
            cursorColor = VeldigoHomePalette.Mint,
            focusedBorderColor = VeldigoHomePalette.Mint,
            unfocusedBorderColor = VeldigoHomePalette.Border,
            focusedContainerColor = VeldigoHomePalette.Surface,
            unfocusedContainerColor = VeldigoHomePalette.Surface,
            errorBorderColor = Color(0xFFFFB38A),
        ),
    )
}
