package io.horizontalsystems.walletkit.modules.transactionInfo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionInfoAccountAccessTest {
    @Test
    fun currentAccountMaySeeItsOwnTransaction() {
        assertTrue(TransactionInfoAccountAccess.canDisplay("wallet-a", "wallet-a"))
    }

    @Test
    fun accountSwitchNeverRevealsAnotherWalletHistory() {
        assertFalse(TransactionInfoAccountAccess.canDisplay("wallet-a", "wallet-b"))
        assertFalse(TransactionInfoAccountAccess.canDisplay("wallet-a", null))
        assertFalse(TransactionInfoAccountAccess.canDisplay("wallet-a", ""))
    }

    @Test
    fun missingRecordOwnershipIsRejected() {
        assertFalse(TransactionInfoAccountAccess.canDisplay("", "wallet-a"))
        assertFalse(TransactionInfoAccountAccess.canDisplay("", ""))
    }
}
