package io.horizontalsystems.walletkit.modules.pin.set

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinSetInputRulesTest {
    @Test
    fun sixDigitPinAcceptsOnlySingleDecimalDigits() {
        for (digit in 0..9) {
            assertTrue(PinSetInputRules.canAppend(digit, enteredCount = 0, finished = false))
            assertTrue(PinSetInputRules.canAppend(digit, enteredCount = 5, finished = false))
        }
        assertFalse(PinSetInputRules.canAppend(-1, 0, false))
        assertFalse(PinSetInputRules.canAppend(10, 0, false))
        assertFalse(PinSetInputRules.canAppend(11, 0, false))
    }

    @Test
    fun completionAndFullPinBlockFurtherEntry() {
        assertFalse(PinSetInputRules.canAppend(1, 6, false))
        assertFalse(PinSetInputRules.canAppend(1, 7, false))
        assertFalse(PinSetInputRules.canAppend(1, 0, true))
        assertFalse(PinSetInputRules.canAppend(1, -1, false))
    }
}
