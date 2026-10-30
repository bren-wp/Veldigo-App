package io.horizontalsystems.walletkit.modules.nft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.entities.nft.NftAssetShortMetadata
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.util.Locale

/**
 * Read-only NFT discovery. The legacy NFT adapters / syncer are deliberately
 * disabled in this repository; do not re-enable them from a UI layer.
 *
 * The user explicitly requests a provider lookup of their public Ethereum
 * receiving address. A provider report is NOT independently verified ownership.
 */
class NftGalleryViewModel : ViewModel() {
    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object Unsupported : State
        data object WalletNotReady : State
        data object Failed : State
        data class Ready(
            val accountId: String,
            val assets: List<NftAssetShortMetadata>,
            val collectionNames: Map<String, String>,
        ) : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state = _state.asStateFlow()
    private var loading = false

    fun load() {
        if (loading) return
        val account = App.accountManager.activeAccount
        if (account == null) {
            _state.value = State.WalletNotReady
            return
        }
        val ethereumWallet = App.walletManager.activeWallets.firstOrNull {
            it.account.id == account.id && it.token.blockchainType == BlockchainType.Ethereum
        }
        if (ethereumWallet == null) {
            _state.value = State.Unsupported
            return
        }

        loading = true
        _state.value = State.Loading
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val adapter = App.adapterManager.getReceiveAdapterForWallet(ethereumWallet)
                        ?: return@withContext null
                    val address = adapter.getFreshReceiveAddress()
                    if (address.isBlank()) return@withContext null
                    // Only request public metadata for the actually selected account.
                    App.nftMetadataManager.addressMetadata(BlockchainType.Ethereum, address)
                }
                val currentAccount = App.accountManager.activeAccount?.id
                if (!NftGallerySafety.isSameAccount(account.id, currentAccount)) {
                    _state.value = State.Idle
                } else if (response == null) {
                    _state.value = State.WalletNotReady
                } else {
                    _state.value = State.Ready(
                        accountId = account.id,
                        assets = response.assets.distinctBy { it.nftUid.uid },
                        collectionNames = response.collections.associate {
                            it.providerUid to it.name
                        },
                    )
                }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                _state.value = State.Failed
            } finally {
                loading = false
            }
        }
    }

    fun clearOnAccountChange() {
        val current = App.accountManager.activeAccount?.id
        val ready = state.value as? State.Ready ?: return
        if (!NftGallerySafety.isSameAccount(ready.accountId, current)) {
            _state.value = State.Idle
        }
    }
}

/**
 * External thumbnail URLs can track public wallet activity. Fetch is opt-in,
 * HTTPS-only, and never attempts local/private network access.
 */
internal object NftGallerySafety {
    fun isSameAccount(expected: String, current: String?): Boolean =
        expected.isNotBlank() && expected == current

    fun safeRemoteImage(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val uri = runCatching { URI(url) }.getOrNull() ?: return null
        if (!uri.scheme.equals("https", ignoreCase = true) || uri.userInfo != null) return null
        val host = uri.host?.lowercase(Locale.ROOT) ?: return null
        if (host == "localhost" || host.endsWith(".localhost") || host.endsWith(".local")) return null
        if (host.contains(':')) return null
        if (host == "127.0.0.1" || host.startsWith("127.")) return null
        if (host == "10.0.0.1" || host.startsWith("10.")) return null
        if (host.startsWith("192.168.") || host.startsWith("169.254.")) return null
        val octets = host.split(".")
        if (octets.size == 4 && octets.all { octet -> octet.toIntOrNull()?.let { it in 0..255 } == true }) {
            val first = octets[0].toInt()
            val second = octets[1].toInt()
            if (first == 0 || first >= 224 || (first == 172 && second in 16..31)) return null
        }
        return url
    }
}
