package io.horizontalsystems.walletkit.modules.createaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.IAccountFactory
import io.horizontalsystems.walletkit.core.IAccountManager
import io.horizontalsystems.walletkit.core.managers.PassphraseValidator
import io.horizontalsystems.walletkit.core.managers.WalletActivator
import io.horizontalsystems.walletkit.core.managers.WordsManager
import io.horizontalsystems.walletkit.core.providers.Translator
import io.horizontalsystems.walletkit.entities.Account
import io.horizontalsystems.walletkit.entities.AccountOrigin
import io.horizontalsystems.walletkit.entities.AccountType
import io.horizontalsystems.walletkit.entities.DataState
import io.horizontalsystems.walletkit.entities.normalizeNFKD
import io.horizontalsystems.walletkit.modules.createaccount.CreateAccountModule.Kind.Mnemonic12
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType

class CreateAccountViewModel(
    private val accountFactory: IAccountFactory,
    private val wordsManager: WordsManager,
    private val accountManager: IAccountManager,
    private val walletActivator: WalletActivator,
    private val passphraseValidator: PassphraseValidator,
) : ViewModel() {

    // Compose-observable, memory-only inputs. They are never put in saved
    // instance state or navigation args, and are cleared when advanced mode
    // is disabled or when the creation screen completes.
    var passphrase by mutableStateOf("")
        private set
    var passphraseConfirmation by mutableStateOf("")
        private set

    val mnemonicKinds = CreateAccountModule.Kind.values().toList()

    val defaultAccountName = accountFactory.getNextAccountName()
    var accountName by mutableStateOf(VeldigoWalletNameRules.input(accountManager.getRandomWalletName()))
        private set

    var selectedKind by mutableStateOf(Mnemonic12)
        private set

    var passphraseEnabled by mutableStateOf(false)
        private set

    var passphraseConfirmState by mutableStateOf<DataState.Error?>(null)
        private set

    var passphraseState by mutableStateOf<DataState.Error?>(null)
        private set

    var success by mutableStateOf<AccountType?>(null)
        private set

    // The exact account saved by this operation, used only for opening the real
    // recovery-phrase backup after creation; never a newly generated replacement.
    var createdAccount by mutableStateOf<Account?>(null)
        private set

    fun createAccount() {
        // Two quick taps must not generate two distinct funded-wallet identities.
        if (success != null || createdAccount != null) return
        if (passphraseEnabled && passphraseIsInvalid()) {
            return
        }

        val accountType = mnemonicAccountType(selectedKind.wordsCount)
        val account = accountFactory.account(
            VeldigoWalletNameRules.saved(accountName, defaultAccountName),
            accountType,
            AccountOrigin.Created,
            false,
            false,
        )

        accountManager.save(account)
        activateDefaultWallets(account)
        createdAccount = account
        success = accountType
    }

    fun onChangeAccountName(name: String) {
        accountName = VeldigoWalletNameRules.input(name)
    }

    fun generateRandomAccountName() {
        accountName = VeldigoWalletNameRules.input(accountManager.getRandomWalletName())
    }

    fun onChangePassphrase(v: String) {
        if (passphraseValidator.containsValidCharacters(v)) {
            passphraseState = null
            passphrase = v
        } else {
            passphraseState = DataState.Error(
                Exception(
                    Translator.getString(R.string.CreateWallet_Error_PassphraseForbiddenSymbols)
                )
            )
        }
    }

    fun onChangePassphraseConfirmation(v: String) {
        passphraseConfirmState = null
        passphraseConfirmation = v
    }

    fun setMnemonicKind(kind: CreateAccountModule.Kind) {
        selectedKind = kind
    }

    fun setAdvancedOptionsEnabled(enabled: Boolean) {
        passphraseEnabled = enabled
        if (!enabled) {
            selectedKind = Mnemonic12
            passphrase = ""
            passphraseConfirmation = ""
            passphraseState = null
            passphraseConfirmState = null
        }
    }

    fun onSuccessMessageShown() {
        // The created account has already been persisted; wipe transient
        // passphrase fields and release the screen's reference to the mnemonic.
        passphrase = ""
        passphraseConfirmation = ""
        success = null
        createdAccount = null
    }

    private fun passphraseIsInvalid(): Boolean {
        if (passphraseState is DataState.Error) {
            return true
        }
        if (passphrase.isNotBlank() && passphrase != passphraseConfirmation) {
            passphraseConfirmState = DataState.Error(
                Exception(
                    Translator.getString(R.string.CreateWallet_Error_InvalidConfirmation)
                )
            )
            return true
        }
        return false
    }

    private fun activateDefaultWallets(account: Account) {
        val tokenQueries = listOfNotNull(
            TokenQuery(BlockchainType.Bitcoin, TokenType.Derived(TokenType.Derivation.Bip84)),
            TokenQuery(BlockchainType.Ethereum, TokenType.Native),
        )
        walletActivator.activateWallets(account, tokenQueries)
    }

    private fun mnemonicAccountType(wordCount: Int): AccountType {
        // A new account can be created only using an English wordlist and limited chars in the passphrase.
        // Despite it, we add text normalizing.
        // It is to avoid potential issues if we allow non-English wordlists on account creation.
        val words = wordsManager.generateWords(wordCount).map { it.normalizeNFKD() }
        return AccountType.Mnemonic(words, passphrase.normalizeNFKD())
    }

}
