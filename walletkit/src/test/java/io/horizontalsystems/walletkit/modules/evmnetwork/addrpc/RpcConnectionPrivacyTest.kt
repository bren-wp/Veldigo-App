package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class RpcConnectionPrivacyTest {
    @Test
    fun configuredTorNeverUsesStandaloneDirectHttpClient() = runBlocking {
        val client = OkHttpClient.Builder()
            .addInterceptor { throw AssertionError("Network request must never occur") }
            .build()
        val probe = RpcConnectionProbe(client)
        val result = probe.check(
            blockchainType = BlockchainType.Ethereum,
            url = "https://rpc.example.org",
            basicAuth = "",
            torEnabled = true,
        )
        assertEquals(RpcConnectionProbe.Outcome.NotTestedTor, result.outcome)
    }

    @Test
    fun websocketAndOnionMustNotLeakThroughPlainHttpsProbe() = runBlocking {
        val client = OkHttpClient.Builder()
            .addInterceptor { throw AssertionError("Unexpected direct network request") }
            .build()
        val probe = RpcConnectionProbe(client)
        for (url in listOf("wss://rpc.example.org/ws", "http://abcdef123.onion/rpc")) {
            val result = probe.check(
                BlockchainType.Ethereum, url, "", torEnabled = false
            )
            assertEquals(RpcConnectionProbe.Outcome.NotTestedTransport, result.outcome)
        }
    }

    @Test
    fun unknownBlockchainNeverClaimsVerifiedIdentity() = runBlocking {
        val probe = RpcConnectionProbe(
            OkHttpClient.Builder()
                .addInterceptor { throw AssertionError("Unexpected network request") }
                .build()
        )
        val result = probe.check(
            BlockchainType.Tron, "https://rpc.example.org", "", torEnabled = false
        )
        assertEquals(RpcConnectionProbe.Outcome.NotTestedChain, result.outcome)
    }
}
