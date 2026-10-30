package io.horizontalsystems.walletkit.modules.settings.security.setup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Test biometric enrollment gate without pretending that a unit test can verify a real sensor. */
class BiometricSetupGateTest {
    @Test
    fun enrollmentRequiresSecureDevicePinAndSupportedBiometrics() {
        assertEquals(
            BiometricSetupPreflight.DEVICE_LOCK_REQUIRED,
            BiometricSetupGate.preflight(deviceSecure = false, pinSet = true, biometricAvailable = true),
        )
        assertEquals(
            BiometricSetupPreflight.PIN_REQUIRED,
            BiometricSetupGate.preflight(deviceSecure = true, pinSet = false, biometricAvailable = true),
        )
        assertEquals(
            BiometricSetupPreflight.BIOMETRIC_UNAVAILABLE,
            BiometricSetupGate.preflight(deviceSecure = true, pinSet = true, biometricAvailable = false),
        )
        assertEquals(
            BiometricSetupPreflight.READY,
            BiometricSetupGate.preflight(deviceSecure = true, pinSet = true, biometricAvailable = true),
        )
    }

    @Test
    fun cancelOrFailureMustNotEnableBiometrics() {
        assertFalse(BiometricSetupGate.mayEnableAfterResult(true, true, true, authenticated = false))
        assertFalse(BiometricSetupGate.mayEnableAfterResult(false, true, true, authenticated = true))
        assertFalse(BiometricSetupGate.mayEnableAfterResult(true, false, true, authenticated = true))
        assertFalse(BiometricSetupGate.mayEnableAfterResult(true, true, false, authenticated = true))
    }

    @Test
    fun verifiedAndroidResultMayEnableWhenAllPrerequisitesStillHold() {
        assertTrue(BiometricSetupGate.mayEnableAfterResult(true, true, true, authenticated = true))
    }
}
