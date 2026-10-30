package io.horizontalsystems.walletkit.modules.evmnetwork.addrpc

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RpcConnectionUiRulesTest {
    @Test fun saveRequiresFreshResult() {
        assertFalse(RpcConnectionUiRules.canSave(true, false, null, false))
        assertFalse(RpcConnectionUiRules.canSave(true, false, null, true))
        assertFalse(RpcConnectionUiRules.canSave(true, true, RpcConnectionProbe.Outcome.Verified, true))
        assertFalse(RpcConnectionUiRules.canSave(false, false, RpcConnectionProbe.Outcome.Verified, true))
        assertTrue(RpcConnectionUiRules.canSave(true, false, RpcConnectionProbe.Outcome.Verified, false))
    }

    @Test fun mismatchedChainCannotBeOverridden() {
        assertFalse(RpcConnectionUiRules.canAcknowledge(RpcConnectionProbe.Outcome.WrongChain))
        assertFalse(RpcConnectionUiRules.canSave(true, false, RpcConnectionProbe.Outcome.WrongChain, true))
        assertFalse(RpcConnectionUiRules.canSave(true, false, RpcConnectionProbe.Outcome.InvalidUrl, true))
    }

    @Test fun unsupportedTransportOrOfflineRequiresExplicitAcknowledgement() {
        val outcomes = listOf(
            RpcConnectionProbe.Outcome.NotTestedTor,
            RpcConnectionProbe.Outcome.NotTestedTransport,
            RpcConnectionProbe.Outcome.NotTestedChain,
            RpcConnectionProbe.Outcome.Unreachable,
            RpcConnectionProbe.Outcome.InvalidResponse,
            RpcConnectionProbe.Outcome.UnsupportedAuth,
        )
        for (outcome in outcomes) {
            assertFalse(RpcConnectionUiRules.canSave(true, false, outcome, false))
            assertTrue(RpcConnectionUiRules.canSave(true, false, outcome, true))
        }
    }
}
