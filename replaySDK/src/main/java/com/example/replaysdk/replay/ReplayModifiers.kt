package com.example.replaysdk.replay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

val LocalReplayHighlightTag = compositionLocalOf<String?> { null }
// ReplayModifiers.kt מעודכן
fun Modifier.replayElement(tag: String, onClick: () -> Unit): Modifier = composed {
    val highlightKey = LocalReplayHighlightTag.current
    val isHighlighted = highlightKey == tag

    // זה הפרמטר שחסר לך כדי למנוע את החסימה
    val interactionSource = remember { MutableInteractionSource() }

    this
        .then(
            if (isHighlighted) {
                Modifier.border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            } else Modifier
        )
        .clickable(
            interactionSource = interactionSource,
            indication = null // מבטל את הריצוד שחוסם את הלחיצה האמיתית
        ) {
            // הוספת לוג לבדיקה - אם תראי את זה בלוג, סימן שהסתדרנו!
            android.util.Log.d("REPLAY_SDK", "TAP DETECTED ON: $tag")
            Replay.trackClick(tag)
            onClick() // הפעלת המעבר ב-MainActivity
        }
}