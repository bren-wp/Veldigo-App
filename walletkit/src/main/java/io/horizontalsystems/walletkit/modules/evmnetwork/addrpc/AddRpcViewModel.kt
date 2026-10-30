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

    var url by mutableStateOf("")
        private set
    var auth by mutableStateOf("")
        private set
    private var urlCaution: Caution? = null

    var viewState by mutableStateOf(AddRpcViewState(null))
        private set

    fun onEnterBasicAuth(basicAuth: String) {
        auth = basicAuth.take(1024)
    }

    fun onEnterRpcUrl(enteredUrl: String) {
        urlCaution = null
        url = enteredUrl.trim()
        syncState()
    }

    fun onScreenClose() {
        url = ""
        auth = ""
        urlCaution = null
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

        evmSyncSourceManager.saveSyncSource(blockchain.type, url, auth.trim().takeIf(String::isNotBlank))
        // Never retain basic authentication text in the setup ViewModel after
        // the existing manager persists it.
        auth = ""
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
        auth = ""
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