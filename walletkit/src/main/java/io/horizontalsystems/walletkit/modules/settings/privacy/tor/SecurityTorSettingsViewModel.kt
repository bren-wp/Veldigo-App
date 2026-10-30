package io.horizontalsystems.walletkit.modules.settings.privacy.tor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.core.AppLogger
import io.horizontalsystems.walletkit.core.ITorManager
import kotlinx.coroutines.launch

/**
 * A Tor preference change is a two-step decision: request, then confirm.
 * The switch reflects the actual persisted setting, not a speculative
 * enable/disable action before the confirmation/restart has happened.
 */
class SecurityTorSettingsViewModel(
    private val torManager: ITorManager,
) : ViewModel() {
    private val logger = AppLogger("SecurityTorSettingsViewModel")

    var torCheckEnabled by mutableStateOf(torManager.isTorEnabled)
        private set

    var pendingTorEnabled by mutableStateOf<Boolean?>(null)
        private set

    var showRestartAlert by mutableStateOf(false)
        private set

    var restartApp by mutableStateOf(false)
        private set

    val torStatusFlow get() = torManager.torStatusFlow

    fun setTorEnabledWithChecks(enabled: Boolean) {
        if (!TorPreferenceChangeRules.requiresConfirmation(torManager.isTorEnabled, enabled)) return
        pendingTorEnabled = enabled
        showRestartAlert = true
    }

    /** Called only from an explicit confirmation, not during recomposition. */
    fun setTorEnabled() {
        val requested = pendingTorEnabled ?: return
        pendingTorEnabled = null
        showRestartAlert = false

        if (requested) {
            torManager.setTorAsEnabled()
            torCheckEnabled = torManager.isTorEnabled
            // Never call keepUnlocked(): the new process must enforce normal
            // PIN/biometric authorization after a network-stack restart.
            restartApp = true
        } else {
            torManager.setTorAsDisabled()
            torCheckEnabled = torManager.isTorEnabled
            viewModelScope.launch {
                try {
                    torManager.stop()
                } catch (e: Exception) {
                    logger.warning("Tor shutdown exception", e)
                }
                // The setting is already persisted off; the process restart
                // replaces all network clients even when shutdown failed.
                restartApp = true
            }
        }
    }

    /** Cancel and dismiss are identical: neither changes the Tor setting. */
    fun restartAppAlertShown() = resetSwitch()

    fun appRestarted() {
        restartApp = false
    }

    fun resetSwitch() {
        pendingTorEnabled = null
        showRestartAlert = false
        torCheckEnabled = torManager.isTorEnabled
    }
}
