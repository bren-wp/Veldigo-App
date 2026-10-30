package io.horizontalsystems.walletkit.modules.manageaccounts

/** The mockup's "Main"/"Savings" group names are illustrative, not stored wallet types. */
internal enum class VeldigoWalletFilter {
    All,
    Owned,
    Watch;

    fun matches(isWatchAccount: Boolean): Boolean = when (this) {
        All -> true
        Owned -> !isWatchAccount
        Watch -> isWatchAccount
    }
}

/**
 * Watch-only addresses do not contain signing keys or recovery words, so do
 * not incorrectly demand a mnemonic backup. An owned wallet is protected
 * by either an acknowledged seed backup or an existing file backup.
 */
internal object VeldigoWalletStatusRules {
    fun backupRequired(
        isWatchAccount: Boolean,
        isBackedUp: Boolean,
        isFileBackedUp: Boolean,
    ): Boolean = !isWatchAccount && !isBackedUp && !isFileBackedUp

    fun showAlertIcon(
        isWatchAccount: Boolean,
        isBackedUp: Boolean,
        isFileBackedUp: Boolean,
        nonStandard: Boolean,
        nonRecommended: Boolean,
    ): Boolean = !isWatchAccount && (
        backupRequired(isWatchAccount, isBackedUp, isFileBackedUp)
            || nonStandard || nonRecommended
    )
}
