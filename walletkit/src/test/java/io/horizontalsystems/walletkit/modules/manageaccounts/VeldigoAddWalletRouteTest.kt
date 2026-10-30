package io.horizontalsystems.walletkit.modules.manageaccounts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VeldigoAddWalletRouteTest {
    @Test
    fun standaloneChooserClosesItselfAfterSuccessfulWalletSetup() {
        val input = VeldigoAddWalletRoute.resolveInput(null)
        assertEquals(AddWalletPage::class, input.popOffOnSuccess)
        assertTrue(input.popOffInclusive)
    }

    @Test
    fun manageModeKeepsAccountListVisibleOnSuccessfulWalletSetup() {
        val fromManager = ManageAccountsModule.Input(
            ManageAccountsPage::class,
            false,
        )
        val resolved = VeldigoAddWalletRoute.resolveInput(fromManager)
        assertEquals(ManageAccountsPage::class, resolved.popOffOnSuccess)
        assertFalse(resolved.popOffInclusive)
    }

    @Test
    fun switcherModePreservesInclusivePopNavigation() {
        val fromSwitcher = ManageAccountsModule.Input(
            ManageAccountsPage::class,
            true,
        )
        val resolved = VeldigoAddWalletRoute.resolveInput(fromSwitcher)
        assertEquals(ManageAccountsPage::class, resolved.popOffOnSuccess)
        assertTrue(resolved.popOffInclusive)
    }
}
