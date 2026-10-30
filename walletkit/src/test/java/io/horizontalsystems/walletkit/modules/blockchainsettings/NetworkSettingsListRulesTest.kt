package io.horizontalsystems.walletkit.modules.blockchainsettings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkSettingsListRulesTest {
    @Test
    fun searchesOnlyActualNetworkNamesAndSourceDescriptions() {
        assertTrue(NetworkSettingsListRules.matches("Bitcoin", "Electrum", "bit"))
        assertTrue(NetworkSettingsListRules.matches("Ethereum", "Default RPC", "rPc"))
        assertTrue(NetworkSettingsListRules.matches("Polygon", "Provider A", "  poly  "))
        assertFalse(NetworkSettingsListRules.matches("Bitcoin", "Electrum", "cardano"))
        assertFalse(NetworkSettingsListRules.matches("Solana", "Native", "networknotconfigured"))
    }

    @Test
    fun emptyQueryDoesNotRemoveConfiguredChains() {
        assertTrue(NetworkSettingsListRules.matches("Ethereum", "Provider", ""))
        assertTrue(NetworkSettingsListRules.matches("Monero", "Default", " \t "))
    }

    @Test
    fun searchInputIsBoundedAndCannotEndWithUnpairedSurrogate() {
        assertEquals("BTCRPC", NetworkSettingsListRules.normalizeQuery("BTC\nRPC"))
        assertEquals(80, NetworkSettingsListRules.normalizeQuery("x".repeat(500)).length)
        val endPair = "A".repeat(79) + "😀"
        assertEquals("A".repeat(79), NetworkSettingsListRules.normalizeQuery(endPair))
    }
}
