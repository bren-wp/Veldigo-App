package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.Caution
import io.horizontalsystems.walletkit.modules.balance.ui.VeldigoHomePalette
import io.horizontalsystems.walletkit.modules.nav3.HSNavigation
import io.horizontalsystems.marketkit.models.Blockchain

/**
 * The RPC URL and Basic Auth field stay in ViewModel memory only: do not put
 * either into saved-instance state, navigation args, telemetry or a WebView.
 */
@Composable
fun AddRpcScreen(
    navigation: HSNavigation,
    blockchain: Blockchain,
) {
    val viewModel = viewModel<AddRpcViewModel>(factory = AddRpcModule.Factory(blockchain))
    val ui = viewModel.viewState

    LaunchedEffect(ui.closeScreen) {
        if (ui.closeScreen) {
            viewModel.onScreenClose()
            navigation.removeLastOrNull()
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
                text = stringResource(R.string.AddEvmSyncSource_AddRPCSource),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = blockchain.name,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = VeldigoHomePalette.White,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = stringResource(R.string.Veldigo_EvmRpc_AddDescription),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = VeldigoHomePalette.Muted,
                modifier = Modifier.padding(top = 8.dp, bottom = 26.dp),
            )

            Text(
                text = stringResource(R.string.AddEvmSyncSource_RpcUrl),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
            )
            OutlinedTextField(
                value = viewModel.url,
                onValueChange = viewModel::onEnterRpcUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.Veldigo_EvmRpc_UrlHint),
                        fontSize = 13.sp,
                        color = VeldigoHomePalette.Muted,
                    )
                },
                singleLine = true,
                textStyle = TextStyle(color = VeldigoHomePalette.White, fontSize = 14.sp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                isError = ui.urlCaution != null,
                shape = RoundedCornerShape(14.dp),
                colors = veldigoRpcTextFieldColors(),
            )
            ui.urlCaution?.let { caution ->
                Text(
                    text = caution.text,
                    color = if (caution.type == Caution.Type.Error)
                        Color(0xFFFFB38A) else VeldigoHomePalette.Mint,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 7.dp),
                )
            }
            Text(
                text = stringResource(R.string.Veldigo_EvmRpc_UrlSafety),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 10.dp),
            )

            Text(
                text = stringResource(R.string.AddEvmSyncSource_BasicAuthentication),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VeldigoHomePalette.White,
                modifier = Modifier.padding(top = 27.dp),
            )
            OutlinedTextField(
                value = viewModel.auth,
                onValueChange = viewModel::onEnterBasicAuth,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.Veldigo_EvmRpc_AuthHint),
                        fontSize = 13.sp,
                        color = VeldigoHomePalette.Muted,
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(color = VeldigoHomePalette.White, fontSize = 14.sp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                shape = RoundedCornerShape(14.dp),
                colors = veldigoRpcTextFieldColors(),
            )
            Text(
                text = stringResource(R.string.Veldigo_EvmRpc_AuthSafety),
                color = VeldigoHomePalette.Muted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 10.dp),
            )

            Row(
                modifier = Modifier.padding(top = 29.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield_24),
                    tint = VeldigoHomePalette.Mint,
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                )
                Text(
                    text = stringResource(R.string.Veldigo_EvmRpc_UntrustedWarning),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = VeldigoHomePalette.Muted,
                )
            }
        }

        Button(
            onClick = viewModel::onAddClick,
            enabled = viewModel.url.isNotBlank() && !ui.closeScreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp)
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
                text = stringResource(R.string.Button_Add),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun veldigoRpcTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = VeldigoHomePalette.White,
    unfocusedTextColor = VeldigoHomePalette.White,
    cursorColor = VeldigoHomePalette.Mint,
    focusedBorderColor = VeldigoHomePalette.Mint,
    unfocusedBorderColor = VeldigoHomePalette.Border,
    errorBorderColor = Color(0xFFFFB38A),
    focusedContainerColor = VeldigoHomePalette.Surface,
    unfocusedContainerColor = VeldigoHomePalette.Surface,
)
