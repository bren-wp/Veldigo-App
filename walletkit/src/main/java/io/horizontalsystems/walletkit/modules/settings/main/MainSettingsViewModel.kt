package io.horizontalsystems.walletkit.modules.settings.main

import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.IPinComponent
import io.horizontalsystems.walletkit.ISystemInfoManager
import io.horizontalsystems.walletkit.core.IAccountManager
import io.horizontalsystems.walletkit.core.IBackupManager
import io.horizontalsystems.walletkit.core.ITermsManager
import io.horizontalsystems.walletkit.core.ViewModelUiState
import io.horizontalsystems.walletkit.core.collectSafely
import io.horizontalsystems.walletkit.core.providers.IAppConfigProvider
import kotlinx.coroutines.launch

/**
 * Only real wallet/security signals are required to present Settings.
 * Do not initialize WalletConnect sessions, pending dApp requests, markets,
 * paywall/subscription state or swap-debug settings for the wallet-only UI.
 */
class MainSettingsViewModel(
    private val backupManager: IBackupManager,
    private val systemInfoManager: ISystemInfoManager,
    private val termsManager: ITermsManager,
    private val pinComponent: IPinComponent,
    private val accountManager: IAccountManager,
    private val appConfigProvider: IAppConfigProvider,
) : ViewModelUiState<MainSettingUiState>() {

    val appVersion: String
        get() {
            var version = systemInfoManager.appVersion
            if (io.horizontalsystems.walletkit.BuildConfig.DEBUG) {
                version += " (${appConfigProvider.appBuild})"
            }
            return version
        }

    init {
        viewModelScope.launch {
            backupManager.allBackedUpFlow.collectSafely { emitState() }
        }
        viewModelScope.launch {
            pinComponent.pinSetFlow.collect { emitState() }
        }
        viewModelScope.launch {
            accountManager.activeAccountStateFlow.collect { emitState() }
        }
        viewModelScope.launch {
            termsManager.termsAcceptedSharedFlow.collect { emitState() }
        }
    }

    /** Refresh after returning from device settings, PIN or biometric enrollment. */
    fun refresh() {
        emitState()
    }

    override fun createState(): MainSettingUiState {
        val pinSet = pinComponent.isPinSet
        val allBackedUp = backupManager.allBackedUp
        val hasNonStandardAccount = accountManager.hasNonStandardAccount
        return MainSettingUiState(
            manageWalletShowAlert = VeldigoSettingsStatusRules.walletAttention(
                allBackedUp, hasNonStandardAccount,
            ),
            securityCenterShowAlert = VeldigoSettingsStatusRules.securityAttention(pinSet),
            aboutAppShowAlert = !termsManager.allTermsAccepted,
            pinEnabled = pinSet,
            biometricEnabled = pinSet && pinComponent.isBiometricAuthEnabled,
        )
    }
}

data class MainSettingUiState(
    val manageWalletShowAlert: Boolean,
    val securityCenterShowAlert: Boolean,
    val aboutAppShowAlert: Boolean,
    val pinEnabled: Boolean,
    val biometricEnabled: Boolean,
)
