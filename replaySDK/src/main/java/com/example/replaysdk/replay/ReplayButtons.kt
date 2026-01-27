package com.example.replaysdk.replay

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReplayButton(
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier.replayHighlight(tag),
        onClick = {
            // Prevent user clicks from interfering during replay
            if (Replay.isReplaying()) return@Button

            // Record only while recording
            if (Replay.isRecording()) {
                Replay.trackClick(tag)
            }
            onClick()
        }
    ) { content() }
}

@Composable
fun ReplayOutlinedButton(
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = modifier.replayHighlight(tag),
        onClick = {
            // Prevent user clicks from interfering during replay
            if (Replay.isReplaying()) return@OutlinedButton

            // Record only while recording
            if (Replay.isRecording()) {
                Replay.trackClick(tag)
            }
            onClick()
        }
    ) { content() }
}
