package com.example.replaysdk.replay

import kotlinx.serialization.Serializable

/**
 * Event types (avoid magic strings across the SDK and host apps).
 */
object EventType {
    const val SCREEN = "SCREEN"
    const val CLICK = "CLICK"
}

@Serializable
data class Event(
    val type: String,           // EventType.SCREEN / EventType.CLICK
    val screen: String,         // e.g. "LoginScreen"
    val target: String? = null, // e.g. "login_button"
    val timestamp: Long
)
