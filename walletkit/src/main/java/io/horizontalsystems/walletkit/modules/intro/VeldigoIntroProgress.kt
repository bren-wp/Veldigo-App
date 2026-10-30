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
 * Reference has four dots. The final panel visually restarts the sequence;
 * semanticStep preserves the correct screen-reader progress.
 * The flattened concept uses mint for the current and previously visited dots.
 */
@Composable
internal fun VeldigoIntroProgress(completed: Int, semanticStep: Int = completed + 1) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Introduction step $semanticStep of 4"
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
