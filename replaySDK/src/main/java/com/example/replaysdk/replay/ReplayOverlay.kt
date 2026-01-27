package com.example.replaysdk.replay

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ReplayOverlay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var lastSessionId by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        SmallFloatingActionButton(
            onClick = {
                Replay.start()
                isRecording = true
                Toast.makeText(context, "Recording Started", Toast.LENGTH_SHORT).show()
            },
            containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ) { Text("Record") }

        Spacer(Modifier.height(8.dp))

        SmallFloatingActionButton(
            onClick = {
                scope.launch {
                    try {
                        lastSessionId = Replay.stopAndUpload()
                        isRecording = false
                        Toast.makeText(context, "Session Saved", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("REPLAY_SDK", "Stop&Upload failed", e)
                        Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) { Text("Stop") }

        Spacer(Modifier.height(8.dp))

        SmallFloatingActionButton(
            onClick = {
                val id = lastSessionId
                if (id == null) {
                    Toast.makeText(context, "No session yet", Toast.LENGTH_SHORT).show()
                    return@SmallFloatingActionButton
                }

                scope.launch {
                    try {
                        val session = Replay.fetch(id)
                        Replay.replay(session) // ✅ library drives navigation + highlight
                        Toast.makeText(context, "REPLAY Finished", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("REPLAY_SDK", "Replay Failed", e)
                        Toast.makeText(context, "Replay Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.tertiary
        ) { Text("REPLAY") }
    }
}
