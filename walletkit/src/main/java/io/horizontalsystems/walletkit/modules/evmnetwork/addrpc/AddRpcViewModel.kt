package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.core.Caution
import io.horizontalsystems.walletkit.core.managers.EvmSyncSourceManager
import io.horizontalsystems.walletkit.core.providers.Translator
import io.horizontalsystems.walletkit.core.stats.StatEvent
import io.horizontalsystems.walletkit.core.stats.StatPage
import io.horizontalsystems.walletkit.core.stats.stat
import io.horizontalsystems.marketkit.models.Blockchain
import kotlinx.coroutines.launch

class AddRpcViewModel(
    private val blockchain: Blockchain,
    private val evmSyncSourceManager: EvmSyncSourceManager,
    private val probe: RpcConnectionProbe = RpcConnectionProbe(),
) : ViewModel() {

    var url by mutableStateOf("")
        private set
    var auth by mutableStateOf("")
        private set
    private var urlCaution: Caution? = null
    private var probeRevision = 0

    var connectionResult by mutableStateOf<RpcConnectionProbe.Result?>(null)
        private set
    var checkingConnection by mutableStateOf(false)
        private set
    var riskAcknowledged by mutableStateOf(false)
        private set

    val canSave: Boolean
        get() = RpcConnectionUiRules.canSave(
            endpointPresent = url.isNotBlank(),
            checking = checkingConnection,
            result = connectionResult?.outcome,
            acknowledged = riskAcknowledged,
        )

    fun onAcknowledgeUnverified(checked: Boolean) {
        riskAcknowledged = checked
    }

    fun onTestConnection() {
        if (checkingConnection || url.isBlank()) return
        val checkRevision = ++probeRevision
        val checkedUrl = url.trim()
        val checkedAuth = auth
        checkingConnection = true
        connectionResult = null
        riskAcknowledged = false
        viewModelScope.launch {
            val result = probe.check(
                blockchainType = blockchain.type,
                url = checkedUrl,
                basicAuth = checkedAuth,
                torEnabled = App.torKitManager.isTorEnabled,
            )
            // An old response must never mark newly edited URL/auth as safe.
            if (checkRevision == probeRevision) {
                connectionResult = result
                checkingConnection = false
            }
        }
    }

    var viewState by mutableStateOf(AddRpcViewState(null))
        private set

    fun onEnterBasicAuth(basicAuth: String) {
        invalidateConnectionCheck()
        auth = basicAuth.take(1024)
    }

    fun onEnterRpcUrl(enteredUrl: String) {
        invalidateConnectionCheck()
        urlCaution = null
        url = enteredUrl.take(4096).trim()
        syncState()
    }

    fun onScreenClose() {
        invalidateConnectionCheck()
        url = ""
        auth = ""
        urlCaution = null
        viewState = AddRpcViewState()
    }

    fun onAddClick() {
        if (!canSave) return
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
        invalidateConnectionCheck()
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
        invalidateConnectionCheck()
        url = ""
        auth = ""
        super.onCleared()
    }

    private fun invalidateConnectionCheck() {
        probeRevision++
        checkingConnection = false
        connectionResult = null
        riskAcknowledged = false
    }

    private fun syncState() {
        viewState = AddRpcViewState(urlCaution)
    }
}

data class AddRpcViewState(
    val urlCaution: Caution? = null,
    val closeScreen: Boolean = false
)