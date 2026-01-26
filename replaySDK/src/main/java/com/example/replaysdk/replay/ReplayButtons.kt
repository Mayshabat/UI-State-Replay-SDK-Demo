package com.example.replaysdk.replay

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Ready-to-use buttons that automatically record clicks to the Replay SDK.
 * Developers just replace Button -> ReplayButton (same idea for OutlinedButton).
 */
@Composable
fun ReplayButton(
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            Replay.trackClick(tag)
            onClick()
        }
    ) {
        content()
    }
}

@Composable
fun ReplayOutlinedButton(
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            Replay.trackClick(tag)
            onClick()
        }
    ) {
        content()
    }
}
