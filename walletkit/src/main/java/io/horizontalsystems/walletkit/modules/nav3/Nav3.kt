package io.horizontalsystems.walletkit.modules.nav3

import android.content.Intent
import android.view.WindowManager
import android.widget.Toast
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.util.Consumer
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.horizontalsystems.walletkit.R
import io.horizontalsystems.walletkit.core.App
import io.horizontalsystems.walletkit.helpers.HudHelper
import io.horizontalsystems.walletkit.hideKeyboard
import io.horizontalsystems.walletkit.modules.keystore.KeyStoreActivity
import io.horizontalsystems.walletkit.modules.main.MainActivityViewModel
import io.horizontalsystems.walletkit.modules.main.MainActivityViewModel.Factory
import io.horizontalsystems.walletkit.modules.main.WalletDeepLinkPolicy
import io.horizontalsystems.walletkit.modules.main.MainScreenValidationError
import io.horizontalsystems.walletkit.modules.pin.ui.PinUnlock

@Composable
fun Nav3(entryPage: HSPage) {
    val mainActivityViewModel = viewModel<MainActivityViewModel>(factory = Factory())
    // PIN unlock is required for wallet destinations; there is no public Markets tab.
    val showUnlock by App.lockGate.showUnlockFlow.collectAsState()

    val activity = LocalActivity.current

    // Kept by the view model across rotation; a new process starts a new stack (see there).
    val backStack = remember {
        mainActivityViewModel.navBackStack ?: run {
            NavBackStack<HSPage>(entryPage).also { mainActivityViewModel.navBackStack = it }
        }
    }

    val hsNavigation = remember { HSNavigation(backStack) }

    HandleNavigateToMain(mainActivityViewModel, hsNavigation)
    IntentEffect(mainActivityViewModel, hsNavigation)
    Validate(mainActivityViewModel)
    ToggleScreenshot(hsNavigation, showUnlock)
    ReportCurrentPageToLockGate(hsNavigation)

    Box {
        val eventBusNavEntryDecorator = rememberResultEventBusNavEntryDecorator<HSPage>()
        val bottomSheetStrategy = remember { BottomSheetSceneStrategy<HSPage>() }
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberSharedViewModelStoreNavEntryDecorator(),
                eventBusNavEntryDecorator,
            ),
            backStack = backStack,
            // Guarded pop: NavDisplay's default onBack removes entries without
            // protecting the root, so a racing back event can empty the stack.
            onBack = hsNavigation::removeLastOrNull,
            sceneStrategies = listOf(bottomSheetStrategy),
            entryProvider = { screen ->
                eventBusNavEntryDecorator.setResultKey(screen.resultKey)
                NavEntry(
                    key = screen,
                    contentKey = screen.contentKey(),
                    metadata = screen.getMetadata()
                ) {
                    screen.GetContent(hsNavigation)
                }
            }
        )

        AnimatedVisibility(
            visible = showUnlock,
            enter = fadeIn(),
            // Leaves immediately rather than fading. The secure flag is cleared as soon as the app
            // unlocks, so an exit animation would keep the keypad composited on a surface that is
            // capturable again. Entering is unaffected: the flag is set before the fade in starts.
            exit = ExitTransition.None
        ) {
            PinUnlock(isLocked = showUnlock)
        }
    }

    BackHandler(enabled = showUnlock) {
        if (App.lockGate.unlockRequested) {
            // Keypad was opened for a wallet action from a public screen: back just dismisses it.
            App.lockGate.cancelUnlockRequest()
        } else {
            activity?.moveTaskToBack(true)
        }
    }
}

@Composable
private fun HandleNavigateToMain(
    viewModel: MainActivityViewModel,
    navigation: HSNavigation
) {
    val navigateToMain by viewModel.navigateToMainLiveData.observeAsState()
    LaunchedEffect(navigateToMain) {
        if (navigateToMain != null) {
            navigation.removeLastUntil(EntryPage::class, false)
            viewModel.onNavigatedToMain()
        }
    }
}

@Composable
private fun IntentEffect(viewModel: MainActivityViewModel, navigation: HSNavigation) {
    val activity = LocalActivity.current
    // The launch intent is acted on once, on a fresh start. After process death the activity
    // comes back with the same intent; a pairing or payment link must not run again, and the
    // market page it may have named is already the back stack's initial entry.
    var launchIntentHandled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (launchIntentHandled) return@LaunchedEffect
        launchIntentHandled = true
        activity?.intent?.let(viewModel::setIntent)
    }
    DisposableEffect(activity) {
        val consumer = Consumer<Intent> {
            viewModel.setIntent(it)
            // Only chain-native payment requests may navigate to Send.
            // Non-payment external links are ignored by MainViewModel.
            if (it.data?.toString()?.let(WalletDeepLinkPolicy::isNativePaymentRequest) == true) {
                navigation.removeLastUntil(EntryPage::class, false)
            }
        }
        (activity as? ComponentActivity)?.addOnNewIntentListener(consumer)
        onDispose {
            (activity as? ComponentActivity)?.removeOnNewIntentListener(consumer)
        }
    }
}

@Composable
private fun Validate(viewModel: MainActivityViewModel) {
    val activity = LocalActivity.current
    LifecycleResumeEffect(Unit) {
        try {
            viewModel.validate()
        } catch (_: MainScreenValidationError.NoSystemLock) {
            activity?.let { KeyStoreActivity.startForNoSystemLock(it); it.finish() }
        } catch (_: MainScreenValidationError.KeyInvalidated) {
            activity?.let { KeyStoreActivity.startForInvalidKey(it); it.finish() }
        } catch (_: MainScreenValidationError.UserAuthentication) {
            activity?.let { KeyStoreActivity.startForUserAuthentication(it); it.finish() }
        } catch (_: MainScreenValidationError.KeystoreRuntimeException) {
            Toast.makeText(App.instance, "Issue with Keystore", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        onPauseOrDispose {}
    }
}

@Composable
private fun ReportCurrentPageToLockGate(navigation: HSNavigation) {
    val currentScreen = navigation.lastOrNull()
    LaunchedEffect(currentScreen) {
        App.lockGate.mainPageOnTop = currentScreen is EntryPage
        App.lockGate.currentPageAccessibleWhileLocked = currentScreen?.accessibleWhileLocked ?: false
    }
}

@Composable
private fun ToggleScreenshot(navigation: HSNavigation, showUnlock: Boolean) {
    val activity = LocalActivity.current
    val currentScreen = navigation.lastOrNull()
    LaunchedEffect(currentScreen, showUnlock) {
        if (activity != null) {
            activity.currentFocus?.hideKeyboard(activity)
            // The unlock keypad is drawn as an overlay rather than pushed as a page, so it has no
            // screenshotEnabled of its own. Without the lock state here the flag stays however the
            // page underneath left it, and locking over an ordinary screen leaves passcode entry
            // recordable.
            if (showUnlock || currentScreen?.screenshotEnabled == false) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                // NavDisplay keeps the outgoing page visible for its pop animation.
                // Keep FLAG_SECURE until those last frames (possibly containing seed
                // words or a PIN) have disappeared. A new protected screen cancels
                // this coroutine and leaves the flag set without a capture gap.
                delay(550)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
}
