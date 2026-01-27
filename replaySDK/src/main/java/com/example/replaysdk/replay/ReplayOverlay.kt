package com.example.replaysdk.replay

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun OverlayFab(
    label: String,
    active: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val containerColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        active -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        active -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    SmallFloatingActionButton(
        onClick = { if (enabled) onClick() }, // ✅ enabled handled manually
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (active) 10.dp else 4.dp,
            pressedElevation = 12.dp
        ),
        modifier = Modifier.alpha(if (enabled) 1f else 0.45f)
    ) {
        Text(label)
    }
}

@Composable
fun ReplayOverlay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // last session sources:
    // - lastLocalSession: always available after Stop, even if upload fails
    // - lastUploadedSessionId: available if upload succeeds
    var lastLocalSession by remember { mutableStateOf<Session?>(null) }
    var lastUploadedSessionId by remember { mutableStateOf<String?>(null) }

    // ✅ ticker: forces recomposition so buttons always reflect SDK state changes
    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            // we keep it always running but lightweight
            tick++
            delay(200)
        }
    }
    @Suppress("UNUSED_VARIABLE")
    val tickValue = tick

    // current SDK state (now refreshed by ticker)
    val isRecording = Replay.isRecording()
    val isReplaying = Replay.isReplaying()

    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.End
    ) {

        /**
         * START
         * - Allowed when not replaying
         * - Visible "active" while recording
         */
        OverlayFab(
            label = "Start",
            active = isRecording,
            enabled = !isReplaying, // ✅ after replay ends, this becomes true automatically
            onClick = {
                when {
                    isReplaying -> Toast.makeText(context, "Can't start while replaying", Toast.LENGTH_SHORT).show()
                    isRecording -> Toast.makeText(context, "Already recording", Toast.LENGTH_SHORT).show()
                    else -> {
                        Replay.start()
                        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        /**
         * STOP
         * - Allowed only while recording
         * - Creates local session ALWAYS
         * - Tries to upload (optional), keeps local even if upload fails
         */
        OverlayFab(
            label = "Stop",
            active = false,
            enabled = isRecording,
            onClick = {
                if (!isRecording) {
                    Toast.makeText(context, "Not recording", Toast.LENGTH_SHORT).show()
                    return@OverlayFab
                }

                scope.launch {
                    try {
                        // ✅ Always capture local session first (so replay will always work)
                        val session = Replay.stop()
                        lastLocalSession = session

                        // ✅ Try upload (if it fails, replay can still run locally)
                        try {
                            val sessionId = Replay.upload(session)
                            lastUploadedSessionId = sessionId
                            Toast.makeText(context, "Uploaded: $sessionId", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("REPLAY_SDK", "Upload failed (local session kept)", e)
                            Toast.makeText(context, "Upload failed – replay will work locally", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("REPLAY_SDK", "Stop failed", e)
                        Toast.makeText(context, "Stop failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        /**
         * REPLAY
         * - Allowed only when NOT recording
         * - Works if we have either:
         *    - uploaded session id (cloud replay)
         *    - local session (offline replay)
         * - Active while replaying
         */
        val canReplay = !isRecording && (lastUploadedSessionId != null || lastLocalSession != null)

        OverlayFab(
            label = "Replay",
            active = isReplaying,
            enabled = canReplay,
            onClick = {
                if (isRecording) {
                    Toast.makeText(context, "Stop recording first", Toast.LENGTH_SHORT).show()
                    return@OverlayFab
                }

                scope.launch {
                    try {
                        val session = when (val id = lastUploadedSessionId) {
                            null -> lastLocalSession
                            else -> Replay.fetch(id)
                        }

                        if (session == null) {
                            Toast.makeText(context, "No session to replay yet", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        Replay.replay(session)

                        // ✅ after replay ends, Start becomes clickable again automatically
                        // because isReplaying becomes false in Replay.kt
                    } catch (e: Exception) {
                        Log.e("REPLAY_SDK", "Replay failed", e)
                        Toast.makeText(context, "Replay failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        val status = when {
            isReplaying -> "REPLAYING..."
            isRecording -> "RECORDING..."
            else -> "IDLE"
        }

        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
