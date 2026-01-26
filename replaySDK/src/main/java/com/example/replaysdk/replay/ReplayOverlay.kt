package com.example.replaysdk.replay

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Floating overlay controls provided by the SDK:
 * Start recording, Stop & Upload, Replay last uploaded session.
 *
 * The host app supplies onReplayEvent to map events -> app actions.
 */
@Composable
fun ReplayOverlay(
    modifier: Modifier = Modifier,
    delayMs: Long = 500,
    onReplayEvent: (Event) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var lastSessionId by remember { mutableStateOf<String?>(null) }
    var isReplaying by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        SmallFloatingActionButton(
            onClick = {
                if (!isRecording && !isReplaying) {
                    Replay.start()
                    isRecording = true
                    lastSessionId = null
                    Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                }
            },
            containerColor = if (!isRecording && !isReplaying)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) { Text("Start") }

        Spacer(Modifier.height(10.dp))

        SmallFloatingActionButton(
            onClick = {
                if (isRecording && !isReplaying) {
                    scope.launch {
                        try {
                            val id = Replay.stopAndUpload()
                            isRecording = false
                            lastSessionId = id
                            Toast.makeText(context, "Uploaded ✓ id: $id", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            isRecording = false
                            Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            containerColor = if (isRecording && !isReplaying)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) { Text("Stop") }

        Spacer(Modifier.height(10.dp))

        SmallFloatingActionButton(
            onClick = {
                val id = lastSessionId ?: run {
                    Toast.makeText(context, "No session to replay", Toast.LENGTH_SHORT).show()
                    return@SmallFloatingActionButton
                }

                if (!isRecording && !isReplaying) {
                    scope.launch {
                        try {
                            isReplaying = true
                            Toast.makeText(context, "Replay started", Toast.LENGTH_SHORT).show()

                            val session = Replay.fetch(id)
                            Replay.replay(session, delayMs = delayMs, onEvent = onReplayEvent)

                            Toast.makeText(context, "Replay finished", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Replay failed", Toast.LENGTH_SHORT).show()
                        } finally {
                            isReplaying = false
                        }
                    }
                }
            },
            containerColor = if (lastSessionId != null && !isRecording && !isReplaying)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) { Text("Replay") }
    }
}
