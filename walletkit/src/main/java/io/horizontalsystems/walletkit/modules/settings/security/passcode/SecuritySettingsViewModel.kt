package io.horizontalsystems.walletkit.modules.settings.security.passcode

import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.IPinComponent
import io.horizontalsystems.walletkit.ISystemInfoManager
import io.horizontalsystems.walletkit.core.ILocalStorage
import io.horizontalsystems.walletkit.core.ViewModelUiState
import io.horizontalsystems.walletkit.core.managers.BalanceHiddenManager
import io.horizontalsystems.walletkit.core.managers.SpamManager
import kotlinx.coroutines.launch

/**
 * App-local wallet protection only. Premium, subscription, and paid "defense"
 * state are not required by the self-custody Security Center.
 */
class SecuritySettingsViewModel(
    private val systemInfoManager: ISystemInfoManager,
    private val pinComponent: IPinComponent,
    private val balanceHiddenManager: BalanceHiddenManager,
    private val localStorage: ILocalStorage,
    private val spamManager: SpamManager,
) : ViewModelUiState<SecuritySettingsUiState>() {

    val biometricSettingsVisible: Boolean
        get() = systemInfoManager.biometricAuthSupported

    private var pinEnabled = pinComponent.isPinSet
    private var duressPinEnabled = pinComponent.isDuressPinSet()
    private var balanceAutoHideEnabled = balanceHiddenManager.balanceAutoHidden
    private var hideSuspiciousTxs = spamManager.hideSuspiciousTx

    init {
        // Enabling biometrics must happen on the separate Android
        // BiometricPrompt page. This flow only reflects the persisted result.
        viewModelScope.launch {
            pinComponent.isBiometricAuthEnabledFlow.collect { emitState() }
        }
        viewModelScope.launch {
            pinComponent.pinSetFlow.collect { update() }
        }
        viewModelScope.launch {
            spamManager.hideSuspiciousTxStateFlow.collect {
                hideSuspiciousTxs = it
                emitState()
            }
        }
    }

    override fun createState() = SecuritySettingsUiState(
        pinEnabled = pinEnabled,
        biometricsEnabled = SecurityCenterStateRules.biometricActive(
            pinEnabled, pinComponent.isBiometricAuthEnabled,
        ),
        duressPinEnabled = duressPinEnabled,
        balanceAutoHideEnabled = balanceAutoHideEnabled,
        hideSuspiciousTxs = hideSuspiciousTxs,
        autoLockIntervalName = localStorage.autoLockInterval.title,
    )

    /** Re-read actual state on resume after PIN, biometric or OS configuration. */
    fun update() {
        pinEnabled = pinComponent.isPinSet
        duressPinEnabled = pinComponent.isDuressPinSet()
        balanceAutoHideEnabled = balanceHiddenManager.balanceAutoHidden
        hideSuspiciousTxs = spamManager.hideSuspiciousTx
        emitState()
    }

    fun disableBiometrics() {
        pinComponent.isBiometricAuthEnabled = false
        emitState()
    }

    fun disablePin() {
        pinComponent.disablePin()
        pinComponent.isBiometricAuthEnabled = false
        update()
    }

    fun disableDuressPin() {
        pinComponent.disableDuressPin()
        update()
    }

    fun onSetBalanceAutoHidden(enabled: Boolean) {
        balanceHiddenManager.setBalanceAutoHidden(enabled)
        balanceAutoHideEnabled = enabled
        emitState()
    }

    fun hideSuspiciousTxs(hidden: Boolean) {
        spamManager.updateFilterHideSuspiciousTx(hidden)
        hideSuspiciousTxs = hidden
        emitState()
    }
}

data class SecuritySettingsUiState(
    val pinEnabled: Boolean,
    val biometricsEnabled: Boolean,
    val duressPinEnabled: Boolean,
    val balanceAutoHideEnabled: Boolean,
    val hideSuspiciousTxs: Boolean,
    val autoLockIntervalName: Int,
)
