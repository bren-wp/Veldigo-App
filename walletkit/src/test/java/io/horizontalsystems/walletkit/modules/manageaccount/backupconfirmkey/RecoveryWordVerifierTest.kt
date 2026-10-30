package io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure assertions: no account is ever marked backed up based on a partial or
 * incorrectly ordered phrase, and duplicated recovery words are unambiguous.
 */
class RecoveryWordVerifierTest {
    @Test
    fun confirmIsBlockedUntilEveryRequiredWordWasSelected() {
        val original = (0 until 12).map { HiddenWordItem(it, "word$it", isRevealed = false) }
        assertFalse(RecoveryWordVerifier.canConfirm(emptyList()))
        assertFalse(RecoveryWordVerifier.canConfirm(original))
        assertFalse(RecoveryWordVerifier.canConfirm(original.mapIndexed { i, word ->
            word.copy(isRevealed = i != 11)
        }))
        assertTrue(RecoveryWordVerifier.canConfirm(original.map { it.copy(isRevealed = true) }))
    }

    @Test
    fun repeatedWordsHaveSeparateStableOptions() {
        val optionA = WordOption("abandon", enabled = true, sourceIndex = 0)
        val optionB = WordOption("abandon", enabled = true, sourceIndex = 1)
        val options = listOf(optionA.copy(enabled = false), optionB)
        assertEquals(-1, RecoveryWordVerifier.availableOptionIndex(options, optionA))
        assertEquals(1, RecoveryWordVerifier.availableOptionIndex(options, optionB))
        assertTrue(RecoveryWordVerifier.matches(HiddenWordItem(0, "abandon", false), optionB))
    }

    @Test
    fun wrongWordCannotAdvanceTheOrderedCheck() {
        assertFalse(
            RecoveryWordVerifier.matches(
                HiddenWordItem(4, "correct", false),
                WordOption("incorrect", enabled = true, sourceIndex = 9)
            )
        )
        assertTrue(
            RecoveryWordVerifier.matches(
                HiddenWordItem(4, "correct", false),
                WordOption("correct", enabled = true, sourceIndex = 9)
            )
        )
    }

    @Test
    fun disabledOrForgedChoiceIsNeverSelectable() {
        val valid = WordOption("ocean", enabled = true, sourceIndex = 8)
        val options = listOf(valid)
        assertEquals(-1, RecoveryWordVerifier.availableOptionIndex(options, valid.copy(enabled = false, sourceIndex = 2)))
        assertEquals(-1, RecoveryWordVerifier.availableOptionIndex(options, valid.copy(sourceIndex = 9)))
        assertEquals(-1, RecoveryWordVerifier.availableOptionIndex(options, valid.copy(word = "wrong")))
        assertEquals(0, RecoveryWordVerifier.availableOptionIndex(options, valid))
    }
}
