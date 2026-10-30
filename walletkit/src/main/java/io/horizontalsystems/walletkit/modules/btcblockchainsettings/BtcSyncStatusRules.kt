package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import io.horizontalsystems.walletkit.core.AdapterState

/**
 * Derive an honest, non-sensitive Bitcoin-family sync summary from the actual
 * adapter states. Do not invent percentages or claim every wallet is synced
 * merely because one Bitcoin adapter completed.
 */
internal object BtcSyncStatusRules {
    enum class Phase {
        NoWallet, WaitingForAdapter, WaitingForTor, Connecting,
        Syncing, SearchingTransactions, Synced, Failed,
    }

    data class Summary(
        val phase: Phase,
        val wallets: Int,
        val adapters: Int,
        val synced: Int,
        val progress: Int? = null,
        val remainingBlocks: Long? = null,
        val transactionsFound: Int? = null,
    )

    fun summarize(
        states: List<AdapterState?>,
        torWaiting: Boolean,
    ): Summary {
        val walletCount = states.size
        val adapterCount = states.count { it != null }
        val syncedCount = states.count { it === AdapterState.Synced }
        val phase = when {
            states.isEmpty() -> Phase.NoWallet
            torWaiting -> Phase.WaitingForTor
            adapterCount == 0 -> Phase.WaitingForAdapter
            states.any { it is AdapterState.NotSynced } -> Phase.Failed
            states.all { it === AdapterState.Synced } -> Phase.Synced
            states.any { it is AdapterState.Syncing || it is AdapterState.Downloading } -> Phase.Syncing
            states.any { it is AdapterState.SearchingTxs } -> Phase.SearchingTransactions
            states.any { it == null } -> Phase.WaitingForAdapter
            else -> Phase.Connecting
        }

        // Only show a percentage and block remainder for one known adapter,
        // not a fabricated average across multiple independent wallets.
        val onlySync = if (states.size == 1) states.singleOrNull() as? AdapterState.Syncing else null
        val onlySearching = if (states.size == 1) states.singleOrNull() as? AdapterState.SearchingTxs else null
        return Summary(
            phase = phase,
            wallets = walletCount,
            adapters = adapterCount,
            synced = syncedCount,
            progress = if (phase == Phase.Syncing) onlySync?.progress?.takeIf { it in 0..100 } else null,
            remainingBlocks = if (phase == Phase.Syncing) onlySync?.blocksRemained?.takeIf { it >= 0L } else null,
            transactionsFound = if (phase == Phase.SearchingTransactions)
                onlySearching?.count?.takeIf { it >= 0 } else null,
        )
    }
}
