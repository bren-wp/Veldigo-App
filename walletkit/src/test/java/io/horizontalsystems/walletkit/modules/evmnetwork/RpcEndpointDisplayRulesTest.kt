package io.horizontalsystems.walletkit.modules.evmnetwork

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RpcEndpointDisplayRulesTest {
    @Test fun hidesApiKeysUserinfoAndQueryParameters() {
        assertEquals(
            "https://rpc.example.org:8443",
            RpcEndpointDisplayRules.safeLabel(
                "https://user:secret@rpc.example.org:8443/v2/api-private-key?token=secret"
            )
        )
        assertEquals(
            "wss://ws.example.org",
            RpcEndpointDisplayRules.safeLabel("wss://ws.example.org/private/123?api=secret")
        )
    }

    @Test fun hidesUnparseableEndpointRatherThanEchoingIt() {
        assertEquals("Private RPC endpoint", RpcEndpointDisplayRules.safeLabel("%%%my_key"))
    }

    @Test fun onlyUnselectedCustomSourceIsRemovable() {
        assertFalse(RpcEndpointDisplayRules.canDelete(false, false))
        assertFalse(RpcEndpointDisplayRules.canDelete(false, true))
        assertFalse(RpcEndpointDisplayRules.canDelete(true, true))
        assertTrue(RpcEndpointDisplayRules.canDelete(true, false))
    }
}
