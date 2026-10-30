package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RpcChainIdResponseTest {
    @Test fun parsesValidEthereumJsonRpcResponse() {
        assertEquals(BigInteger.ONE, RpcChainIdResponse.parse(
            """{"jsonrpc":"2.0","id":1,"result":"0x1"}"""
        ))
        assertEquals(BigInteger.valueOf(8453), RpcChainIdResponse.parse(
            """{"jsonrpc":"2.0","id":1,"result":"0x2105"}"""
        ))
    }

    @Test fun rejectsMalformedAndServerErrorResponses() {
        for (response in listOf(
            "", "not JSON", "[]", "{}",
            """{"jsonrpc":"2.0","id":2,"result":"0x1"}""",
            """{"jsonrpc":"1.0","id":1,"result":"0x1"}""",
            """{"jsonrpc":"2.0","id":1,"error":{"code":-32601,"message":"unknown method"}}""",
            """{"jsonrpc":"2.0","id":1,"result":"0x0"}""",
            """{"jsonrpc":"2.0","id":1,"result":"secret"}""",
        )) {
            assertNull(RpcChainIdResponse.parse(response))
        }
    }
}
