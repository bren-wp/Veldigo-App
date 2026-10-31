package io.horizontalsystems.walletkit.modules.main

/**
 * Allow only chain-native payment requests to enter the Send flow.
 *
 * The scheme check is an admission filter, not address validation.
 * AddressUriParser and the chain-specific sender must still reject invalid
 * destinations, network mismatches and unsupported payment requests.
 *
 * Intentionally excludes referral, Market, WalletConnect, TonConnect and dApp URIs.
 */
internal object WalletDeepLinkPolicy {
    private val paymentPrefixes = listOf(
        "bitcoin:",
        "ethereum:",
        "toncoin:",
        "monero:",
        "tron:",
        "solana:",
        "zcash:",
        "litecoin:",
    )

    // Untrusted Android intents must not feed huge or control-character payloads into
    // the per-chain URI parser. This is an admission guard, not payment validation.
    private const val MAX_PAYMENT_URI_LENGTH = 4096

    fun isNativePaymentRequest(value: String): Boolean =
        value.length in 1..MAX_PAYMENT_URI_LENGTH &&
            value.none { it.code < 0x20 || it.code == 0x7f } &&
            paymentPrefixes.any(value::startsWith)
}
