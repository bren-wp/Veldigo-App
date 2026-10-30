package io.horizontalsystems.walletkit.modules.pin.set

import io.horizontalsystems.walletkit.modules.pin.PinModule

/**
 * Only single decimal digits may enter the 6-digit PIN setup state machine.
 * A completed screen must reject additional input while closing.
 */
internal object PinSetInputRules {
    fun canAppend(digit: Int, enteredCount: Int, finished: Boolean): Boolean =
        !finished && digit in 0..9 && enteredCount in 0 until PinModule.PIN_COUNT
}
