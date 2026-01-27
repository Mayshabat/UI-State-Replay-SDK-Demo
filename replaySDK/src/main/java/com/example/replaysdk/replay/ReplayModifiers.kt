package com.example.replaysdk.replay

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

/**
 * Highlight-only modifier.
 * No clickable here (prevents conflicts with Material Button / other click handlers).
 */
fun Modifier.replayHighlight(tag: String): Modifier = composed {
    val isHighlighted = Replay.isReplaying() && Replay.currentHighlightTag() == tag

    if (isHighlighted) {
        this.border(
            width = 4.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        )
    } else {
        this
    }
}
