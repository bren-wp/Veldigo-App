package io.horizontalsystems.bankwallet.nav3

import io.horizontalsystems.walletkit.modules.pin.PinModule
import io.horizontalsystems.walletkit.modules.settings.security.setup.SecuritySetupPage
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
    fun pinCopyMustReflectActualExistingPinLength() {
        assertEquals("The reference copy must describe the real PIN, not an invented 4-6 range", 6, PinModule.PIN_COUNT)
    }

    @Test
    fun securitySetupNeverPretendsToBeAnUnlockBypass() {
        assertFalse(SecuritySetupPage.accessibleWhileLocked)
        assertTrue(SecuritySetupPage.screenshotEnabled)
    }
}
