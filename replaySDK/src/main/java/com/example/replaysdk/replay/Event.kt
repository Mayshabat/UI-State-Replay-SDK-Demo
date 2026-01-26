

package com.example.replaysdk.replay

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val type: String,      // e.g. "CLICK"
    val screen: String,    // e.g. "Login", "Open_p1"
    val timestamp: Long
)
