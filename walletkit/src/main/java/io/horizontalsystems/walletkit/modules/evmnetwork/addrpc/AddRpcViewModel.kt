package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.Caution
import io.horizontalsystems.walletkit.core.managers.EvmSyncSourceManager
import io.horizontalsystems.walletkit.core.providers.Translator
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.marketkit.models.Blockchain

class AddRpcViewModel(
    private val blockchain: Blockchain,
    private val evmSyncSourceManager: EvmSyncSourceManager
) : ViewModel() {

    private var url = ""
    private var auth: String? = null
    private var urlCaution: Caution? = null

    var viewState by mutableStateOf(AddRpcViewState(null))
        private set

    fun onEnterBasicAuth(basicAuth: String) {
        auth = basicAuth.trim()
    }

    fun onEnterRpcUrl(enteredUrl: String) {
        urlCaution = null
        url = enteredUrl.trim()
        syncState()
    }

    fun onScreenClose() {
        viewState = AddRpcViewState()
    }

    fun onAddClick() {
        val sourceUri = RpcEndpointValidation.parse(url)
        if (sourceUri == null) {
            urlCaution = Caution(
                Translator.getString(R.string.AddEvmSyncSource_Error_InvalidUrl),
                Caution.Type.Error,
            )
            syncState()
            return
        }

        val existingSources = evmSyncSourceManager.allSyncSources(blockchain.type)
        if (RpcEndpointValidation.isDuplicate(sourceUri, existingSources.flatMap { it.uris })) {
            urlCaution = Caution(
                Translator.getString(R.string.AddEvmSyncSource_Warning_UrlExists),
                Caution.Type.Warning,
            )
            syncState()
            return
        }

        evmSyncSourceManager.saveSyncSource(blockchain.type, url, auth?.takeIf(String::isNotBlank))
        // Never retain basic authentication text in the setup ViewModel after
        // the existing manager persists it.
        auth = null
        url = ""
        urlCaution = null
        viewState = AddRpcViewState(closeScreen = true)

        stat(
            page = StatPage.BlockchainSettingsEvmAdd,
            event = StatEvent.AddEvmSource(blockchain.uid),
        )
    }

    override fun onCleared() {
        url = ""
        auth = null
        super.onCleared()
    }

    private fun syncState() {
        viewState = AddRpcViewState(urlCaution)
    }
}

data class AddRpcViewState(
    val urlCaution: Caution? = null,
    val closeScreen: Boolean = false
)