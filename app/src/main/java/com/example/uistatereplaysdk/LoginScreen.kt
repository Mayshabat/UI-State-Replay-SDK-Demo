package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.ReplayButton

@Composable
fun LoginScreen(
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        ReplayButton(
            tag = "Login_Btn",
            onClick = onLogin
        ) {
            Text("Login")
        }
    }
}
