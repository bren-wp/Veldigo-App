package io.horizontalsystems.walletkit.modules.blockchainsettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.core.imageUrl
import io.horizontalsystems.walletkit.core.order
import kotlinx.coroutines.launch

/**
 * View state is derived from real ChainRegistry and EVM providers. Compose
 * mutations happen in a single collector; do not launch a second coroutine
 * for every refresh and risk publishing stale network ordering.
 */
class BlockchainSettingsViewModel(
    private val service: BlockchainSettingsService,
) : ViewModel() {

    var btcLikeChains by mutableStateOf<List<BlockchainSettingsModule.BlockchainViewItem>>(emptyList())
        private set

    var otherChains by mutableStateOf<List<BlockchainSettingsModule.BlockchainViewItem>>(emptyList())
        private set

    var loaded by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            service.blockchainItemsFlow.collect { sync(it) }
        }
        // Wait for the first real service snapshot before showing the empty
        // state; a pending initial load does not mean no networks exist.
        service.start()
    }

    override fun onCleared() {
        service.stop()
        super.onCleared()
    }

    private fun sync(items: List<BlockchainSettingsModule.BlockchainItem>) {
        val btc = items
            .filterIsInstance<BlockchainSettingsModule.BlockchainItem.Chain>()
            .filter { it.btcLike }
            .map { it.toViewItem() }
            .sortedBy { it.blockchainItem.blockchain.type.order }

        val others = items
            .mapNotNull { item ->
                when (item) {
                    is BlockchainSettingsModule.BlockchainItem.Evm ->
                        BlockchainSettingsModule.BlockchainViewItem(
                            title = item.blockchain.name,
                            subtitle = item.syncSource.name,
                            imageUrl = item.blockchain.type.imageUrl,
                            blockchainItem = item,
                        )
                    is BlockchainSettingsModule.BlockchainItem.Chain ->
                        if (item.btcLike) null else item.toViewItem()
                }
            }
            .sortedBy { it.blockchainItem.blockchain.type.order }

        btcLikeChains = btc
        otherChains = others
        loaded = true
    }

    private fun BlockchainSettingsModule.BlockchainItem.Chain.toViewItem() =
        BlockchainSettingsModule.BlockchainViewItem(
            title = blockchain.name,
            subtitle = subtitle,
            imageUrl = blockchain.type.imageUrl,
            blockchainItem = this,
        )
}
