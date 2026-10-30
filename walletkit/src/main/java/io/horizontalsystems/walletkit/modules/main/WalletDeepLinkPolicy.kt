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

    fun isNativePaymentRequest(value: String): Boolean =
        paymentPrefixes.any(value::startsWith)
}
