package com.example.replaysdk.replay

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReplayOverlay(
    modifier: Modifier = Modifier,
    onReplayEvent: (Event) -> Unit,
    onHighlightChanged: (String?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lastSessionId by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
        // כפתור הקלטה
        SmallFloatingActionButton(
            onClick = {
                Replay.start()
                isRecording = true
                Toast.makeText(context, "Recording Started", Toast.LENGTH_SHORT).show()
            },
            containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ) { Text("Record") }

        Spacer(Modifier.height(8.dp))

        // כפתור עצירה
        SmallFloatingActionButton(
            onClick = {
                scope.launch {
                    lastSessionId = Replay.stopAndUpload()
                    isRecording = false
                    Toast.makeText(context, "Session Saved", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Text("Stop") }

        Spacer(Modifier.height(8.dp))

        // כפתור REPLAY הויזואלי
        SmallFloatingActionButton(
            onClick = {
                val id = lastSessionId ?: return@SmallFloatingActionButton
                scope.launch {
                    try {
                        val session = Replay.fetch(id)
                        val orderedEvents = Replay.eventsOf(session)

                        onReplayEvent(Event("REPLAY_START", "REPLAY", null, 0))
                        delay(500)

                        for (e in orderedEvents) {
                            if (e.type == "SCREEN") {
                                onReplayEvent(e)
                                delay(800)
                            } else if (e.type == "CLICK") {
                                onHighlightChanged(e.target) // הצגת הריבוע הכחול פיזית
                                delay(1000) // זמן צפייה בלחיצה
                                onReplayEvent(e) // ביצוע הפעולה באפליקציה
                                delay(400)
                                onHighlightChanged(null)
                            }
                        }
                        Toast.makeText(context, "REPLAY Finished", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("REPLAY_SDK", "Replay Failed", e)
                    } finally {
                        onHighlightChanged(null)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.tertiary
        ) { Text("REPLAY") }
    }
}