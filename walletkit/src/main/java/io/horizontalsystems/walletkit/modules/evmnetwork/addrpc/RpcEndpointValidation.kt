package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import io.horizontalsystems.walletkit.core.utils.OnionNodeUrl
import java.net.URI
import java.util.Locale

/**
 * Only TLS-backed public RPC or ordinary HTTP for valid .onion hosts.
 * Reject embedded passwords and fragments before storing any custom node.
 */
internal object RpcEndpointValidation {
    const val MaxUrlLength = 2048

    fun parse(value: String): URI? {
        if (value.isBlank() || value.length > MaxUrlLength || value.any(Char::isWhitespace)) return null
        val uri = runCatching { URI(value) }.getOrNull() ?: return null
        val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
        if (uri.host.isNullOrBlank() || uri.rawUserInfo != null || uri.rawFragment != null) return null
        if (uri.port !in -1..65535 || uri.port == 0) return null
        val secure = scheme == "https" || scheme == "wss"
        return if (secure || OnionNodeUrl.isHttpOnion(uri)) uri else null
    }

    fun isDuplicate(value: URI, existing: Iterable<URI>): Boolean =
        existing.any { it == value }
}
