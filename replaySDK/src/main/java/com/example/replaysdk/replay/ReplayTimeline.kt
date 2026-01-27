package com.example.replaysdk.replay

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ReplayTimeline(
    modifier: Modifier = Modifier,
    maxItems: Int = 30
) {
    // refresh periodically so you can see events "live"
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            tick++
        }
    }

    val events = Replay.getRecordedEvents().takeLast(maxItems)

    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "Events (${events.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            if (events.isEmpty()) {
                Text("No events yet", style = MaterialTheme.typography.bodyMedium)
                return@Column
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 220.dp)
            ) {
                items(events) { e ->
                    val text = when (e.type) {
                        "SCREEN" -> "🧭 SCREEN → ${e.screen}"
                        "CLICK" -> "🖱️ CLICK → ${e.target} (on ${e.screen})"
                        else -> "${e.type} → ${e.target ?: ""}"
                    }
                    Text(text, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
