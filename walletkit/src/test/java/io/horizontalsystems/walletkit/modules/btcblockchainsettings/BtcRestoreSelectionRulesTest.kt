package io.horizontalsystems.walletkit.modules.btcblockchainsettings

import io.horizontalsystems.walletkit.entities.BtcRestoreMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BtcRestoreSelectionRulesTest {
    @Test
    fun selectsOnlyARealAvailableMode() {
        val btc = BtcRestoreMode.values().toList()
        assertEquals(BtcRestoreMode.Blockchair, BtcRestoreSelectionRules.eligible("blockchair", btc))
        assertEquals(BtcRestoreMode.Hybrid, BtcRestoreSelectionRules.eligible("hybrid", btc))
        assertEquals(BtcRestoreMode.Blockchain, BtcRestoreSelectionRules.eligible("blockchain", btc))
        assertNull(BtcRestoreSelectionRules.eligible("fake-rpc-node", btc))
        assertNull(BtcRestoreSelectionRules.eligible("", btc))
    }

    @Test
    fun unavailableBlockchairCannotBeSelectedForUnsupportedNetworks() {
        val limited = listOf(BtcRestoreMode.Hybrid, BtcRestoreMode.Blockchain)
        assertNull(BtcRestoreSelectionRules.eligible("blockchair", limited))
        assertFalse(BtcRestoreSelectionRules.canSave(
            BtcRestoreMode.Blockchair, BtcRestoreMode.Hybrid, limited
        ))
    }

    @Test
    fun sameModeDoesNotResynchronizeOrSaveAgain() {
        val modes = BtcRestoreMode.values().toList()
        for (mode in modes) {
            assertFalse(BtcRestoreSelectionRules.canSave(mode, mode, modes))
        }
    }

    @Test
    fun realChangedModesCanBeSaved() {
        val modes = listOf(BtcRestoreMode.Hybrid, BtcRestoreMode.Blockchain)
        assertTrue(BtcRestoreSelectionRules.canSave(
            BtcRestoreMode.Blockchain, BtcRestoreMode.Hybrid, modes
        ))
    }
}
