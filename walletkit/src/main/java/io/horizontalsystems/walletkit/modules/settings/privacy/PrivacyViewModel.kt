package io.horizontalsystems.walletkit.modules.settings.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.ViewModelUiState
import io.horizontalsystems.walletkit.core.stats.StatsManager
import kotlinx.coroutines.launch

/**
 * Real, opt-in app-usage telemetry preference. This screen intentionally has
 * no external VPN/promotion link or marketing service dependency.
 */
class PrivacyViewModel(
    private val statsManager: StatsManager,
) : ViewModelUiState<PrivacyUiState>() {

    private var uiStatsEnabled = statsManager.uiStatsEnabledFlow.value

    init {
        viewModelScope.launch {
            statsManager.uiStatsEnabledFlow.collect {
                uiStatsEnabled = it
                emitState()
            }
        }
    }

    override fun createState() = PrivacyUiState(uiStatsEnabled)

    fun toggleUiStats(enabled: Boolean) {
        statsManager.toggleUiStats(enabled)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PrivacyViewModel(App.statsManager) as T
    }
}

data class PrivacyUiState(val uiStatsEnabled: Boolean)
