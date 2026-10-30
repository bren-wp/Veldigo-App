package io.horizontalsystems.walletkit.modules.settings.main

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VeldigoSettingsStatusRulesTest {
    @Test
    fun missingBackupOrNonstandardAccountShowsWalletSecurityWarning() {
        assertTrue(VeldigoSettingsStatusRules.walletAttention(allBackedUp = false, nonStandardAccount = false))
        assertTrue(VeldigoSettingsStatusRules.walletAttention(allBackedUp = true, nonStandardAccount = true))
        assertTrue(VeldigoSettingsStatusRules.walletAttention(allBackedUp = false, nonStandardAccount = true))
        assertFalse(VeldigoSettingsStatusRules.walletAttention(allBackedUp = true, nonStandardAccount = false))
    }

    @Test
    fun missingPinStillRequiresAttention() {
        assertTrue(VeldigoSettingsStatusRules.securityAttention(pinEnabled = false))
        assertFalse(VeldigoSettingsStatusRules.securityAttention(pinEnabled = true))
    }
}
