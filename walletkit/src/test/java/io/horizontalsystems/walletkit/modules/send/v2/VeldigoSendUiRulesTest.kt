package io.horizontalsystems.walletkit.modules.send.v2

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Veldigo Send skin must not enable Continue until the existing SendForm
 * validation says the transfer is ready. This does not test or bypass signing.
 */
class VeldigoSendUiRulesTest {
    @Test fun needsAmount() {
        assertFalse(VeldigoSendUiRules.canContinue(
            SendStep.InputRequired(SendInputType.Amount)))
    }

    @Test fun needsRecipient() {
        assertFalse(VeldigoSendUiRules.canContinue(
            SendStep.InputRequired(SendInputType.Address)))
    }

    @Test fun requiredExtraFieldBlocksContinue() {
        assertFalse(VeldigoSendUiRules.canContinue(
            SendStep.InputRequired(SendInputType.Extra)))
    }

    @Test fun networkOrBalanceErrorBlocksContinue() {
        assertFalse(VeldigoSendUiRules.canContinue(
            SendStep.Error(IllegalStateException("Unavailable"))))
    }

    @Test fun validatedTransferCanContinueToExistingConfirmation() {
        assertTrue(VeldigoSendUiRules.canContinue(SendStep.Proceed))
    }
}
