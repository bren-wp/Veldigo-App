package io.horizontalsystems.walletkit.modules.moneronetwork

import java.net.URI
import java.util.Locale

/**
 * Never echo node RPC URL credentials, private paths, or query tokens.
 * Existing default Monero nodes use plain host:port identifiers, while saved
 * custom nodes contain absolute HTTPS or permitted onion HTTP URLs.
 */
internal object MoneroNodeDisplayRules {
    fun safeEndpoint(hostOrUrl: String): String {
        val value = hostOrUrl.trim()
        if (Regex("^[a-zA-Z0-9.-]+:[0-9]{1,5}$").matches(value)) {
            return value
        }
        val uri = runCatching { URI(value) }.getOrNull()
            ?: return "Private node"
        val scheme = uri.scheme?.lowercase(Locale.ROOT)
            ?: return "Private node"
        val host = uri.host ?: return "Private node"
        if (scheme !in setOf("https", "http")) return "Private node"
        val port = if (uri.port in 1..65535) ":${uri.port}" else ""
        return "$scheme://$host$port"
    }

    /** Never delete the node that currently serves the wallet. */
    fun canRemove(isCustom: Boolean, selected: Boolean): Boolean =
        isCustom && !selected
}
