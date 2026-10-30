package io.horizontalsystems.walletkit.modules.settings.privacy.tor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TorPreferenceChangeRulesTest {
    @Test fun noOpDoesNotRequireRestart() {
        assertFalse(TorPreferenceChangeRules.requiresConfirmation(true, true))
        assertFalse(TorPreferenceChangeRules.requiresConfirmation(false, false))
    }

    @Test fun stateChangeNeedsConfirmation() {
        assertTrue(TorPreferenceChangeRules.requiresConfirmation(false, true))
        assertTrue(TorPreferenceChangeRules.requiresConfirmation(true, false))
        assertEquals(true, TorPreferenceChangeRules.confirmTarget(false, true))
        assertEquals(false, TorPreferenceChangeRules.confirmTarget(true, false))
    }

    @Test fun dismissalOrStaleRequestCannotChangePreference() {
        assertNull(TorPreferenceChangeRules.confirmTarget(false, null))
        assertNull(TorPreferenceChangeRules.confirmTarget(true, true))
        assertNull(TorPreferenceChangeRules.confirmTarget(false, false))
    }
}
