package io.horizontalsystems.walletkit.modules.settings.security.passcode

/**
 * A stale biometric preference without an active PIN is not a working wallet
 * protection method. Display only the real effective state.
 */
internal object SecurityCenterStateRules {
    fun biometricActive(pinSet: Boolean, biometricSettingEnabled: Boolean): Boolean =
        pinSet && biometricSettingEnabled

    fun canSelectAutoLock(pinSet: Boolean): Boolean = pinSet

    fun isProtectionMissing(pinSet: Boolean): Boolean = !pinSet
}
