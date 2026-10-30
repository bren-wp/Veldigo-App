package io.horizontalsystems.walletkit.modules.blockchainsettings

/**
 * Search only the names and sync-source labels returned by actual wallet
 * network managers. No invented network categories, status or endpoint data.
 */
internal object NetworkSettingsListRules {
    const val MaxQueryLength = 80

    fun normalizeQuery(value: String): String {
        val clean = value.filterNot { Character.isISOControl(it) }
        val cut = clean.take(MaxQueryLength)
        return if (cut.lastOrNull()?.isHighSurrogate() == true) cut.dropLast(1) else cut
    }

    fun matches(name: String, source: String, query: String): Boolean {
        val words = query.trim()
        return words.isBlank() ||
            name.contains(words, ignoreCase = true) ||
            source.contains(words, ignoreCase = true)
    }
}
