package io.horizontalsystems.walletkit.modules.manageaccount.backupkey

/**
 * Two full-width seed cells per row on phones. A tablet may display three.
 * Shrinking to three columns on a narrow handset truncates BIP39 words and
 * could cause an irreversible backup transcription mistake.
 */
internal object RecoveryPhraseGridRules {
    fun columns(availableWidthDp: Float): Int =
        if (availableWidthDp >= 520f) 3 else 2
}
