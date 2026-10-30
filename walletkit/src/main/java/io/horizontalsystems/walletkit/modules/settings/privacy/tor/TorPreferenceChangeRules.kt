package io.horizontalsystems.walletkit.modules.settings.privacy.tor

/**
 * Prevent a redundant restart when selecting the current Tor preference.
 * Persist only after the user explicitly confirms the pending opposite state.
 */
internal object TorPreferenceChangeRules {
    fun requiresConfirmation(current: Boolean, requested: Boolean): Boolean =
        current != requested

    fun confirmTarget(current: Boolean, pending: Boolean?): Boolean? =
        pending?.takeIf { it != current }
}
