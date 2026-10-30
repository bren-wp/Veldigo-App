package io.horizontalsystems.walletkit.modules.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Four-step reference sequence: Secure, All Your Crypto, Freedom, and completion.
 * The flattened concept uses mint for the current and previously visited dots.
 */
@Composable
internal fun VeldigoIntroProgress(completed: Int) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Introduction step ${completed + 1} of 4"
        },
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) { index ->
            Spacer(
                modifier = Modifier
                    .size(if (index == completed) 8.dp else 6.dp)
                    .background(
                        if (index <= completed) VeldigoWelcomeTokens.Mint
                        else androidx.compose.ui.graphics.Color(0xFF405760),
                        CircleShape,
                    )
            )
        }
    }
}
