package io.horizontalsystems.walletkit.modules.transactionInfo

/**
 * A transaction from another wallet must never be shown after account switch.
 * Initial account loading is handled by the page without rendering private data.
 */
internal object TransactionInfoAccountAccess {
    fun canDisplay(recordAccountId: String, activeAccountId: String?): Boolean =
        recordAccountId.isNotBlank() &&
            !activeAccountId.isNullOrBlank() &&
            recordAccountId == activeAccountId
}
