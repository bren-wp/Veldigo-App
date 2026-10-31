package io.horizontalsystems.walletkit.modules.main

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletDeepLinkPolicyTest {
    @Test
    fun acceptsNativePaymentSchemesForFurtherChainValidation() {
        for (prefix in listOf("bitcoin:", "ethereum:", "toncoin:", "monero:",
            "tron:", "solana:", "zcash:", "litecoin:")) {
            assertTrue(prefix, WalletDeepLinkPolicy.isNativePaymentRequest(prefix + "destination"))
        }
    }

    @Test
    fun rejectsOversizedAndControlCharacterPaymentLinksBeforeParsing() {
        assertFalse(WalletDeepLinkPolicy.isNativePaymentRequest("bitcoin:" + "x".repeat(4097)))
        assertFalse(WalletDeepLinkPolicy.isNativePaymentRequest("bitcoin:destination\nnext"))
        assertFalse(WalletDeepLinkPolicy.isNativePaymentRequest("ethereum:destination\rnext"))
        assertFalse(WalletDeepLinkPolicy.isNativePaymentRequest("solana:destination\u0000next"))
        assertTrue(WalletDeepLinkPolicy.isNativePaymentRequest("bitcoin:destination?amount=1.25"))
    }

    @Test
    fun rejectsMarketsReferralsAndDappConnectionLinks() {
        for (uri in listOf(
            "wc:2@1?bridge=example",
            "unstoppable.money://wc?uri=wc:example",
            "https://unstoppable.money/referral?userId=1",
            "https://example.com/market/bitcoin",
            "tonconnect://connect",
            "https://example.com",
            "",
        )) {
            assertFalse(uri, WalletDeepLinkPolicy.isNativePaymentRequest(uri))
        }
    }
}
