package io.horizontalsystems.walletkit.modules.manageaccount.backupconfirmkey

import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.IAccountManager
import io.horizontalsystems.walletkit.core.IRandomProvider
import io.horizontalsystems.walletkit.core.ViewModelUiState
import io.horizontalsystems.walletkit.core.providers.Translator
import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.entities.AccountType

/**
 * Verifies the full phrase in its original order.
 *
 * Selecting words never marks the account backed up. Only the separate,
 * explicitly enabled Confirm action can persist backup verification.
 * A stable source index distinguishes duplicate BIP39 words.
 */
class BackupConfirmKeyViewModel(
    private val account: Account,
    private val accountManager: IAccountManager,
    @Suppress("UNUSED_PARAMETER") randomProvider: IRandomProvider,
) : ViewModelUiState<BackupConfirmUiState>() {

    private val wordsIndexed: List<Pair<Int, String>>
    private var hiddenWordItems = listOf<HiddenWordItem>()
    private var wordOptions = listOf<WordOption>()
    private var currentHiddenWordItemIndex = 0
    private var readyToConfirm = false
    private var confirmed = false
    private var error: Throwable? = null

    init {
        val words = when (val type = account.type) {
            is AccountType.Mnemonic -> type.words
            is AccountType.MoneroMnemonic -> type.words
            else -> emptyList()
        }
        wordsIndexed = words.mapIndexed { index, word -> index to word }
        resetSelection()
        emitState()
    }

    override fun createState() = BackupConfirmUiState(
        hiddenWordItems = hiddenWordItems,
        wordOptions = wordOptions,
        currentHiddenWordItemIndex = currentHiddenWordItemIndex,
        readyToConfirm = readyToConfirm,
        confirmed = confirmed,
        error = error,
    )

    private fun resetSelection() {
        // All source words are required, even with repeated words or 15–25-word
        // accounts. A shuffled option never changes the saved phrase.
        hiddenWordItems = wordsIndexed.map { (index, word) ->
            HiddenWordItem(index, word, isRevealed = false)
        }
        wordOptions = wordsIndexed.shuffled().map { (index, word) ->
            WordOption(word, enabled = true, sourceIndex = index)
        }
        currentHiddenWordItemIndex = 0
        readyToConfirm = false
    }

    fun onSelectWord(option: WordOption) {
        if (confirmed || readyToConfirm || currentHiddenWordItemIndex !in hiddenWordItems.indices) {
            return
        }
        // Identical words can occur several times in a valid mnemonic.
        // Match each chip by its unique source index to disable only that chip.
        val optionIndex = RecoveryWordVerifier.availableOptionIndex(wordOptions, option)
        if (optionIndex == -1) return

        val expected = hiddenWordItems[currentHiddenWordItemIndex]
        if (!RecoveryWordVerifier.matches(expected, option)) {
            resetSelection()
            error = Exception(Translator.getString(R.string.BackupConfirmKey_Error_InvalidWord))
        } else {
            hiddenWordItems = hiddenWordItems.toMutableList().apply {
                set(currentHiddenWordItemIndex, expected.copy(isRevealed = true))
            }
            wordOptions = wordOptions.toMutableList().apply {
                set(optionIndex, option.copy(enabled = false))
            }
            currentHiddenWordItemIndex += 1
            readyToConfirm = currentHiddenWordItemIndex == hiddenWordItems.size &&
                RecoveryWordVerifier.canConfirm(hiddenWordItems)
        }
        emitState()
    }

    fun confirmBackup() {
        if (confirmed || !readyToConfirm || hiddenWordItems.isEmpty()) return
        // Explicitly verify the whole original phrase once more before storage.
        if (!RecoveryWordVerifier.canConfirm(hiddenWordItems)) return
        accountManager.update(account.copy(isBackedUp = true))
        confirmed = true
        emitState()
    }

    fun clearSelectionOnBackground() {
        if (confirmed) return
        resetSelection()
        error = null
        emitState()
    }

    fun onErrorShown() {
        error = null
        emitState()
    }
}

data class BackupConfirmUiState(
    val hiddenWordItems: List<HiddenWordItem>,
    val wordOptions: List<WordOption>,
    val currentHiddenWordItemIndex: Int,
    val readyToConfirm: Boolean,
    val confirmed: Boolean,
    val error: Throwable?,
)

data class WordOption(val word: String, val enabled: Boolean, val sourceIndex: Int = -1)

data class HiddenWordItem(
    val index: Int,
    val word: String,
    val isRevealed: Boolean,
) {
    override fun toString() = if (isRevealed) "${index + 1}. $word" else "${index + 1}."
}

/** Pure security-critical predicates, independently unit-tested without UI or Android state. */
internal object RecoveryWordVerifier {
    fun availableOptionIndex(options: List<WordOption>, selected: WordOption): Int =
        options.indexOfFirst {
            it.sourceIndex == selected.sourceIndex && it.word == selected.word && it.enabled
        }

    fun matches(expected: HiddenWordItem, selected: WordOption): Boolean =
        expected.word == selected.word

    fun canConfirm(words: List<HiddenWordItem>): Boolean =
        words.isNotEmpty() && words.all { it.isRevealed }
}
