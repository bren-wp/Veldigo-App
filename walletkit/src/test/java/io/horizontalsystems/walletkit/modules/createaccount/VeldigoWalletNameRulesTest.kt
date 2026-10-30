package io.horizontalsystems.walletkit.modules.createaccount

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VeldigoWalletNameRulesTest {
    @Test
    fun preservesUnicodeDisplayNamesWithoutStoringControls() {
        assertEquals("Moj novčanik 01", VeldigoWalletNameRules.input("Moj novčanik\n 01"))
        assertEquals("Štednja", VeldigoWalletNameRules.saved("  Štednja  ", "Fallback"))
        assertEquals("Wallet", VeldigoWalletNameRules.saved(" \n ", " Wallet "))
    }

    @Test
    fun cannotStoreArbitrarilyLongNames() {
        val long = "W".repeat(100)
        assertEquals(VeldigoWalletNameRules.MaxLength, VeldigoWalletNameRules.input(long).length)
        assertEquals(VeldigoWalletNameRules.MaxLength, VeldigoWalletNameRules.saved(long, "Default").length)
        assertFalse(VeldigoWalletNameRules.saved("", "Default").isBlank())
    }

    @Test
    fun cannotLeaveHalfOfASurrogatePair() {
        val name = "A".repeat(47) + "😀" + "X"
        val sanitized = VeldigoWalletNameRules.input(name)
        assertEquals("A".repeat(47), sanitized)
        assertTrue(sanitized.length <= VeldigoWalletNameRules.MaxLength)
    }

    @Test
    fun trimsFallbackAndPreventsWhitespaceOnlyNames() {
        assertEquals("Default Wallet", VeldigoWalletNameRules.saved("  ", " Default Wallet "))
    }
}
