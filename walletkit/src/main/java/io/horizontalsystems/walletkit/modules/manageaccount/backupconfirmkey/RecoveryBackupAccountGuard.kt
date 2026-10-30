package io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey

import io.horizontalsystems.walletkit.entities.Account

/**
 * A successful word-order challenge proves only knowledge of the account
 * loaded when the screen opened. A deleted or replaced account must not be
 * resurrected, or silently marked as backed up, by an old verification page.
 *
 * Account.equals() compares IDs only. We explicitly compare key-bearing
 * AccountType as well.
 */
internal object RecoveryBackupAccountGuard {
    fun matches(current: Account?, original: Account): Boolean =
        current != null &&
            current.id == original.id &&
            current.type == original.type &&
            current.origin == original.origin
}
