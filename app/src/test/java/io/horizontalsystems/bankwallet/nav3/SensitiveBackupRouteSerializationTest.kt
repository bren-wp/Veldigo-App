package io.horizontalsystems.bankwallet.nav3

import io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey.BackupConfirmKeyPage
import io.horizontalsystems.walletkit.modules.manageaccount.backupkey.BackupKeyPage
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Sensitive BIP39 word lists and optional passphrases must never be serialized
 * into persisted Navigation 3 page data. These route payloads contain IDs only.
 */
class SensitiveBackupRouteSerializationTest {
    @Test
    fun backupPageContainsOnlyAccountIdAndKeepsScreenshotProtection() {
        val page = BackupKeyPage("wallet-id-backup-test")
        val encoded = Json.encodeToString(BackupKeyPage.serializer(), page)
        val restored = Json.decodeFromString(BackupKeyPage.serializer(), encoded)

        assertEquals("wallet-id-backup-test", restored.accountId)
        assertTrue(encoded.contains("wallet-id-backup-test"))
        assertFalse(encoded.contains("mnemonic", ignoreCase = true))
        assertFalse(encoded.contains("passphrase", ignoreCase = true))
        assertFalse(page.screenshotEnabled)
        assertFalse(restored.screenshotEnabled)
    }

    @Test
    fun confirmationPageContainsOnlyAccountIdAndKeepsScreenshotProtection() {
        val page = BackupConfirmKeyPage("wallet-id-verify-test")
        val encoded = Json.encodeToString(BackupConfirmKeyPage.serializer(), page)
        val restored = Json.decodeFromString(BackupConfirmKeyPage.serializer(), encoded)

        assertEquals("wallet-id-verify-test", restored.accountId)
        assertTrue(encoded.contains("wallet-id-verify-test"))
        assertFalse(encoded.contains("mnemonic", ignoreCase = true))
        assertFalse(encoded.contains("passphrase", ignoreCase = true))
        assertFalse(page.screenshotEnabled)
        assertFalse(restored.screenshotEnabled)
    }
}
