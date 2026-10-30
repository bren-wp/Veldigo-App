package io.horizontalsystems.bankwallet.nav3

import io.horizontalsystems.walletkit.modules.pin.PinModule
import io.horizontalsystems.walletkit.modules.pin.SetPinPage
import io.horizontalsystems.walletkit.modules.settings.security.setup.SecuritySetupPage
import io.horizontalsystems.walletkit.modules.settings.security.setup.EnableFingerprintPage
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The reference chooser must route through existing security code, not credentials in nav state. */
class SecuritySetupRouteTest {
    @Test
    fun securitySetupHasSerializableNavigationKey() {
        val encoded = Json.encodeToString(SecuritySetupPage.serializer(), SecuritySetupPage)
        assertEquals(SecuritySetupPage, Json.decodeFromString(SecuritySetupPage.serializer(), encoded))
        assertFalse(encoded.contains("recovery", ignoreCase = true))
        assertFalse(encoded.contains("passphrase", ignoreCase = true))
        assertFalse(encoded.contains("privateKey", ignoreCase = true))
    }

    @Test
    fun fingerprintRouteSerializesOnlyNavigationIntentAndStaysProtected() {
        val page = EnableFingerprintPage(finishOnSuccess = true)
        val json = Json.encodeToString(EnableFingerprintPage.serializer(), page)
        val restored = Json.decodeFromString(EnableFingerprintPage.serializer(), json)

        assertTrue(restored.finishOnSuccess)
        assertFalse(restored.screenshotEnabled)
        assertFalse(restored.accessibleWhileLocked)
        assertFalse(json.contains("pin", ignoreCase = true))
        assertFalse(json.contains("biometricToken", ignoreCase = true))
        assertFalse(json.contains("mnemonic", ignoreCase = true))
    }

    @Test
    fun realPinSetupRouteIsProtectedAndNeverSerializesPlaintextDigits() {
        val page = SetPinPage()
        val encoded = Json.encodeToString(SetPinPage.serializer(), page)
        assertFalse(page.screenshotEnabled)
        assertFalse(encoded.contains("enteredPin", ignoreCase = true))
        assertFalse(encoded.contains("submittedPin", ignoreCase = true))
        assertFalse(encoded.contains("pinValue", ignoreCase = true))
        assertEquals(page, Json.decodeFromString(SetPinPage.serializer(), encoded))
    }

    @Test
    fun pinCopyMustReflectActualExistingPinLength() {
        assertEquals("The reference copy must describe the real PIN, not an invented 4-6 range", 6, PinModule.PIN_COUNT)
    }

    @Test
    fun securitySetupNeverPretendsToBeAnUnlockBypass() {
        assertFalse(SecuritySetupPage.accessibleWhileLocked)
        assertFalse(SecuritySetupPage.screenshotEnabled)
    }
}
