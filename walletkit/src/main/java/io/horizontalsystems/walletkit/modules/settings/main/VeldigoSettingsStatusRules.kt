package io.horizontalsystems.walletkit.modules.settings.main

/**
 * Derived warnings use the saved wallet state, never a simulated risk score.
 * Missing PIN must remain visible even if the user enabled biometrics before.
 */
internal object VeldigoSettingsStatusRules {
    fun walletAttention(allBackedUp: Boolean, nonStandardAccount: Boolean): Boolean =
        !allBackedUp || nonStandardAccount

    fun securityAttention(pinEnabled: Boolean): Boolean = !pinEnabled
}
