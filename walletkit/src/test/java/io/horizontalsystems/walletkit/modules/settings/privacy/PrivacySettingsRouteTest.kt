package io.horizontalsystems.walletkit.modules.settings.privacy

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PrivacySettingsRouteTest {
    @Test
    fun privacySettingsDoNotBypassWalletLock() {
        assertFalse(PrivacySettingsPage.screenshotEnabled)
        assertFalse(PrivacySettingsPage.accessibleWhileLocked)
    }

    @Test
    fun navigationDoesNotSerializeNetworkCredentialsOrWalletSecrets() {
        val route = Json.encodeToString(PrivacySettingsPage.serializer(), PrivacySettingsPage)
        assertEquals(
            PrivacySettingsPage,
            Json.decodeFromString(PrivacySettingsPage.serializer(), route),
        )
        for (secretName in listOf("mnemonic", "privateKey", "torPassword", "pinValue")) {
            assertFalse(route.contains(secretName, ignoreCase = true))
        }
    }
}
