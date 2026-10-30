package io.horizontalsystems.walletkit.modules.settings.security.passcode

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityCenterStateRulesTest {
    @Test
    fun biometricSettingCannotRepresentActiveProtectionWithoutPin() {
        assertFalse(SecurityCenterStateRules.biometricActive(false, false))
        assertFalse(SecurityCenterStateRules.biometricActive(false, true))
        assertFalse(SecurityCenterStateRules.biometricActive(true, false))
        assertTrue(SecurityCenterStateRules.biometricActive(true, true))
    }

    @Test
    fun autoLockIsOnlyShownWhenAppPinExists() {
        assertFalse(SecurityCenterStateRules.canSelectAutoLock(false))
        assertTrue(SecurityCenterStateRules.canSelectAutoLock(true))
    }

    @Test
    fun missingPinRemainsARealSecurityWarning() {
        assertTrue(SecurityCenterStateRules.isProtectionMissing(false))
        assertFalse(SecurityCenterStateRules.isProtectionMissing(true))
    }
}
