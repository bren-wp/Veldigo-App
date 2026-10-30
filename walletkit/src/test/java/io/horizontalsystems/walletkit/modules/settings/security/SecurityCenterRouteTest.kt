package io.horizontalsystems.walletkit.modules.settings.security

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SecurityCenterRouteTest {
    @Test
    fun securityCenterUsesProtectedNonPublicNavigation() {
        assertFalse(SecuritySettingsPage.screenshotEnabled)
        assertFalse(SecuritySettingsPage.accessibleWhileLocked)
    }

    @Test
    fun securityCenterRouteNeverSerializesPinOrBiometricCredentials() {
        val serialized = Json.encodeToString(SecuritySettingsPage.serializer(), SecuritySettingsPage)
        assertEquals(
            SecuritySettingsPage,
            Json.decodeFromString(SecuritySettingsPage.serializer(), serialized),
        )
        for (secret in listOf("enteredPin", "submittedPin", "mnemonic", "privateKey", "biometricToken")) {
            assertFalse(serialized.contains(secret, ignoreCase = true))
        }
    }
}
