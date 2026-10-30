package io.horizontalsystems.walletkit.modules.multiswap

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Presentation must not treat missing quotes, pending AML screening or
 * provider approval as a user-authorized swap. No network or signing occurs.
 */
class VeldigoSwapReferenceRulesTest {
    @Test fun noRateWithoutQuote() {
        assertFalse(VeldigoSwapReferenceRules.quotedRateAvailable(null))
    }

    @Test fun swapDisabledWithoutLiveProviderQuote() {
        assertFalse(VeldigoSwapReferenceRules.canProceed(
            SwapStep.Proceed, isAlreadyProceeding = false, hasQuote = false,
        ))
    }

    @Test fun pendingOrRejectedStepsCannotAdvance() {
        assertFalse(VeldigoSwapReferenceRules.canProceed(
            SwapStep.InputRequired(InputType.Amount), false, true,
        ))
        assertFalse(VeldigoSwapReferenceRules.canProceed(
            SwapStep.InputRequired(InputType.TokenOut), false, true,
        ))
        assertFalse(VeldigoSwapReferenceRules.canProceed(SwapStep.Quoting, false, true))
        assertFalse(VeldigoSwapReferenceRules.canProceed(SwapStep.Syncing, false, true))
        assertFalse(VeldigoSwapReferenceRules.canProceed(SwapStep.AmlChecking, false, true))
        assertFalse(VeldigoSwapReferenceRules.canProceed(
            SwapStep.Error(IllegalStateException("No valid route")), false, true,
        ))
    }

    @Test fun duplicateTapIsBlockedWhileProceeding() {
        assertFalse(VeldigoSwapReferenceRules.canProceed(
            SwapStep.Proceed, isAlreadyProceeding = true, hasQuote = true,
        ))
    }

    @Test fun validQuoteAndReadinessAllowOpeningExistingConfirmation() {
        assertTrue(VeldigoSwapReferenceRules.canProceed(
            SwapStep.Proceed, isAlreadyProceeding = false, hasQuote = true,
        ))
    }
}
