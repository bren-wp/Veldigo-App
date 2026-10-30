package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import java.net.URI
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RpcEndpointValidationTest {
    @Test fun supportsTlsAndTorOnionOnly() {
        assertNotNull(RpcEndpointValidation.parse("https://eth.example.org/v3/key"))
        assertNotNull(RpcEndpointValidation.parse("wss://eth.example.org/ws"))
        assertNotNull(RpcEndpointValidation.parse("http://serviceabcdef.onion/rpc"))
        assertNull(RpcEndpointValidation.parse("http://example.org/rpc"))
        assertNull(RpcEndpointValidation.parse("ws://example.org/ws"))
        assertNull(RpcEndpointValidation.parse("ftp://example.org"))
        // The current source loader only recognizes lowercase schemes.
        assertNull(RpcEndpointValidation.parse("HTTPS://example.org"))
    }

    @Test fun rejectMalformedOrCredentialBearingEndpoints() {
        assertNull(RpcEndpointValidation.parse(""))
        assertNull(RpcEndpointValidation.parse("https:///no-host"))
        assertNull(RpcEndpointValidation.parse("https://user:secret@rpc.example.org"))
        assertNull(RpcEndpointValidation.parse("https://rpc.example.org/api#fragment"))
        assertNull(RpcEndpointValidation.parse("https://rpc.example.org/a b"))
        assertNull(RpcEndpointValidation.parse("https://rpc.example.org:0"))
        assertNull(RpcEndpointValidation.parse("https://rpc.example.org:99999"))
        assertNull(RpcEndpointValidation.parse("https://rpc.example.org/" + "x".repeat(3000)))
    }

    @Test fun duplicateUrlsRequireExactUriEquality() {
        val uri = URI("https://rpc.example.org/key")
        assertTrue(RpcEndpointValidation.isDuplicate(uri, listOf(uri)))
        assertFalse(RpcEndpointValidation.isDuplicate(uri, listOf(URI("https://rpc.example.org/other"))))
    }
}
