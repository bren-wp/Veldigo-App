package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import io.horizontalsystems.walletkit.core.managers.BtcBlockchainManager
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.walletkit.entities.BtcRestoreMode
import io.horizontalsystems.marketkit.models.Blockchain
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class BtcBlockchainSettingsService(
    val blockchain: Blockchain,
    private val btcBlockchainManager: BtcBlockchainManager,
) {
    private val _hasChangesFlow = MutableSharedFlow<Boolean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val hasChangesFlow: Flow<Boolean> get() = _hasChangesFlow

    var restoreMode: BtcRestoreMode = btcBlockchainManager.restoreMode(blockchain.type)
        private set

    val restoreModes: List<BtcRestoreMode>
        get() = btcBlockchainManager.availableRestoreModes(blockchain.type)

    init {
        syncHasChanges()
    }

    /**
     * Return false for stale, invalid or unchanged selections. Only real
     * per-chain supported modes may be persisted; this screen does not
     * configure arbitrary P2P nodes or RPC endpoints.
     */
    fun save(): Boolean {
        val currentMode = btcBlockchainManager.restoreMode(blockchain.type)
        if (!BtcRestoreSelectionRules.canSave(restoreMode, currentMode, restoreModes)) {
            syncHasChanges()
            return false
        }

        // Let a persistence failure propagate to the ViewModel: it must not
        // close the screen, report success, or emit a false change event.
        btcBlockchainManager.save(restoreMode, blockchain.type)
        syncHasChanges()

        stat(
            page = StatPage.BlockchainSettingsBtc,
            event = StatEvent.SwitchBtcSource(blockchain.uid, restoreMode),
        )
        return true
    }

    fun setRestoreMode(id: String): Boolean {
        val mode = BtcRestoreSelectionRules.eligible(id, restoreModes) ?: return false
        restoreMode = mode
        syncHasChanges()
        return true
    }

    /** Reload the manager's actual saved choice after returning to this page. */
    fun resetToSavedMode() {
        restoreMode = btcBlockchainManager.restoreMode(blockchain.type)
        syncHasChanges()
    }

    private fun syncHasChanges() {
        val saved = btcBlockchainManager.restoreMode(blockchain.type)
        _hasChangesFlow.tryEmit(
            BtcRestoreSelectionRules.canSave(restoreMode, saved, restoreModes),
        )
    }
}
