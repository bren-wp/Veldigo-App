package io.horizontalsystems.walletkit.modules.manageaccount.backupkey

import org.junit.Assert.assertEquals
import org.junit.Test

class RecoveryPhraseGridRulesTest {
    @Test
    fun narrowAndStandardPhonesUseReadableTwoColumnSeedGrid() {
        assertEquals(2, RecoveryPhraseGridRules.columns(264f))
        assertEquals(2, RecoveryPhraseGridRules.columns(320f))
        assertEquals(2, RecoveryPhraseGridRules.columns(400f))
        assertEquals(2, RecoveryPhraseGridRules.columns(519f))
    }

    @Test
    fun tabletsUseThreeColumnsWithoutShrinkingWordLabels() {
        assertEquals(3, RecoveryPhraseGridRules.columns(520f))
        assertEquals(3, RecoveryPhraseGridRules.columns(840f))
    }
}
