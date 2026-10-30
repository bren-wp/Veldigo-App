package io.horizontalsystems.walletkit.modules.manageaccounts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VeldigoWalletRulesTest {
    @Test
    fun allFilterDisplaysBothTypes() {
        assertTrue(VeldigoWalletFilter.All.matches(isWatchAccount = false))
        assertTrue(VeldigoWalletFilter.All.matches(isWatchAccount = true))
    }

    @Test
    fun ownedAndWatchFiltersNeverMisclassify() {
        assertTrue(VeldigoWalletFilter.Owned.matches(isWatchAccount = false))
        assertFalse(VeldigoWalletFilter.Owned.matches(isWatchAccount = true))
        assertTrue(VeldigoWalletFilter.Watch.matches(isWatchAccount = true))
        assertFalse(VeldigoWalletFilter.Watch.matches(isWatchAccount = false))
    }

    @Test
    fun watchAddressesDoNotDemandRecoveryPhraseBackup() {
        assertFalse(VeldigoWalletStatusRules.backupRequired(true, false, false))
        assertFalse(VeldigoWalletStatusRules.showAlertIcon(true, false, false, false, false))
    }

    @Test
    fun fileBackupCountsAndOwnedWalletWithNoBackupIsWarned() {
        assertFalse(VeldigoWalletStatusRules.backupRequired(false, false, true))
        assertFalse(VeldigoWalletStatusRules.backupRequired(false, true, false))
        assertTrue(VeldigoWalletStatusRules.backupRequired(false, false, false))
        assertTrue(VeldigoWalletStatusRules.showAlertIcon(false, false, false, false, false))
        assertFalse(VeldigoWalletStatusRules.showAlertIcon(false, false, true, false, false))
    }

    @Test
    fun recoveryIntegrityWarningsRemainVisibleForOwnedWallets() {
        assertTrue(VeldigoWalletStatusRules.showAlertIcon(false, true, false, true, false))
        assertTrue(VeldigoWalletStatusRules.showAlertIcon(false, true, false, false, true))
    }
}
