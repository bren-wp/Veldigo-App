package io.horizontalsystems.walletkit.modules.evmnetwork

import java.net.URI
import java.util.Locale

/**
 * Never render RPC credentials, query values, API tokens in URI paths, or
 * userinfo on a wallet settings screen. The full endpoint is still retained
 * inside EvmSyncSourceManager for the actual network connection.
 */
internal object RpcEndpointDisplayRules {
    fun safeLabel(url: String): String {
        val uri = runCatching { URI(url) }.getOrNull() ?: return "Private RPC endpoint"
        val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return "Private RPC endpoint"
        val host = uri.host ?: return "Private RPC endpoint"
        val port = if (uri.port in 1..65535) ":${uri.port}" else ""
        return "$scheme://$host$port"
    }

    fun canDelete(isCustom: Boolean, selected: Boolean): Boolean = isCustom && !selected
}
