package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import io.horizontalsystems.marketkit.models.BlockchainType
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RpcChainIdentityRulesTest {
    @Test
    fun validatesHexAndRejectsEmptyNegativeOrInvalidIdentity() {
        assertEquals(BigInteger.ONE, RpcChainIdentityRules.parseHex("0x1"))
        assertEquals(BigInteger.valueOf(8453), RpcChainIdentityRules.parseHex("0x2105"))
        for (raw in listOf(null, "", "1", "0x", "0x0", "0xGG", "0x-1", "0x" + "a".repeat(80))) {
            assertNull(RpcChainIdentityRules.parseHex(raw))
        }
    }

    @Test
    fun expectedMainnetsCannotBeSpoofedByAnotherMainnet() {
        assertTrue(RpcChainIdentityRules.matches(
            RpcChainIdentityRules.expected(BlockchainType.Ethereum), BigInteger.ONE
        ))
        assertFalse(RpcChainIdentityRules.matches(
            RpcChainIdentityRules.expected(BlockchainType.Base), BigInteger.ONE
        ))
        assertEquals(BigInteger.valueOf(56), RpcChainIdentityRules.expected(BlockchainType.BinanceSmartChain))
        assertEquals(BigInteger.valueOf(42161), RpcChainIdentityRules.expected(BlockchainType.ArbitrumOne))
    }

    @Test
    fun unknownChainsNeverGetVerifiedStatusByAccident() {
        assertFalse(RpcChainIdentityRules.matches(null, BigInteger.ONE))
    }
}
