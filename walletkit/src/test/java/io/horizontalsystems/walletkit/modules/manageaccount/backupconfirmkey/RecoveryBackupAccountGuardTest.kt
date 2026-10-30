package io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey

import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.entities.AccountOrigin
import io.horizontalsystems.walletkit.entities.AccountType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecoveryBackupAccountGuardTest {
    private fun account(
        id: String = "account-id",
        words: List<String> = listOf("alpha", "beta"),
        passphrase: String = "",
    ) = Account(
        id = id,
        name = "Wallet",
        type = AccountType.Mnemonic(words, passphrase),
        origin = AccountOrigin.Created,
        level = 0,
    )

    @Test
    fun sameUnchangedWalletCanBeMarkedBackedUp() {
        val original = account()
        assertTrue(RecoveryBackupAccountGuard.matches(original.copy(name = "Renamed"), original))
    }

    @Test
    fun aDeletedOrDifferentWalletCannotBeConfirmed() {
        val original = account()
        assertFalse(RecoveryBackupAccountGuard.matches(null, original))
        assertFalse(RecoveryBackupAccountGuard.matches(account(id = "different"), original))
    }

    @Test
    fun staleRecoveryPhraseOrPassphraseCannotOverwriteExistingWallet() {
        val original = account()
        assertFalse(RecoveryBackupAccountGuard.matches(account(words = listOf("gamma", "beta")), original))
        assertFalse(RecoveryBackupAccountGuard.matches(account(passphrase = "changed"), original))
    }
}
