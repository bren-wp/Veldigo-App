package io.horizontalsystems.walletkit.modules.receive.ui

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Receiving must never display or share a fabricated, stale, or blank deposit
 * destination. Native blockchain-specific QR URI creation is tested separately.
 */
class VeldigoReceivePayloadRulesTest {
    private val address = "bc1qtestreceivingaddressisnotafundedwallet"
    private val request = "bitcoin:$address?amount=0.125"

    @Test fun cannotDisplayOrShareWithoutRealAccountData() {
        assertFalse(VeldigoReceivePayloadRules.hasValidPayload("", request))
        assertFalse(VeldigoReceivePayloadRules.hasValidPayload(address, ""))
        assertFalse(VeldigoReceivePayloadRules.hasValidPayload(" ", request))
        assertNull(VeldigoReceivePayloadRules.copyAddress(""))
        assertNull(VeldigoReceivePayloadRules.sharePayload("", request, null))
        assertNull(VeldigoReceivePayloadRules.sharePayload(address, "", BigDecimal.ONE))
    }

    @Test fun copyAlwaysContainsCompleteAddressNotTheQrPaymentRequest() {
        assertEquals(address, VeldigoReceivePayloadRules.copyAddress(address))
        assertTrue(VeldigoReceivePayloadRules.hasValidPayload(address, address))
        assertEquals(address, VeldigoReceivePayloadRules.sharePayload(address, address, null))
    }

    @Test fun paymentAmountUsesAlreadyValidatedBlockchainUri() {
        assertEquals(
            request,
            VeldigoReceivePayloadRules.sharePayload(
                address,
                request,
                BigDecimal("0.125")
            )
        )
    }

    @Test fun zeroAmountStillSharesPlainAddress() {
        assertEquals(
            address,
            VeldigoReceivePayloadRules.sharePayload(address, request, BigDecimal.ZERO)
        )
    }
}
