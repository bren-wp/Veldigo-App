package io.horizontalsystems.walletkit.modules.manageaccounts

/**
 * Preserve the caller's intended post-create/import/watch navigation. A
 * standalone Add Wallet route uses itself as the fallback target, rather
 * than a nonexistent CreateAccountPage entry.
 */
internal object VeldigoAddWalletRoute {
    fun resolveInput(input: ManageAccountsModule.Input?): ManageAccountsModule.Input =
        input ?: ManageAccountsModule.Input(
            popOffOnSuccess = AddWalletPage::class,
            popOffInclusive = true,
        )
}
