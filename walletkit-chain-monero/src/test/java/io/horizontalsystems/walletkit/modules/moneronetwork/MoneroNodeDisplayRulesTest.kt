package io.horizontalsystems.walletkit.modules.moneronetwork

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneroNodeDisplayRulesTest {
    @Test fun builtInNodeNamesRemainVisibleWithoutARequest() {
        assertEquals(
            "node.xmr.rocks:18089",
            MoneroNodeDisplayRules.safeEndpoint("node.xmr.rocks:18089")
        )
    }

    @Test fun customUrlsCannotLeakCredentialsOrApiKeys() {
        assertEquals(
            "https://xmr.example.org:18081",
            MoneroNodeDisplayRules.safeEndpoint(
                "https://name:password@xmr.example.org:18081/private-token?apiKey=secret#fragment"
            )
        )
        assertEquals(
            "http://hidden.onion:18081",
            MoneroNodeDisplayRules.safeEndpoint("http://hidden.onion:18081/private")
        )
    }

    @Test fun malformedEndpointsAreRedactedRatherThanEchoed() {
        assertEquals("Private node", MoneroNodeDisplayRules.safeEndpoint("secret-value////"))
    }

    @Test fun selectedNodeCannotBeDeleted() {
        assertFalse(MoneroNodeDisplayRules.canRemove(false, false))
        assertFalse(MoneroNodeDisplayRules.canRemove(false, true))
        assertFalse(MoneroNodeDisplayRules.canRemove(true, true))
        assertTrue(MoneroNodeDisplayRules.canRemove(true, false))
    }
}
