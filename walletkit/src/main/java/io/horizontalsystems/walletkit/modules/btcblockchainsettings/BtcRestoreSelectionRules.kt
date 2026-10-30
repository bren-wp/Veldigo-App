package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import io.horizontalsystems.walletkit.entities.BtcRestoreMode

/**
 * Bitcoin-family restore sources are selected only from those actually
 * supported by BtcBlockchainManager for the currently opened blockchain.
 * Never invent custom node URLs or silently pick an unsupported source.
 */
internal object BtcRestoreSelectionRules {
    fun eligible(
        rawMode: String,
        availableModes: List<BtcRestoreMode>,
    ): BtcRestoreMode? =
        availableModes.firstOrNull { it.raw == rawMode }

    fun canSave(
        pendingMode: BtcRestoreMode,
        savedMode: BtcRestoreMode,
        availableModes: List<BtcRestoreMode>,
    ): Boolean =
        pendingMode != savedMode && pendingMode in availableModes
}
