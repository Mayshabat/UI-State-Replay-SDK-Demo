

package com.example.replaysdk.replay

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val type: String,          // "SCREEN", "CLICK"
    val screen: String,        // "LoginScreen"
    val target: String? = null,// "login_button"
    val timestamp: Long
)
