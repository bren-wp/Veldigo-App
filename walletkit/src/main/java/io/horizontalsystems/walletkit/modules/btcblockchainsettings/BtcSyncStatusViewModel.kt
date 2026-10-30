package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.IAdapterManager
import io.horizontalsystems.walletkit.core.ITorManager
import io.horizontalsystems.walletkit.core.managers.WalletManager
import io.horizontalsystems.walletkit.modules.settings.privacy.tor.TorStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Observes the existing live BTC-family adapter states of the active account.
 * No fabricated synchronization timer, polling of external sites or secrets.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BtcSyncStatusViewModel(
    val blockchain: Blockchain,
    private val walletManager: WalletManager,
    private val adapterManager: IAdapterManager,
    private val torManager: ITorManager,
) : ViewModel() {

    internal var summary by mutableStateOf(BtcSyncStatusRules.summarize(emptyList(), false))
        private set

    val canRefresh: Boolean get() =
        !isTorWaiting() && matchingWallets().any { adapterManager.getBalanceAdapterForWallet(it) != null }

    init {
        viewModelScope.launch {
            merge(
                walletManager.activeWalletsUpdatedFlow.map { Unit },
                adapterManager.adaptersReadyFlow.map { Unit },
            )
                .onStart { emit(Unit) }
                .flatMapLatest {
                    val events = matchingWallets().mapNotNull { wallet ->
                        adapterManager.getBalanceAdapterForWallet(wallet)?.balanceStateUpdatedFlow?.map { Unit }
                    }
                    if (events.isEmpty()) {
                        flowOf(Unit)
                    } else {
                        merge(*events.toTypedArray()).onStart { emit(Unit) }
                    }
                }
                .collect { updateSnapshot() }
        }
        viewModelScope.launch {
            torManager.torStatusFlow.collect { updateSnapshot() }
        }
        updateSnapshot()
    }

    fun updateSnapshot() {
        val statuses = matchingWallets().map { wallet ->
            adapterManager.getBalanceAdapterForWallet(wallet)?.balanceState
        }
        summary = BtcSyncStatusRules.summarize(statuses, isTorWaiting())
    }

    /** User-initiated refresh of existing adapters; not a simulated rescan. */
    fun refresh() {
        if (!canRefresh) return
        matchingWallets().forEach { wallet ->
            if (adapterManager.getBalanceAdapterForWallet(wallet) != null) {
                adapterManager.refreshByWallet(wallet)
            }
        }
        updateSnapshot()
    }

    private fun matchingWallets() =
        walletManager.activeWallets.filter { it.token.blockchainType == blockchain.type }

    private fun isTorWaiting() =
        torManager.isTorEnabled && torManager.torStatusFlow.value != TorStatus.Connected

    class Factory(private val blockchain: Blockchain) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BtcSyncStatusViewModel(
                blockchain,
                App.walletManager,
                App.adapterManager,
                App.torKitManager,
            ) as T
    }
}
