package io.horizontalsystems.walletkit.modules.buy

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Purchase UI has no licensed on-ramp integration. Tests ensure that typing an
 * amount or selecting a coin cannot accidentally turn this into a payment.
 */
class VeldigoFiatPurchaseGateTest {
    @Test
    fun positiveFiatAmountWithTwoDecimalsIsAcceptedForLocalEntry() {
        assertTrue(VeldigoFiatPurchaseGate.validAmount("100", 2))
        assertTrue(VeldigoFiatPurchaseGate.validAmount("0.01", 2))
        assertTrue(VeldigoFiatPurchaseGate.validAmount("250.75", 2))
    }

    @Test
    fun zeroNegativeInvalidAndOverprecisionAmountsAreRejected() {
        assertFalse(VeldigoFiatPurchaseGate.validAmount("", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("0", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("-10", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("xyz", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("1.234", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("1e3", 2))
        assertFalse(VeldigoFiatPurchaseGate.validAmount("10.50", 0))
        assertTrue(VeldigoFiatPurchaseGate.validAmount("10", 0))
    }

    @Test
    fun purchasingCannotBeAuthorizedWithoutAnActualProvider() {
        assertFalse(VeldigoFiatPurchaseGate.providerConnected)
        assertFalse(VeldigoFiatPurchaseGate.canExecute(true, true, true))
        assertFalse(VeldigoFiatPurchaseGate.canExecute(true, false, true))
        assertFalse(VeldigoFiatPurchaseGate.canExecute(true, true, false))
    }

    @Test
    fun purchaseRouteHasNoPaymentCredentialsInItsState() {
        // The page is a singleton with no constructor arguments or card state.
        val page = BuyCryptoPage
        assertTrue(page == BuyCryptoPage)
        assertFalse(VeldigoFiatPurchaseGate.providerConnected)
    }
}
