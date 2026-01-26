package com.example.replaysdk.replay

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Modifier to record a click without replacing UI components.
 * Example:
 * Text("Buy", modifier = Modifier.replayClick("BuyText") { ... })
 */
fun Modifier.replayClick(
    tag: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
): Modifier = composed {
    this.clickable(enabled = enabled) {
        Replay.trackClick(tag)
        onClick()
    }
}
