package io.horizontalsystems.walletkit.modules.createaccount

/**
 * Wallet display names are public UI metadata, not seed material.
 * Keep names predictable for compact devices and strip non-printing input.
 * Grapheme clusters are preserved at the cutoff (no split surrogate pair).
 */
internal object VeldigoWalletNameRules {
    const val MaxLength = 48

    fun input(value: String): String {
        val clean = value.filterNot { it.isISOControl() }
        if (clean.length <= MaxLength) return clean
        val cut = clean.take(MaxLength)
        return if (cut.lastOrNull()?.isHighSurrogate() == true) cut.dropLast(1) else cut
    }

    fun saved(value: String, fallback: String): String =
        input(value.trim()).trim().ifBlank { input(fallback.trim()).trim() }
}
