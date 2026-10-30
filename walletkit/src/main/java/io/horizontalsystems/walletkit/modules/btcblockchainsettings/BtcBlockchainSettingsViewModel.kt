package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.providers.Translator
import io.horizontalsystems.walletkit.entities.BtcRestoreMode
import io.horizontalsystems.walletkit.modules.btcblockchainsettings.BtcBlockchainSettingsModule.BlockchainSettingsIcon
import io.horizontalsystems.walletkit.modules.btcblockchainsettings.BtcBlockchainSettingsModule.ViewItem
import kotlinx.coroutines.launch

class BtcBlockchainSettingsViewModel(
    private val service: BtcBlockchainSettingsService,
) : ViewModel() {

    var closeScreen by mutableStateOf(false)
        private set

    var restoreSources by mutableStateOf<List<ViewItem>>(emptyList())
        private set

    var saveButtonEnabled by mutableStateOf(false)
        private set

    var saveError by mutableStateOf(false)
        private set

    var saving by mutableStateOf(false)
        private set

    val title: String = service.blockchain.name

    init {
        viewModelScope.launch {
            service.hasChangesFlow.collect { hasChanges ->
                saveButtonEnabled = hasChanges
                syncRestoreModeState()
            }
        }
        syncRestoreModeState()
    }

    fun onSelectRestoreMode(viewItem: ViewItem) {
        if (saving) return
        if (service.setRestoreMode(viewItem.id)) {
            saveError = false
        }
    }

    fun onSaveClick() {
        if (!saveButtonEnabled || saving) return
        saving = true
        saveError = false
        try {
            if (service.save()) {
                closeScreen = true
                saveButtonEnabled = false
            } else {
                saveError = true
            }
        } catch (_: Exception) {
            // Avoid logging source or account data from unexpected storage
            // exceptions. Never dismiss a screen when the save failed.
            saveError = true
        } finally {
            saving = false
        }
    }

    private fun syncRestoreModeState() {
        restoreSources = service.restoreModes.map { mode ->
            ViewItem(
                id = mode.raw,
                title = Translator.getString(mode.title),
                subtitle = Translator.getString(mode.description),
                selected = mode == service.restoreMode,
                icon = mode.icon,
            )
        }
    }

    private val BtcRestoreMode.icon: BlockchainSettingsIcon
        get() = when (this) {
            BtcRestoreMode.Blockchair -> BlockchainSettingsIcon.ApiIcon(R.drawable.ic_blockchair)
            BtcRestoreMode.Hybrid -> BlockchainSettingsIcon.ApiIcon(R.drawable.ic_api_hybrid)
            BtcRestoreMode.Blockchain -> BlockchainSettingsIcon.ApiIcon(R.drawable.globe_24)
        }
}
